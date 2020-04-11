package info.simplyapps.appengine;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import info.simplyapps.appengine.billing.v3.IabHelper;
import info.simplyapps.appengine.billing.v3.IabResult;
import info.simplyapps.appengine.billing.v3.Purchase;
import info.simplyapps.appengine.storage.DBDriver;
import info.simplyapps.appengine.storage.StoreData;
import info.simplyapps.appengine.storage.dto.Purchases;

import java.util.List;

public abstract class PaymentHandler {

    static final String TAG = "PaymentHandler";

    static final int RC_REQUEST = 30001;

    final String base64EncodedPublicKey;

    protected final Activity activity;

    // The helper object
    IabHelper mHelper;

    /**
     * List of item ids that are managed<br>
     * All items must be added here if they are managed
     *
     * @return
     */
    public abstract List<String> getManagedItemIds();

    /**
     * List of item ids that are unmanaged<br>
     * All items must be added here if they are unmanaged
     *
     * @return
     */
    public abstract List<String> getUnmanagedItemIds();

    public abstract void consumeUnmanagedItem(String item);

    public PaymentHandler(Activity activity, String base64EncodedPublicKey) {
        this.base64EncodedPublicKey = base64EncodedPublicKey;
        this.activity = activity;
        prepareBilling();
    }

    public final void actionLaunchPurchase(String item) {
        if (mHelper.isBusy()) {
            Toast.makeText(activity, R.string.error_purchase_busy, Toast.LENGTH_LONG).show();
        } else if (!mHelper.mSetupDone) {
            Toast.makeText(activity, R.string.error_purchase_unavail, Toast.LENGTH_LONG).show();
        } else {
            try {
                mHelper.launchPurchaseFlow(activity, item, RC_REQUEST, mPurchaseFinishedListener);
            } catch (IllegalStateException e) {
                Toast.makeText(activity, R.string.error_purchase_busy, Toast.LENGTH_LONG).show();
            }
        }
    }

    public final void destroy() {
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    private void prepareBilling() {

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(activity, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set activity to false).
        mHelper.enableDebugLogging(false);

        // Start setup. activity is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    void complain(String message) {
        Log.e(TAG, "**** App Error: " + message);
        alert(message);
    }

    void alert(String message) {
        Log.d(TAG, "Showing alert dialog: " + message);
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    public final boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                // Oh noes!
                String err = result.getMessage();
                if (err.indexOf('(') > 0) {
                    err = err.substring(0, err.indexOf('('));
                }
                complain("Error purchasing: " + err);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            // check if we bought an unmanaged item
            // launch consume listener then
            if (isUnmanagedItem(purchase.getSku())) {
                Log.d(TAG, "Purchase is coins - consume now.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
            if (isManagedItem(purchase.getSku())) {
                // bought the full version
                Log.d(TAG, "Purchase is full version.");
                addManagedItem(purchase.getSku());
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // check sku for the item which is consumed
            if (result.isSuccess()) {
                // successfully consumed, so we apply the unmanaged item
                Log.d(TAG, "Consumption successful. Provisioning.");
                // update storage data
                if (isUnmanagedItem(purchase.getSku())) {
                    consumeUnmanagedItem(purchase.getSku());
                }
            } else {
                complain("Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    // Listener that's called when we finish querying the items we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, info.simplyapps.appengine.billing.v3.Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }
            Log.d(TAG, "Query inventory was successful.");

            for (String itemId : getUnmanagedItemIds()) {
                if (inventory.hasPurchase(itemId)) {
                    Log.d(TAG, "Purchase is " + itemId + " - consume now.");
                    mHelper.consumeAsync(inventory.getPurchase(itemId), mConsumeFinishedListener);
                }
            }
            for (String itemId : getManagedItemIds()) {
                if (inventory.hasPurchase(itemId)) {
                    // bought the full version
                    Log.d(TAG, "Inventory contains " + itemId);
                    addManagedItem(itemId);
                }
            }
        }
    };

    public final void addUnmanagedItem(String item) {
        Purchases p = new Purchases(item);
        if (DBDriver.getInstance().store(p)) {
            StoreData.getInstance().purchases.add(p);
        } else {
            Toast.makeText(activity, R.string.save_failed, Toast.LENGTH_LONG).show();
        }
    }

    public final void addManagedItem(String item) {
        if (!SystemHelper.hasPurchase(item)) {
            addUnmanagedItem(item);
        }
    }

    boolean isUnmanagedItem(String item) {
        return getUnmanagedItemIds().contains(item);
    }

    boolean isManagedItem(String item) {
        return getManagedItemIds().contains(item);
    }

}
