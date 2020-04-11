package info.simplyapps.appengine.screens;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public abstract class GenericScreenTemplate extends Activity {

    protected abstract int getScreenLayout();

    protected abstract boolean isFullScreen();

    public abstract void prepareStorage(Context context);

    public int screenWidth;
    public int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        prepareStorage(getApplicationContext());

        if (isFullScreen()) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(getScreenLayout());

    }
}
