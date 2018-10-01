package com.exalttech.trex.ui.controllers.daemon;

import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.ui.dialog.DialogView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class InterfaceSelectDialogController extends DialogView implements Initializable {
    @FXML
    public ComboBox<InterfaceInfo> interfacesComboBox;

    @FXML
    public Button selectButton;

    @FXML
    public TableView<Map.Entry<String, String>> propertiesTableView;

    @FXML
    public TableColumn<Map.Entry<String, String>, String> propertyNameColumn;

    @FXML
    public TableColumn<Map.Entry<String, String>, String> propertyValueColumn;

    private Map<String, InterfaceInfo> interfacesInfo;

    @Override
    public void onEnterKeyPressed(Stage stage) {

    }

    public String getSelectedString() {
        return interfacesComboBox.getSelectionModel().getSelectedItem().Slot_str;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initInterfacesInfo();
        initTableView();
        initComboBox();
    }

    private void initInterfacesInfo() {
        interfacesInfo = TrexApp.injector.getInstance(InterfaceInfoProvider.class).getInterfacesInfo();

        InterfaceInfo dummy = new InterfaceInfo("dummy");
        dummy.setProperty("Info", "Dummy interface");
        interfacesInfo.put("dummy", dummy);
    }

    private void initTableView() {
        propertyNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
        propertyValueColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));
    }

    private void initComboBox() {
        interfacesComboBox.setItems(FXCollections.observableList(new ArrayList<>(interfacesInfo.values())));
        interfacesComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            propertiesTableView.setItems(FXCollections.observableList(new ArrayList<>(newValue.getProperties().entrySet())));
        });

        interfacesComboBox.setConverter(new StringConverter<InterfaceInfo>() {
            @Override
            public String toString(InterfaceInfo object) {
                return object.Slot_str;
            }

            @Override
            public InterfaceInfo fromString(String string) {
                return null;
            }
        });
        interfacesComboBox.getSelectionModel().select(0);
    }

    public void handleSelectClicked(ActionEvent actionEvent) {
        onEscapKeyPressed();
    }
}
