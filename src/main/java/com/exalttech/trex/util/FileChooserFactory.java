package com.exalttech.trex.util;

import javafx.stage.FileChooser;


public class FileChooserFactory {
    private static FileChooser fileChooser = null;

    public static FileChooser get() {
        if (fileChooser != null) {
            return fileChooser;
        }
        return new FileChooser();
    }

    public static void set(final FileChooser fileChooser) {
        FileChooserFactory.fileChooser = fileChooser;
    }
}
