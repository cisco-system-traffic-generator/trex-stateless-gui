package com.exalttech.trex.ui.controllers.daemon;

import com.google.common.net.InetAddresses;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class ConfigNode {
    @FunctionalInterface
    public interface Validator {
        /**
         * Validates given value. If doesn't throw then it's value is valid
         * @param value
         * @throws Exception
         */
        void validate(Object value) throws Exception;
    }

    private MetaField meta;

    private ObservableList<ConfigNode> children = FXCollections.observableArrayList();
    private ConfigNode parent;

    private boolean removable;
    private SimpleBooleanProperty mandatoryProperty = new SimpleBooleanProperty();

    private Object value;

    private Map<MetaField.Type, Validator> validationMap = new HashMap<>();


    public ConfigNode(MetaField meta, ConfigNode parent, boolean removable) {
        this.removable = removable;
        this.parent = parent;
        this.meta = meta;

        initValidator();

        if (getType() == MetaField.Type.OBJECT) {
            this.meta.attributes.forEach(metaField -> {
                ConfigNode child = new ConfigNode(metaField, this);
                this.children.add(child);
            });
        }

        mandatoryProperty.setValue(isMandatory());

    }

    public ConfigNode(MetaField meta, ConfigNode parent) {
        this(meta, parent, false);
    }

    public ConfigNode(MetaField meta) {
        this(meta, null, false);
    }

    private void initValidator() {
        validationMap.put(MetaField.Type.STRING, val -> { String casted = (String) value; });
        validationMap.put(MetaField.Type.NUMBER, val -> Integer.parseInt((String) value));
        validationMap.put(MetaField.Type.FLOAT, val -> Float.parseFloat((String) value));
        validationMap.put(MetaField.Type.BOOLEAN, val -> { boolean casted = (boolean) value; });
        validationMap.put(MetaField.Type.IP, val -> InetAddresses.forString((String) value));
        validationMap.put(MetaField.Type.MAC, val -> {
            if (!Pattern.matches("^([0-9a-fA-F]{2}:){5}([0-9a-fA-F]{2})$", (String) value)) {
                throw new Exception();
            }
        });

        validationMap.put(MetaField.Type.LIST, val -> {
            for (ConfigNode node : children) {
                if (!node.validateValue()) {
                    throw new Exception();
                }
            }
        });

        validationMap.put(MetaField.Type.OBJECT, val -> {
            for (ConfigNode node : children) {
                if (!node.validateValue()) {
                    throw new Exception();
                }
            }
        });
    }

    public MetaField.Type getType() {
        return meta.type;
    }

    public boolean isMandatory() {
        if (meta.mandatory_if_not_set == null) {
            return meta.mandatory;
        } else {
            return !isDependencySpecified();
        }
    }

    private boolean isDependencySpecified() {
        return parent.getChildren()
                .stream()
                .filter(sibling -> sibling.getId().equals(meta.mandatory_if_not_set))
                .anyMatch(sibling -> sibling.getValue() != null);
    }

    public ObservableBooleanValue mandatoryProperty() {
        return mandatoryProperty;
    }

    public String getDefaultValue() {
        return meta.default_value;
    }

    public String getName() {
        return meta.name;
    }

    public String getDescription() {
        return meta.description;
    }

    public String getId() {
        return meta.id;
    }

    public void setValue(Object newValue) {
        if (getType() == MetaField.Type.LIST) {
            List<Object> newChildren = ((List<Object>) newValue);
            newChildren.forEach(x -> {
                ConfigNode configNode = addListItem();
                configNode.setValue(x);
            });
        } else if (getType() == MetaField.Type.OBJECT){
            Map<String, Object> newMap = (Map<String, Object>) newValue;
            for (Map.Entry<String, Object> entry : newMap.entrySet()) {
                children.stream()
                        .filter(x -> x.getId().equals(entry.getKey()))
                        .findFirst()
                        .ifPresent(x -> x.setValue(entry.getValue()));
            }
        } else {
            value = newValue;
        }

        if (getParent() != null) {
            getParent().updateDependencies();
        }
    }

    public Object getValue() {
        if (getType() == MetaField.Type.LIST) {
            List<Object> childrenValues = getChildren().stream()
                    .map(ConfigNode::getValue)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (childrenValues.isEmpty()) {
                return null;
            }
            return childrenValues;

        } else if (getType() == MetaField.Type.OBJECT) {
            Map<String, Object> childrenMap = new HashMap<>();
            getChildren().stream()
                    .filter(field -> field.getValue() != null)
                    .forEach(field -> childrenMap.put(field.meta.id, field.getValue()));

            if (childrenMap.isEmpty()) {
                return null;
            }
            return childrenMap;

        } else {
            return value;
        }
    }

    private void updateDependencies() {
        getChildren().forEach(x -> x.mandatoryProperty.setValue(x.isMandatory()));
    }

    public boolean validateValue() {
        if (getValue() == null) {
            return !isMandatory();
        }

        try {
            validationMap.get(getType()).validate(value);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();
        for (ConfigNode node : this.children) {
            errors.addAll(node.getValidationErrors());
        }

        if (errors.isEmpty()) {
            String idDescription = getIdDescription();

            if (getValue() == null && isMandatory()) {
                errors.add(MessageFormat.format("Field {0} ({1}) is mandatory, it must be specified",
                        getName(),
                        idDescription
                ));
            } else if (!validateValue()) {
                errors.add(MessageFormat.format("Field {0} ({1}) equals \"{2}\", and cannot be parsed as {3}",
                        getName(),
                        idDescription,
                        value,
                        getType().toString()
                ));
            }
        }

        return errors;
    }

    private String getIdDescription() {
        if (getParent() != null) {
            if (getParent().getType() == MetaField.Type.LIST) {
                return getParent().getIdDescription() + "[" + getParent().getChildren().indexOf(this) + "]";
            } else {
                return getParent().getIdDescription() + "." + getId();
            }
        } else {
            return getId();
        }
    }

    public ConfigNode getParent() {
        return parent;
    }

    public ObservableList<ConfigNode> getChildren() {
        return children;
    }

    public ConfigNode addListItem() {
        ConfigNode newChild = new ConfigNode(meta.item, this, true);
        children.add(newChild);
        return newChild;
    }

    public boolean isRemovable() {
        return removable;
    }
}
