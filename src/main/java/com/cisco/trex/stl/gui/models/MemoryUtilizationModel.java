package com.cisco.trex.stl.gui.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MemoryUtilizationModel {
    private StringProperty title = new SimpleStringProperty();
    private StringProperty bank64b = new SimpleStringProperty();
    private StringProperty bank128b = new SimpleStringProperty();
    private StringProperty bank256b = new SimpleStringProperty();
    private StringProperty bank512b = new SimpleStringProperty();
    private StringProperty bank1024b = new SimpleStringProperty();
    private StringProperty bank2048b = new SimpleStringProperty();
    private StringProperty bank4096b = new SimpleStringProperty();
    private StringProperty bank9kb = new SimpleStringProperty();
    private StringProperty ram = new SimpleStringProperty();

    public MemoryUtilizationModel(String title,
                                  int bank64b,
                                  int bank128b,
                                  int bank256b,
                                  int bank512b,
                                  int bank1024b,
                                  int bank2048b,
                                  int bank4096b,
                                  int bank9kb,
                                  String ram) {
        this.title.set(title);
        this.bank64b.set(String.valueOf(bank64b) + ("percent".equalsIgnoreCase(title) ? "%" : ""));
        this.bank128b.set(String.valueOf(bank128b) + ("percent".equalsIgnoreCase(title) ? "%" : ""));
        this.bank256b.set(String.valueOf(bank256b) + ("percent".equalsIgnoreCase(title) ? "%" : ""));
        this.bank512b.set(String.valueOf(bank512b) + ("percent".equalsIgnoreCase(title) ? "%" : ""));
        this.bank1024b.set(String.valueOf(bank1024b) + ("percent".equalsIgnoreCase(title) ? "%" : ""));
        this.bank2048b.set(String.valueOf(bank2048b) + ("percent".equalsIgnoreCase(title) ? "%" : ""));
        this.bank4096b.set(String.valueOf(bank4096b) + ("percent".equalsIgnoreCase(title) ? "%" : ""));
        this.bank9kb.set(String.valueOf(bank9kb) + ("percent".equalsIgnoreCase(title) ? "%" : ""));
        this.ram.set(ram);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty bank64bProperty() {
        return bank64b;
    }

    public StringProperty bank128bProperty() {
        return bank128b;
    }

    public StringProperty bank256bProperty() {
        return bank256b;
    }

    public StringProperty bank512bProperty() {
        return bank512b;
    }

    public StringProperty bank1024bProperty() {
        return bank1024b;
    }

    public StringProperty bank2048bProperty() {
        return bank2048b;
    }

    public StringProperty bank4096bProperty() {
        return bank4096b;
    }

    public StringProperty bank9kbProperty() {
        return bank9kb;
    }

    public StringProperty ramProperty() {
        return ram;
    }
}
