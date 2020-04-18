package info.simplyapps.appengine.screens;

import android.content.Context;

public interface IGenericUI {

    void prepareStorage(Context context);

    void onPermissionResult(String permission, boolean granted);

    int getScreenLayout();

    boolean isFullScreen();

    int getScreenWidth();

    int getScreenHeight();

}
