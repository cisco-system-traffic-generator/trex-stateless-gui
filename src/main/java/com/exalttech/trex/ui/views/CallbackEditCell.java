package com.exalttech.trex.ui.views;

import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public class CallbackEditCell<S, T> extends TextFieldTableCell<S, T> {
    private final CallbackEditHandler<T> handler;

    @FunctionalInterface
    interface CallbackEditHandler<T> {
        /**
         * Callback which is called right before commitEdit of cell
         * @param rowIndex index of row to be edited
         * @param newValue new value to received on commitEdit
         * @return true if success, false otherwise. If returns true, TextFieldTableCell commits newValue,
         * if false -- does not commit.
         */
        boolean update(Integer rowIndex, T newValue);
    }

    public CallbackEditCell(CallbackEditHandler<T> handler, StringConverter<T> converter) {
        super(converter);
        this.handler = handler;
    }

    @Override
    public void commitEdit(T newValue) {
        boolean isSuccess = handler.update(getIndex(), newValue);
        if (isSuccess) {
            super.commitEdit(newValue);
        }
    }
}
