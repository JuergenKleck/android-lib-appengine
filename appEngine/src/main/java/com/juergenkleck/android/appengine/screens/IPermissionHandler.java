package com.juergenkleck.android.appengine.screens;

/**
 * Android library - AppEngine
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public interface IPermissionHandler {

    void onPermissionResult(String permission, boolean granted);

}
