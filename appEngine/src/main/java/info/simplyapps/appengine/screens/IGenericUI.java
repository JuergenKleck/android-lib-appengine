package info.simplyapps.appengine.screens;

import android.content.Context;

public interface IGenericUI {

    void prepareStorage(Context context);

    int getScreenLayout();

    boolean isFullScreen();

    int getScreenWidth();

    int getScreenHeight();

}
