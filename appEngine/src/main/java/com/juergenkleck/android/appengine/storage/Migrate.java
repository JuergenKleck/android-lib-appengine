package com.juergenkleck.android.appengine.storage;

import com.juergenkleck.android.appengine.storage.dto.BasicTable;

/**
 * Android library - AppEngine
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class Migrate {

    public int from;
    public int to;
    public BasicTable data;

    public Migrate() {

    }

    public Migrate(int from, int to, BasicTable data) {
        super();
        this.from = from;
        this.to = to;
        this.data = data;
    }

}
