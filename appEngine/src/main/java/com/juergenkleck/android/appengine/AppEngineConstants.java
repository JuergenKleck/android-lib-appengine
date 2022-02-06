package com.juergenkleck.android.appengine;


/**
 * Android library - AppEngine
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public final class AppEngineConstants {

    public static final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=";

    // 4 weeks
    public static final long UPDATE_INTERVAL = 2419200000l;

    public static final int READ_TIMEOUT = 10000;
    public static final int CONNECTION_TIMEOUT = 20000;
    public static final String CONNECTION_METHOD = "GET";

    public static final String CONFIG_MIGRATION = "migration";
    public static final String CONFIG_ON_SERVER = "onserver";
    public static final String CONFIG_FORCE_UPDATE = "forceupdate";
    public static final String CONFIG_LAST_CHECK = "lastcheck";

    public static final String DEFAULT_CONFIG_MIGRATION = Integer.toString(0);
    public static final String DEFAULT_CONFIG_ON_SERVER = Integer.toString(0);
    public static final String DEFAULT_CONFIG_FORCE_UPDATE = Boolean.FALSE.toString();
    public static final String DEFAULT_CONFIG_LAST_CHECK = Long.toString(System.currentTimeMillis());

}
