/**
 * *****************************************************************************
 * Copyright (c) 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************
 */
package com.exalttech.trex.ui.views.streams.viewer;

import com.exalttech.trex.ui.models.PacketInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.logging.Level;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javax.xml.bind.DatatypeConverter;
import org.apache.log4j.Logger;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IllegalRawDataException;

/**
 * Displaying packet hex view implementation
 *
 * @author GeorgeKh
 */
public class PacketHex {
    
    private static final Logger LOG = Logger.getLogger(PacketHex.class.getName());
    
    private PacketInfo packetInfo = null;
    private AnchorPane hex;
    private VBox packetView = null;
    PacketParser packetParser;
    final TreeItem<HexData> treeRoot = new TreeItem<>(new HexData("Packet", "", ""));
    TreeTableView<HexData> hexTable;

    /**
     *
     * @param hexPane
     */
    public PacketHex(AnchorPane hexPane) {
        
        this.hex = hexPane;
        
        packetParser = new PacketParser();
        this.packetView = buildPacketView();
        packetView.setStyle("-fx-background-color: white;");
        hex.setStyle("-fx-background-color: white;");
        AnchorPane.setLeftAnchor(packetView, 0.0);
        AnchorPane.setBottomAnchor(packetView, 0.0);
        AnchorPane.setRightAnchor(packetView, 0.0);
        AnchorPane.setTopAnchor(packetView, 0.0);
        hex.getChildren().add(packetView);
    }
    
    /**
     * Set data
     * @param packet 
     */
    public void setData(PacketInfo packet) {
        this.packetInfo = packet;
        treeRoot.getChildren().clear();
        
        treeRoot.setExpanded(true);
        
        if (packet.getPacketHex() != null) {
            treeRoot.getChildren().add(buildPacketHexView(packet.getPacketHex(), packet.getPacketRawData(), "Packet Hex"));
            treeRoot.setExpanded(true);
            
        }
        if (packet.getEthernetHex() != null) {
            treeRoot.getChildren().add(buildPacketHexView(packet.getEthernetHex(), packet.getEthernetRawData(), "L2 Ethernet"));
        }
        if (packet.getIpv4Hex() != null) {
            treeRoot.getChildren().add(buildPacketHexView(packet.getIpv4Hex(), packet.getIpv4RawData(), "L3 IP"));
        }
        if (packet.getL4Hex() != null) {
            treeRoot.getChildren().add(buildPacketHexView(packet.getL4Hex(), packet.getL4RawData(), "L4 " + packet.getL4Name()));
        }
        
        if (packet.getPacketPayLoad() != null) {
            treeRoot.getChildren().add(buildPacketHexView(packet.getPacketPayLoad(), new String(), "L7"));
        }
        if (!treeRoot.getChildren().isEmpty()) {
            hexTable.setRoot(treeRoot);
        }
    }

    /**
     * Build packet view
     *
     * @param packet
     * @return
     */
    private VBox buildPacketView() {
        VBox insidePane = new VBox();
        
        TreeTableColumn<HexData, String> c0 = new TreeTableColumn<>("offst");
        c0.setCellValueFactory((TreeTableColumn.CellDataFeatures<HexData, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getOffset()));
        c0.setSortable(false);
        c0.setPrefWidth(120);
        
        TreeTableColumn<HexData, String> c1 = new TreeTableColumn<>("Hex");
        c1.setCellValueFactory((TreeTableColumn.CellDataFeatures<HexData, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getHex()));
        c1.setSortable(false);
        c1.setPrefWidth(375);
        c1.setCellFactory(new TextFieldCellFactory());
        
        TreeTableColumn<HexData, String> c2 = new TreeTableColumn<>("Payload");
        c2.setCellValueFactory((TreeTableColumn.CellDataFeatures<HexData, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getPayLoad()));
        c2.setSortable(false);
        c2.setPrefWidth(130);
        
        hexTable = new TreeTableView<>();
        hexTable.getStyleClass().add("treeTable");
        hexTable.getColumns().addAll(c0, c1, c2);
        hexTable.setPrefSize(630, 510);
        insidePane.getChildren().add(hexTable);
        
        return insidePane;
    }

    /**
     * Build packet hex view
     *
     * @param hexString
     * @param payLoadString
     * @param headerName
     * @return
     */
    private TreeItem<HexData> buildPacketHexView(String hexString, String payLoadString, String headerName) {
        final TreeItem<HexData> root = new TreeItem<>(new HexData(headerName, "", ""));
        root.setExpanded(true);
        ObservableList<HexData> hexList = prepareHexData(hexString, payLoadString);
        hexList.stream().forEach(line -> {
            root.getChildren().add(new TreeItem<>(new HexData(line.getOffset(), line.getHex(), line.getPayLoad())));
        });
        
        return root;
    }

