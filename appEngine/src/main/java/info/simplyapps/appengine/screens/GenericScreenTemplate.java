package info.simplyapps.appengine.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import info.simplyapps.appengine.PermissionHelper;

public abstract class GenericScreenTemplate extends AppCompatActivity implements IGenericUI, IPermissionHandler, ActivityCompat.OnRequestPermissionsResultCallback {

    private int screenWidth;
    private int screenHeight;

    protected PermissionHelper permissionHelper = new PermissionHelper();

    public abstract int getScreenLayout();

    public abstract boolean isFullScreen();

    public abstract void prepareStorage(Context context);

    public abstract void onPermissionResult(String permission, boolean granted);

    public abstract void onScreenCreate(Bundle savedInstanceState);

    @Override
    public int getScreenWidth() {
        return screenWidth;
    }

    @Override
    public int getScreenHeight() {
        return screenHeight;
    }

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

        onScreenCreate(savedInstanceState);
    }

    public boolean checkPermission(String permission, Boolean alwaysAsk) {
        return permissionHelper.checkPermission(getApplicationContext(), this, permission, alwaysAsk);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

}
