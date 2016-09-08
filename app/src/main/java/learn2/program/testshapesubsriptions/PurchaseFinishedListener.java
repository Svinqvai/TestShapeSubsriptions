package learn2.program.testshapesubsriptions;

import android.util.Log;

import java.util.List;

import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;
import learn2.program.testshapesubsriptions.billing_util.Purchase;


public class PurchaseFinishedListener implements IabHelper.OnIabPurchaseFinishedListener {

    private final List<String> subscriptions;

    private final int position;

    public PurchaseFinishedListener(List<String> subscriptions, int position) {
        this.subscriptions = subscriptions;
        this.position = position;
    }


    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        if (result.isFailure()) {
            Log.d(Constants.APP_TAG, "PurchaseFinishedListener FAIL " + result);
            return;
        }

        if (purchase != null) {
            if (purchase.getSku().equalsIgnoreCase(subscriptions.get(position))) {
                Log.d(Constants.APP_TAG, " PurchaseFinishedListener Scu: " + purchase.getSku());
                Log.d(Constants.APP_TAG, "PurchaseFinishedListener Order ID : " + purchase.getOrderId());
                Log.d(Constants.APP_TAG, "PurchaseFinishedListener DeveloperPayload: " + purchase.getDeveloperPayload());
                Log.d(Constants.APP_TAG, "PurchaseFinishedListener Type: " + purchase.getItemType());
                Log.d(Constants.APP_TAG, "PurchaseFinishedListener Purchase State: " + purchase.getPurchaseState());
                MainActivity.subscriptionIndex = position;
            }
        }
    }
}
