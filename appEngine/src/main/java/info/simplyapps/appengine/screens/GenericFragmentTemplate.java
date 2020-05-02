package info.simplyapps.appengine.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import info.simplyapps.appengine.PermissionHelper;

public abstract class GenericFragmentTemplate extends Fragment implements IGenericUI, IPermissionHandler {

    protected View root;

    private int screenWidth;
    private int screenHeight;

    protected PermissionHelper permissionHelper = new PermissionHelper();

    /**
     * The screen layout which may be null
     *
     * @return
     */
    public abstract int getScreenLayout();

    /**
     * The view layout which must be present if the screen layout is not provided
     *
     * @return
     */
    public abstract int getViewLayout();

    public abstract boolean isFullScreen();

    public abstract void prepareStorage(Context context);

    /**
     * Invoked if a permission request response is incoming from the system
     *
     * @param permission
     * @param granted
     */
    public abstract void onPermissionResult(String permission, boolean granted);

    /**
     * Called when the view is destroyed
     */
    public abstract void onFragmentDestroyView();

    /**
     * Called when the view is created
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    public abstract void onFragmentCreateView(@NonNull LayoutInflater inflater,
                                              ViewGroup container, Bundle savedInstanceState);

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

        if (getScreenLayout() > 0) {
            getActivity().setContentView(getScreenLayout());
        }

        onScreenCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (getViewLayout() > 0) {
            root = inflater.inflate(getViewLayout(), container, false);
        }
        onFragmentCreateView(inflater, container, savedInstanceState);
        return root;
    }

    @Override
    public void onDestroyView() {
        onFragmentDestroyView();
        super.onDestroyView();
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
