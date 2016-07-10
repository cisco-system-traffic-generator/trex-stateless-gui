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
package com.exalttech.trex.ui.controllers;

import com.exalttech.trex.packets.TrexEthernetPacket;
import com.exalttech.trex.util.Util;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.apache.commons.codec.DecoderException;
import org.apache.log4j.Logger;

/**
 * Packet Viewer FXML controller
 *
 * @author Georgekh
 */
public class PacketViewerController implements Initializable {

    private static final Logger LOG = Logger.getLogger(PacketViewerController.class.getName());
    @FXML
    TreeView packetDetailTree;
    @FXML
    AnchorPane hexDetailContainer;
    @FXML
    GridPane hexContainer;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // initializer
    }

    /**
     * Set packet view
     *
     * @param ethernetPacket
     */
    public void setPacketView(TrexEthernetPacket ethernetPacket) {
        try {
            convertToHex(ethernetPacket.getPacket().getRawData());
            String packetDataInfo = ethernetPacket.getPacket().toString();
            buildPacketDetailTree(packetDataInfo);
        } catch (DecoderException | UnsupportedEncodingException ex) {
            LOG.error("Error converting", ex);
        }
    }

    /**
     * Build packet detail tree
     *
     * @param packetData
     */
    private void buildPacketDetailTree(String packetData) {
        String[] mainNode = packetData.split("\n");
        TreeItem root = new TreeItem();
        packetDetailTree.setRoot(root);
        packetDetailTree.setShowRoot(false);
        for (String node : mainNode) {
            node = node.replaceAll("\r", "").trim();
            if ("[".equals(node.substring(0, 1))) {
                TreeItem subRoot = new TreeItem(node);
                root.getChildren().add(subRoot);
            } else {
                TreeItem sub = new TreeItem(node);
                ((TreeItem) root.getChildren().get(root.getChildren().size() - 1)).getChildren().add(sub);
            }
        }

    }

    /**
     * Convert to hex
     *
     * @param rawData
     * @throws DecoderException
     * @throws UnsupportedEncodingException
     */
    private void convertToHex(byte[] rawData) throws DecoderException, UnsupportedEncodingException {
        int splitLine = 16;
        int counter = 0;
        StringBuilder myString = new StringBuilder("");
        int index = 0;
        hexContainer.getChildren().clear();
        StringBuilder hexData = new StringBuilder("");
        StringBuilder indexBuffer = new StringBuilder("");
        StringBuilder rowHex = new StringBuilder("");
        StringBuilder convertedHexBuffer = new StringBuilder("");
        int spacing = 0;
        for (byte b : rawData) {
            String formatedByte = String.format("%02X", b);
            myString.append(formatedByte);
            rowHex.append(formatedByte).append(" ");
            spacing++;
            if (spacing == 4) {
                spacing = 0;
                rowHex.append("  ");
            }
            counter++;

            if (counter % splitLine == 0) {
                indexBuffer.append(String.format("%04X", index)).append(':').append('\n');
                hexData.append(rowHex.toString()).append('\n');
                convertedHexBuffer.append(convertHexToString(myString.toString())).append('\n');
                myString.setLength(0);
                rowHex.setLength(0);
                index = index + 16;
            }
        }
        if (!Util.isNullOrEmpty(myString.toString())) {
            indexBuffer.append(String.format("%04X", index)).append(':').append('\n');
            hexData.append(rowHex.toString()).append('\n');
            convertedHexBuffer.append(convertHexToString(myString.toString())).append('\n');
        }
        hexContainer.add(new Label(indexBuffer.toString()), 0, 0);
        hexContainer.add(new Label(hexData.toString()), 1, 0);
        hexContainer.add(new Label(convertedHexBuffer.toString()), 2, 0);
        rowHex.setLength(0);
        myString.setLength(0);
        hexData.setLength(0);
    }

    /**
     * Convert hex to string
     *
     * @param hex
     * @return
     */
    private String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, i + 2);
            int decimal = Integer.parseInt(output, 16);
            if (!Character.isISOControl(decimal)) {
                sb.append((char) decimal);
            } else {
                sb.append('.');
            }
        }
        return sb.toString();
    }
}