    /**
     * Return packet hex from list
     *
     * @return
     */
    public String getPacketHexFromList() {
        StringBuilder hexStringBuffer = new StringBuilder();
        ListIterator<TreeItem<HexData>> arr = treeRoot.getChildren().get(0).getChildren().listIterator();
        for (ListIterator<TreeItem<HexData>> i = arr; i.hasNext();) {
            TreeItem<HexData> item = i.next();
            hexStringBuffer.append(item.getValue().getHex()).append(' ');
        }
        return hexStringBuffer.toString().replaceAll(" ", "").replaceAll("\n", "");
    }

    /**
     * Prepare hex data
     *
     * @param hexString
     * @param payLoadString
     * @return
     */
    private ObservableList<HexData> prepareHexData(String hexString, String payLoadString) {
        String[] hexArr = hexString.split("\r\n|\r|\n");
        String[] payLoadArr = payLoadString.split("\r\n|\r|\n");
        List<HexData> lis = new ArrayList<>();
        for (int i = 0; i < hexArr.length; i++) {
            try {
                String offset = hexArr[i].substring(0, hexArr[i].indexOf(" "));
                String payloadString="";
                if(payLoadArr.length > i){
                    payloadString = payLoadArr[i];
                }
                lis.add(new HexData(offset, hexArr[i].replace(offset + " ", ""), payloadString));
            } catch (Exception e) {
                LOG.error("Failed to find index", e);
            }
        }
        return FXCollections.observableArrayList(lis);
    }

    /**
     * Model present hex data
     */
    public class HexData {
        
        private SimpleStringProperty hex;
        private SimpleStringProperty offset;
        private SimpleStringProperty payLoad;
        private ObjectProperty<SimpleStringProperty> hexObject;

        /**
         *
         * @param offset
         * @param hex
         * @param payLoad
         */
        public HexData(String offset, String hex, String payLoad) {
            this.hex = new SimpleStringProperty(hex);
            this.offset = new SimpleStringProperty(offset);
            this.payLoad = new SimpleStringProperty(payLoad);
        }

        /**
         *
         * @return
         */
        public String getOffset() {
            return offset.get();
        }

        /**
         *
         * @param offset
         */
        public void setOffset(String offset) {
            this.offset.set(offset);
        }

        /**
         *
         * @return
         */
        public SimpleStringProperty offsetProperty() {
            return offset;
        }

        /**
         *
         * @return
         */
        public String getPayLoad() {
            return payLoad.get();
        }

        /**
         *
         * @param payLoad
         */
        public void setPayLoad(String payLoad) {
            this.payLoad.set(payLoad);
        }

        /**
         *
         * @return
         */
        public SimpleStringProperty payLoadProperty() {
            return payLoad;
        }

        /**
         *
         * @return
         */
        public String getHex() {
            return hex.get();
        }

        /**
         *
         * @return
         */
        public SimpleStringProperty getHexObject() {
            return hexObject.get();
        }

        /**
         *
         * @param hexObject
         */
        public void setHexObject(SimpleStringProperty hexObject) {
            this.hexObject.set(hexObject);
        }

        /**
         *
         * @return
         */
        public ObjectProperty<SimpleStringProperty> hexObjectProperty() {
            return hexObject;
        }

        /**
         *
         * @param hex
         */
        public void setHex(String hex) {
            this.hex.set(hex);
        }

        /**
         *
         * @return
         */
        public SimpleStringProperty hexProperty() {
            return hex;
        }
        
    }

    /**
     * Class present text field cell factory
     */
    public class TextFieldCellFactory implements Callback<TreeTableColumn<HexData, String>, TreeTableCell<HexData, String>> {
        
        @Override
        public TreeTableCell<HexData, String> call(TreeTableColumn<HexData, String> param) {
            return new TextFieldCell();
        }

        /**
         * Class present text fiels cell implementation
         */
        public class TextFieldCell extends TreeTableCell<HexData, String> {
            
            private TextField textField;
            private StringProperty boundToCurrently = null;

            /**
             *
             */
            public TextFieldCell() {
                textField = new TextField();
                textField.addEventFilter(KeyEvent.KEY_TYPED, hex_Validation(50));
                textField.getStyleClass().add("hexTextFieldEditor");
                textField.setEditable(false);
                textField.setOnMouseClicked((MouseEvent mouseEvent) -> {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                        handleMouseClickedEvent();
                    }
                });
                this.setGraphic(textField);
            }

