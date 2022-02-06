package com.juergenkleck.android.appengine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.juergenkleck.android.appengine.storage.DBDriver;
import com.juergenkleck.android.appengine.storage.StoreData;
import com.juergenkleck.android.appengine.storage.dto.Configuration;

/**
 * Android library - AppEngine
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public final class UpdateCheck extends AsyncTask<String, String, String> {

    public UpdateCheck() {
    }

    public static boolean requiresUpdate(StoreData storeData) {
        Configuration cOnServer = SystemHelper.getConfiguration(AppEngineConstants.CONFIG_ON_SERVER, AppEngineConstants.DEFAULT_CONFIG_ON_SERVER);
        if (Integer.valueOf(cOnServer.value) > storeData.migration) {// && Boolean.valueOf(cForce.value).booleanValue()) {
            return true;
        }
        return false;
    }

    public static boolean requiresCheck(StoreData storeData) {
        Configuration cLastCheck = SystemHelper.getConfiguration(AppEngineConstants.CONFIG_LAST_CHECK, AppEngineConstants.DEFAULT_CONFIG_LAST_CHECK);
        if (Long.valueOf(cLastCheck.value) + AppEngineConstants.UPDATE_INTERVAL < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return downloadUrl(params[0]);
        } catch (IOException e) {
//			return "Unable to retrieve web page. URL may be invalid.";
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (result != null && result.length() > 0) {
                JSONObject obj = new JSONObject(result);
                int version = -1;
                boolean force = false;
                if (obj.has("version")) {
                    version = obj.getInt("version");
                }
                if (obj.has("force")) {
                    force = obj.getBoolean("force");
                }

                if (version > 0) {
                    synchronized (StoreData.getInstance()) {
                        // stored version greater than installed version
                        if (version > StoreData.getInstance().migration) {
                            Configuration cForce = SystemHelper.getConfiguration(AppEngineConstants.CONFIG_FORCE_UPDATE, AppEngineConstants.DEFAULT_CONFIG_FORCE_UPDATE);
                            Configuration cOnServer = SystemHelper.getConfiguration(AppEngineConstants.CONFIG_ON_SERVER, AppEngineConstants.DEFAULT_CONFIG_ON_SERVER);
                            cForce.value = Boolean.toString(force);
                            cOnServer.value = Integer.toString(version);
                            if (DBDriver.getInstance().store(cOnServer)) {
                                SystemHelper.setConfiguration(cOnServer);
                            }
                            if (DBDriver.getInstance().store(cForce)) {
                                SystemHelper.setConfiguration(cForce);
                            }
                        }
                        Configuration cLastCheck = SystemHelper.getConfiguration(AppEngineConstants.CONFIG_LAST_CHECK, AppEngineConstants.DEFAULT_CONFIG_LAST_CHECK);
                        cLastCheck.value = Long.toString(System.currentTimeMillis());
                        if (DBDriver.getInstance().store(cLastCheck)) {
                            SystemHelper.setConfiguration(cLastCheck);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // do nothing on failure
        }
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }


    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 100 characters of the retrieved
        // web page content.
        int len = 100;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(AppEngineConstants.READ_TIMEOUT);
            conn.setConnectTimeout(AppEngineConstants.CONNECTION_TIMEOUT);
            conn.setRequestMethod(AppEngineConstants.CONNECTION_METHOD);
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public static void showRemindLaterDialog(final Activity activity, final String packageName) {
        AlertDialog.Builder b = new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.update_title))
                .setMessage(activity.getString(R.string.update_text))
                .setPositiveButton(activity.getString(R.string.btn_update), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                AppEngineConstants.GOOGLE_PLAY_URL + packageName));
                        activity.startActivity(marketIntent);
                        activity.finish();
                    }
                })
                .setNegativeButton(activity.getString(R.string.btn_later), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Configuration cLastCheck = SystemHelper.getConfiguration(AppEngineConstants.CONFIG_LAST_CHECK, AppEngineConstants.DEFAULT_CONFIG_LAST_CHECK);
                        cLastCheck.value = Long.toString(System.currentTimeMillis());
                        if (DBDriver.getInstance().store(cLastCheck)) {
                            SystemHelper.setConfiguration(cLastCheck);
                        }
                    }
                });
        AlertDialog d = b.create();
        d.show();
    }

}
