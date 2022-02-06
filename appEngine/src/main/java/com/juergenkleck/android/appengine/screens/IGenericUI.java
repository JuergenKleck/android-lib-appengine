package com.juergenkleck.android.appengine.screens;

import android.content.Context;

/**
 * Android library - AppEngine
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public interface IGenericUI {

    void prepareStorage(Context context);

    int getScreenLayout();

    boolean isFullScreen();

    int getScreenWidth();

    int getScreenHeight();

}
