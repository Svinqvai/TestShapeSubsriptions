package learn2.program.testshapesubsriptions;

import android.util.Log;

import java.util.List;

import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;
import learn2.program.testshapesubsriptions.billing_util.Inventory;
import learn2.program.testshapesubsriptions.billing_util.Purchase;


public class QueryInventoryFinishedListener implements IabHelper.QueryInventoryFinishedListener {

    private final List<String> subscriptions;

    public QueryInventoryFinishedListener(List<String> subscriptions) {
        this.subscriptions = subscriptions;
    }


    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        if (result.isFailure()) {
            Log.d(Constants.APP_TAG, "QueryForSubscriptionDetailsListener FAIL Cannot get the list of the subscriptions " + result);
            return;
        }

        if (inventory != null) {
            for (int i = 0; i < subscriptions.size(); i++) {
                String subscriptionKey = subscriptions.get(i);

                if (inventory.hasDetails(subscriptionKey)) {
                    Log.d(Constants.APP_TAG, "Scu: " + inventory.getSkuDetails(subscriptionKey).getSku());
                    Log.d(Constants.APP_TAG, "Scu Title: " + inventory.getSkuDetails(subscriptionKey).getTitle());
                    Log.d(Constants.APP_TAG, "Scu Type: " + inventory.getSkuDetails(subscriptionKey).getType());
                    Log.d(Constants.APP_TAG, "Scu Price: " + inventory.getSkuDetails(subscriptionKey).getPrice());
                    Log.d(Constants.APP_TAG, "Scu Desc: " + inventory.getSkuDetails(subscriptionKey).getDescription());
                    Log.d(Constants.APP_TAG, "Purchase status: " + (inventory.hasPurchase(subscriptionKey) ? "YES" : "NO"));

                    Log.d(Constants.APP_TAG, "-------------------------------------------------------");

                    if (inventory.hasPurchase(subscriptionKey)) {
                        return;
                    }
                }
            }
        }
    }
}
