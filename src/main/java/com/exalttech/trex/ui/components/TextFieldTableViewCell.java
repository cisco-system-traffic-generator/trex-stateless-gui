/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exalttech.trex.ui.components;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

/**
 * Textfield table view cell implementation
 * @author GeorgeKH
 */
public class TextFieldTableViewCell<S, T> implements Callback<TableColumn, TableCell> {

    @Override
    public TableCell call(TableColumn param) {
        return new TableCell<S, T>() {
            private TextField textField;
            {
                textField = new TextField();
                textField.setPrefSize(158, 22);
                textField.setOnKeyPressed((KeyEvent event) -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        commitEdit((T) textField.getText());
                    }
                });
                textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if (!newValue) {
                        commitEdit((T) textField.getText());
                       
                    }
                });
                
                textField.textProperty().bindBidirectional(textProperty());
            }
            
            @Override
            public void updateItem(final T item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(textField);
                    setText((String) getItem());
                }
            }
            
            @Override
            public void commitEdit(T item) {
                
                if (!isEditing() && !item.equals(getItem())) {
                    TableView<S> table = getTableView();
                    if (table != null) {
                        TableColumn<S, T> column = getTableColumn();
                        CellEditEvent<S, T> event = new CellEditEvent<>(table, new TablePosition<S, T>(table, getIndex(), column), TableColumn.editCommitEvent(), item);
                        Event.fireEvent(column, event);
                    }
                }
                super.commitEdit(item);
            }
        };
    }

}
