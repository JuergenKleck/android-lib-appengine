package com.juergenkleck.android.appengine.storage.dto;

import java.io.Serializable;

/**
 * Android library - AppEngine
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public final class Configuration extends BasicTable implements Serializable {

    /**
     * serial id
     */
    private static final long serialVersionUID = -213755492728977917L;

    public Configuration() {

    }

    public Configuration(String name) {
        this.name = name;
    }

    public Configuration(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String name;
    public String value;

}
