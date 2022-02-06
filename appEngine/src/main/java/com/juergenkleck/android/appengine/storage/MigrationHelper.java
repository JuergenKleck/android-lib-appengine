package com.juergenkleck.android.appengine.storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Android library - AppEngine
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public class MigrationHelper {

    private static List<Migrate> list = new ArrayList<Migrate>();

    public static synchronized void add(Migrate m) {
        list.add(m);
    }

    public static synchronized List<Migrate> get() {
        return list;
    }

    public static synchronized boolean hasData() {
        return list.size() > 0;
    }

}
