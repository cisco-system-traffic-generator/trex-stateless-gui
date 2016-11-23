/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.views.importPcap;

import com.exalttech.trex.ui.views.streams.builder.IPV4Type;
import com.exalttech.trex.util.Util;
import java.util.function.UnaryOperator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import org.apache.log4j.Logger;

/**
 * Imported packet properties view implementation
 * @author GeorgeKH
 */
public class ImportedPacketPropertiesView extends AnchorPane {

    private static final Logger LOG = Logger.getLogger(ImportedPacketPropertiesView.class.getName());

    @FXML
    CheckBox dstEnabledCB;
    @FXML
    CheckBox srcEnabledCB;
    @FXML
    TextField dstAddressTF;
    @FXML
    TextField srcAddressTF;
    @FXML
    TextField dstCountTF;
    @FXML
    TextField srcCountTF;
    @FXML
    ComboBox dstModeCB;
    @FXML
    ComboBox srcModeCB;

    @FXML
    RadioButton speedupRB;
    @FXML
    TextField speedupTF;
    @FXML
    RadioButton ipgRB;
    @FXML
    TextField ipgTF;

    @FXML
    TextField countTF;

    ImportedPacketProperties propertiesBinder;

    /**
     * Constructor
     */
    public ImportedPacketPropertiesView() {
        loadFXML();
        init();
        bindProperties();
        addInputValidation();
    }

    /**
     * Load Fxml UI implementation
     */
    private void loadFXML() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ImportedPacketPropertiesView.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();

        } catch (Exception ex) {
            LOG.error("Error setting UI", ex);
        }
    }

    /**
     * Initialize lists
     */
    private void init() {
        for (IPV4Type type : IPV4Type.values()) {
            srcModeCB.getItems().add(type.getTitle());
            dstModeCB.getItems().add(type.getTitle());
        }
    }

    /**
     * Bind properties together
     */
    private void bindProperties() {
        dstAddressTF.disableProperty().bind(dstEnabledCB.selectedProperty().not());
        dstCountTF.disableProperty().bind(dstEnabledCB.selectedProperty().not().or(dstModeCB.valueProperty().isEqualTo("Fixed")));
        dstModeCB.disableProperty().bind(dstEnabledCB.selectedProperty().not());

        srcAddressTF.disableProperty().bind(srcEnabledCB.selectedProperty().not());
        srcCountTF.disableProperty().bind(srcEnabledCB.selectedProperty().not().or(srcModeCB.valueProperty().isEqualTo("Fixed")));
        srcModeCB.disableProperty().bind(srcEnabledCB.selectedProperty().not());

        speedupTF.disableProperty().bind(speedupRB.selectedProperty().not());
        ipgTF.disableProperty().bind(ipgRB.selectedProperty().not());
    }

    /**
     * Set properties binder model
     *
     * @param propertiesBinder
     */
    public void setPropertiesBinder(ImportedPacketProperties propertiesBinder) {
        this.propertiesBinder = propertiesBinder;
        bindPropertiesWithFields();
    }

    /**
     * Bind properties binder model with fields
     */
    private void bindPropertiesWithFields() {
        dstEnabledCB.selectedProperty().bindBidirectional(propertiesBinder.getEnabledDstProperty());
        dstAddressTF.textProperty().bindBidirectional(propertiesBinder.getDstAddressProperty());
        dstCountTF.textProperty().bindBidirectional(propertiesBinder.getDstCountProperty());
        dstModeCB.valueProperty().bindBidirectional(propertiesBinder.getDstModeProperty());

        srcEnabledCB.selectedProperty().bindBidirectional(propertiesBinder.getEnabledSrcProperty());
        srcAddressTF.textProperty().bindBidirectional(propertiesBinder.getSrcAddressProperty());
        srcCountTF.textProperty().bindBidirectional(propertiesBinder.getSrcCountProperty());
        srcModeCB.valueProperty().bindBidirectional(propertiesBinder.getSrcModeProperty());

        speedupTF.textProperty().bindBidirectional(propertiesBinder.getSpeedupProperty());
        ipgTF.textProperty().bindBidirectional(propertiesBinder.getIpgProperty());
        ipgRB.selectedProperty().bindBidirectional(propertiesBinder.getIpgSelectionProperty());

        countTF.textProperty().bindBidirectional(propertiesBinder.getCountProperty());
    }

    /**
     * Add input formatter instructions
     */
    private void addInputValidation() {
        UnaryOperator<TextFormatter.Change> unitFormatter = Util.getTextChangeFormatter(Util.getUnitRegex(false));
        srcCountTF.setTextFormatter(new TextFormatter<>(unitFormatter));
        dstCountTF.setTextFormatter(new TextFormatter<>(unitFormatter));

        countTF.setTextFormatter(Util.getNumberFilter(5));

        UnaryOperator<TextFormatter.Change> digitsFormatter = Util.getTextChangeFormatter(digitsRegex());
        speedupTF.setTextFormatter(new TextFormatter<>(digitsFormatter));
        ipgTF.setTextFormatter(new TextFormatter<>(digitsFormatter));

    }

    /**
     * Return digit regex 
     * @return 
     */
    private String digitsRegex() {
        String partialBlock = "(([0-9]{0,10}))(\\.){0,4}[0-9]";

        String testField = partialBlock + "{0,4}";
        return "^" + testField;
    }

    /**
     * Return true if all input fields value is valid, otherwise return false
     * @return 
     */
    public boolean isValidInputValues() {
        return (isValidAddresses() && validateCount());
    }

    /**
     * Validate IP source/destination addresses
     * @return 
     */
    private boolean isValidAddresses() {
        boolean valid = true;
        Alert errorMsg = Util.getAlert(Alert.AlertType.ERROR);
        if (srcEnabledCB.isSelected() && !Util.isValidIPAddress(srcAddressTF.getText())) {
            errorMsg.setContentText("Invalid source IP address");
            valid = false;
        } else if (dstEnabledCB.isSelected() && !Util.isValidIPAddress(dstAddressTF.getText())) {
            errorMsg.setContentText("Invalid destination IP address");
            valid = false;
        }
        if (!valid) {
            errorMsg.showAndWait();
        }
        return valid;
    }

    /**
     * Validate source/destination count value
     * @return 
     */
    private boolean validateCount() {
        boolean valid = true;
        Alert errorMsg = Util.getAlert(Alert.AlertType.ERROR);
        if (srcEnabledCB.isSelected() && !isValidCount(srcCountTF.getText())) {
            errorMsg.setContentText("Source count should be between 2 - 100M ");
            valid = false;
        } else if (dstEnabledCB.isSelected() && !isValidCount(dstCountTF.getText())) {
            errorMsg.setContentText("Destination count should be between 2 - 100M ");
            valid = false;
        }
        if (!valid) {
            errorMsg.showAndWait();
        }
        return valid;
    }

    /**
     * Check count range, return true if it is within the range
     * otherwise return false
     * @param countValue
     * @return 
     */
    private boolean isValidCount(String countValue) {
        double countVal = Util.convertUnitToNum(countValue);
        if (countVal > Util.convertUnitToNum("100M") || countVal < 2) {

            return false;
        }
        return true;
    }
}
