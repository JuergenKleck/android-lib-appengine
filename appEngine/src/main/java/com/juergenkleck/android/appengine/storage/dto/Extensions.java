package com.juergenkleck.android.appengine.storage.dto;

import java.io.Serializable;

/**
 * Android library - AppEngine
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public final class Extensions extends BasicTable implements Serializable {

    /**
     * serial id
     */
    private static final long serialVersionUID = -213755492728977917L;

    public Extensions() {

    }

    public Extensions(String name) {
        this.name = name;
    }

    public Extensions(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    // the extension name
    public String name;
    public int amount;

}
