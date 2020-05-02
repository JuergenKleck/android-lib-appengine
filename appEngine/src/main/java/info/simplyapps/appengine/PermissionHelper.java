package info.simplyapps.appengine;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import info.simplyapps.appengine.screens.IPermissionHandler;

/**
 * Helper class which checks permissions and request permissions from the user if not granted
 */
public class PermissionHelper {

    static Map<String, Integer> permissionMapper = new HashMap<>();

    private int reformatTo16Bit(int id) {
        return id & 0x0000FFFF;
    }

    /**
     * Check the permission
     *
     * @param context
     * @param activity
     * @param permission from android.Manifest.permission.?
     * @param alwaysAsk  True if we should always ask for permission
     * @return
     */
    public boolean checkPermission(@NonNull Context context, @Nullable Activity activity, @NonNull String permission, @Nullable Boolean alwaysAsk) {
        boolean hasPermission = false;
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {

            if (!permissionMapper.containsKey(permission)) {
                permissionMapper.put(permission, reformatTo16Bit(new Random().nextInt()));
            }

            if ((alwaysAsk == null || !alwaysAsk) && ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                Toast.makeText(activity, R.string.permission_required, Toast.LENGTH_LONG).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        permissionMapper.get(permission));
            }
        } else {
            // Permission has already been granted
            hasPermission = true;
        }
        return hasPermission;
    }

    public void onRequestPermissionsResult(IPermissionHandler permissionObj, int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (permissionMapper.containsValue(requestCode)) {
            // If request is cancelled, the result arrays are empty.
            Optional<Map.Entry<String, Integer>> permission = permissionMapper.entrySet().stream().filter(stringIntegerEntry ->
                    stringIntegerEntry.getValue().equals(requestCode)).findAny();
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionObj.onPermissionResult(permission.isPresent() ? permission.get().getKey() : "", true);
            } else {
                permissionObj.onPermissionResult(permission.isPresent() ? permission.get().getKey() : "", false);
            }
            return;
        }
    }
}
