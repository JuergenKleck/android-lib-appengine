package info.simplyapps.appengine.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import info.simplyapps.appengine.PermissionHelper;

public abstract class GenericFragmentTemplate extends Fragment implements IGenericUI {

    private int screenWidth;
    private int screenHeight;

    protected PermissionHelper permissionHelper = new PermissionHelper();

    public abstract int getScreenLayout();

    public abstract boolean isFullScreen();

    public abstract void prepareStorage(Context context);

    public abstract void onPermissionResult(String permission, boolean granted);

    @Override
    public int getScreenWidth() {
        return screenWidth;
    }

    @Override
    public int getScreenHeight() {
        return screenHeight;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        prepareStorage(getActivity().getApplicationContext());

        if (isFullScreen()) {
            getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        getActivity().setContentView(getScreenLayout());

    }

    public boolean checkPermission(String permission, Boolean alwaysAsk) {
        return permissionHelper.checkPermission(getContext(), getActivity(), permission, alwaysAsk);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

}