            /**
             * Handle mouse clicked event
             */
            private void handleMouseClickedEvent() {
                TreeTableRow<HexData> selectedRow = (TreeTableRow<HexData>) textField.getParent().getParent();
                if (!selectedRow.getTreeItem().getValue().getOffset().contains("-")) {
                    String originalLine = textField.getText();
                    String selectedHex = textField.getSelectedText().trim();
                    String replacedHex = showDialog(selectedHex);
                    if (replacedHex != null) {
                        try {
                            textField.replaceSelection(replacedHex.toUpperCase());
                            String payLoad = hexToASCII(textField.getText());
                            TreeTableRow<HexData> hexTable = (TreeTableRow<HexData>) textField.getParent().getParent();
                            TreeItem<HexData> selectedItem = hexTable.getTreeItem();
                            selectedItem.setValue(new HexData(selectedItem.getValue().getOffset(), textField.getText(), packetParser.formatPayLoad(payLoad)));
                            String originalHex = getPacketHexFromList();
                            if (selectedItem.getValue().getOffset().contains("-")) {
                                originalHex = originalHex.replaceAll(originalLine.replaceAll(" ", "").replaceAll("\n", ""), textField.getText().replaceAll(" ", "").replaceAll("\n", ""));
                            }
                            byte[] rawdata = DatatypeConverter.parseHexBinary(originalHex);
                            EthernetPacket p = EthernetPacket.newPacket(rawdata, 0, rawdata.length);
                            packetParser.parsePacket(p, packetInfo);
                            treeRoot.getChildren().clear();
                            setData(packetInfo);
                        } catch (IllegalRawDataException ex) {
                            java.util.logging.Logger.getLogger(PacketHex.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

            /**
             * Show dialog
             *
             * @param selectedText
             * @return
             */
            private String showDialog(String selectedText) {
                Dialog dialog = new Dialog();
                dialog.setTitle("Edit Hex");
                dialog.setResizable(false);
                TextField text1 = new TextField();
                text1.addEventFilter(KeyEvent.KEY_TYPED, hex_Validation(2));
                text1.setText(selectedText);
                StackPane dialogPane = new StackPane();
                dialogPane.setPrefSize(150, 50);
                dialogPane.getChildren().add(text1);
                dialog.getDialogPane().setContent(dialogPane);
                ButtonType buttonTypeOk = new ButtonType("Save", ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
                dialog.setResultConverter(new Callback<ButtonType, String>() {
                    @Override
                    public String call(ButtonType b) {
                        
                        if (b == buttonTypeOk) {
                            switch (text1.getText().length()) {
                                case 0:
                                    return null;
                                case 1:
                                    return "0".concat(text1.getText());
                                default:
                                    return text1.getText();
                            }
                        }
                        return null;
                    }
                });
                
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    return result.get();
                }
                return null;
            }

            /**
             *
             * @param item
             * @param empty
             */
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    // Show the Text Field
                    this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    ObservableValue<String> ov = getTableColumn().getCellObservableValue(getIndex());
                    SimpleStringProperty sp = (SimpleStringProperty) ov;
                    
                    if (this.boundToCurrently == null) {
                        this.boundToCurrently = sp;
                        this.textField.textProperty().bindBidirectional(sp);
                    } else if (this.boundToCurrently != sp) {
                        this.textField.textProperty().unbindBidirectional(this.boundToCurrently);
                        this.boundToCurrently = sp;
                        this.textField.textProperty().bindBidirectional(this.boundToCurrently);
                    }
                } else {
                    this.setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }

            /**
             * Convert hex to ASCII
             *
             * @param hexString
             * @return
             */
            private String hexToASCII(String hexString) {
                String hexValue = hexString.replace(" ", "");
                StringBuilder output = new StringBuilder("");
                for (int i = 0; i < hexValue.length(); i += 2) {
                    String str = hexValue.substring(i, i + 2);
                    output.append((char) Integer.parseInt(str, 16));
                }
                return output.toString();
            }

            /**
             *
             * @param maxLengh
             * @return
             */
            public EventHandler<KeyEvent> hex_Validation(final Integer maxLengh) {
                return new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent e) {
                        TextField hexLine = (TextField) e.getSource();
                        if (hexLine.getSelection() != null) {
                            hexLine.deleteText(hexLine.getSelection());
                        }
                        if (hexLine.getText().length() >= maxLengh) {
                            e.consume();
                        }
                        if (!e.getCharacter().matches("[0-9A-Fa-f]")) {
                            e.consume();
                        }
                    }
                };
            }
            
        }
    }
}
