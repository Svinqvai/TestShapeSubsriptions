package learn2.program.testshapesubsriptions.subscriptions;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import learn2.program.testshapesubsriptions.R;
import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;
import learn2.program.testshapesubsriptions.billing_util.Inventory;
import learn2.program.testshapesubsriptions.billing_util.Purchase;
import learn2.program.testshapesubsriptions.nav.BaseActivity;
import learn2.program.testshapesubsriptions.nav.Constants;

public class MainActivity extends BaseActivity {
    private boolean hasSubscription;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alert("O_o " + hasSubscription);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(Constants.APP_TAG, "IabHelper.OnIabSetupFinishedListener() --> Setup finished.");

                if (!result.isSuccess()) {
                    alert("Problem setting up in-app billing: " + result);
                    return;
                }
                if (mHelper == null) {
                    return;
                }

                try {
                    mHelper.queryInventoryAsync(true, null, subscriptionsList, mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    alert("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (mHelper == null) {
                return;
            }

            if (result.isFailure()) {
                return;
            }
            Log.d(Constants.APP_TAG, "Query inventory was successful.");

            for (String key : subscriptionsList) {
                Purchase purchase = inventory.getPurchase(key);
                if (purchase != null && purchase.isAutoRenewing()) {
                    hasSubscription = verifyDeveloperPayload(purchase);
                    break;
                }
            }
        }
    };
}
