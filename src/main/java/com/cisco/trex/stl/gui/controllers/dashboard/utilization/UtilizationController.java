package com.cisco.trex.stl.gui.controllers.dashboard.utilization;

import com.cisco.trex.stl.gui.models.MemoryUtilizationModel;
import com.cisco.trex.stl.gui.models.UtilizationCPUModel;
import com.cisco.trex.stl.gui.storages.StatsStorage;
import com.cisco.trex.stl.gui.storages.UtilizationStorage;
import com.exalttech.trex.util.Initialization;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;

import java.util.Iterator;
import java.util.List;


public class UtilizationController extends AnchorPane {
    @FXML
    private AnchorPane root;
    
    @FXML
    private ToggleGroup toggleGroupMode;
    
    @FXML
    private BorderPane cpuUtil;
    
    @FXML
    private TableView<UtilizationCPUModel> cpuUtilTable;
    
    private boolean cpuUtilTableInitialized = false;
    
    @FXML
    private BorderPane memoryUtil;
    
    @FXML
    private TableView<MemoryUtilizationModel> memoryUtilTable;

    @FXML
    private TableColumn<MemoryUtilizationModel, String> memTableTitle;
    
    @FXML
    private TableColumn<MemoryUtilizationModel, String> memTable64b;
    
    @FXML
    private TableColumn<MemoryUtilizationModel, String> memTable128b;
    
    @FXML
    private TableColumn<MemoryUtilizationModel, String> memTable256b;
    
    @FXML
    private TableColumn<MemoryUtilizationModel, String> memTable512b;
    
    @FXML
    private TableColumn<MemoryUtilizationModel, String> memTable1024b;
    
    @FXML
    private TableColumn<MemoryUtilizationModel, String> memTable2048b;
    
    @FXML
    private TableColumn<MemoryUtilizationModel, String> memTable4096b;
    
    @FXML
    private TableColumn<MemoryUtilizationModel, String> memTable9kb;
    
    @FXML
    private TableColumn<MemoryUtilizationModel, String> memTableRam;
    
    private boolean memoryUtilTableInitialized = false;

    private boolean isActive = false;
    private UtilizationStorage.UtilizationChangedListener utilizationChangedListener = this::render;

    public UtilizationController() {
        Initialization.initializeFXML(this, "/fxml/dashboard/utilization/Utilization.fxml");
        Initialization.initializeCloseEvent(root, this::onWindowCloseRequest);

        toggleGroupMode.selectedToggleProperty().addListener(this::typeChanged);

        memTableTitle.setCellValueFactory((cellData -> cellData.getValue().titleProperty()));
        memTable64b.setCellValueFactory((cellData -> cellData.getValue().bank64bProperty()));
        memTable128b.setCellValueFactory((cellData -> cellData.getValue().bank128bProperty()));
        memTable256b.setCellValueFactory((cellData -> cellData.getValue().bank256bProperty()));
        memTable512b.setCellValueFactory((cellData -> cellData.getValue().bank512bProperty()));
        memTable1024b.setCellValueFactory((cellData -> cellData.getValue().bank1024bProperty()));
        memTable2048b.setCellValueFactory((cellData -> cellData.getValue().bank2048bProperty()));
        memTable4096b.setCellValueFactory((cellData -> cellData.getValue().bank4096bProperty()));
        memTable9kb.setCellValueFactory((cellData -> cellData.getValue().bank9kbProperty()));
        memTableRam.setCellValueFactory((cellData -> cellData.getValue().ramProperty()));
    }

    public void setActive(final boolean isActive) {
        if (this.isActive == isActive) {
            return;
        }

        this.isActive = isActive;
        final UtilizationStorage utilizationStorage = StatsStorage.getInstance().getUtilizationStorage();
        if (this.isActive) {
            utilizationStorage.addUtilizationChangedListener(utilizationChangedListener);
            render();
        } else {
            utilizationStorage.removeUtilizationChangedListener(utilizationChangedListener);
        }
    }

    private void onWindowCloseRequest(final WindowEvent window) {
        setActive(false);
    }

    private void typeChanged(
            final ObservableValue<? extends Toggle> observable,
            final Toggle oldValue,
            final Toggle newValue
    ) {
        if (newValue == null) {
            oldValue.setSelected(true);
        } else {
            render();
        }
    }

    private void render() {
        if (((ToggleButton)toggleGroupMode.getSelectedToggle()).getText().equals("CPU")) {
            memoryUtil.setVisible(false);
            cpuUtil.setVisible(true);
            renderCPU();
        } else {
            cpuUtil.setVisible(false);
            memoryUtil.setVisible(true);
            renderMbuf();
        }
    }

    private void renderCPU() {
        UtilizationStorage utilizationStorage = StatsStorage.getInstance().getUtilizationStorage();
        synchronized (utilizationStorage.getDataLock()) {
            List<UtilizationCPUModel> cpuUtilsModels = utilizationStorage.getCpuUtilsModels();
            if (!cpuUtilTableInitialized) {
                initCPUUtilTable(cpuUtilsModels);
                cpuUtilTableInitialized = true;
            }
            cpuUtilTable.getItems().clear();
            cpuUtilTable.getItems().addAll(cpuUtilsModels);
            
        }
    }

    private void initCPUUtilTable(List<UtilizationCPUModel> cpuUtilsModels) {
        if (cpuUtilsModels.isEmpty()) {
            return;
        }
        UtilizationCPUModel model = cpuUtilsModels.get(0);
        
        TableColumn<UtilizationCPUModel, String> threadColumn = new TableColumn<>();
        threadColumn.setText("Thread");
        threadColumn.setCellValueFactory(cellData -> cellData.getValue().threadProperty());

        TableColumn<UtilizationCPUModel, String> avgColumn = new TableColumn<>();
        avgColumn.setText("Avg");
        avgColumn.setCellValueFactory(cellData -> cellData.getValue().avgProperty().asString());

        Iterator<IntegerProperty> iterator = model.getHistory().iterator();
        iterator.next();
        
        TableColumn<UtilizationCPUModel, String> latestColumn = new TableColumn<>();
        latestColumn.setText("Latest");
        latestColumn.setCellValueFactory(cellData -> cellData.getValue().getHistory(0).asString());

        cpuUtilTable.getColumns().addAll(threadColumn, avgColumn, latestColumn);
        
        for(int i = 1; i < model.getHistory().size(); i++) {
            final int idx = i;
            TableColumn<UtilizationCPUModel, String> historyColumn = new TableColumn<>();
            historyColumn.setText(String.valueOf(-idx));
            historyColumn.setCellValueFactory(cellData -> cellData.getValue().getHistory(idx).asString());
            cpuUtilTable.getColumns().add(historyColumn);
        }
    }

    private void renderMbuf() {
        UtilizationStorage utilizationStorage = StatsStorage.getInstance().getUtilizationStorage();
        synchronized (utilizationStorage.getDataLock()) {
            List<MemoryUtilizationModel> memUtilsModels = utilizationStorage.getMemUtilsModels();
            memoryUtilTable.getItems().clear();
            memoryUtilTable.getItems().addAll(memUtilsModels);
        }
    }
}
