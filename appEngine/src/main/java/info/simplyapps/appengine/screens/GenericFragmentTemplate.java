package info.simplyapps.appengine.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.fragment.app.Fragment;

public abstract class GenericFragmentTemplate extends Fragment {

    protected abstract int getScreenLayout();

    protected abstract boolean isFullScreen();

    public abstract void prepareStorage(Context context);

    public int screenWidth;
    public int screenHeight;

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
}
