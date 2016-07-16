package learn2.program.testshapesubsriptions;

import android.util.Log;

import java.util.List;

import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;
import learn2.program.testshapesubsriptions.billing_util.Inventory;


public class SubscriptionOnQueryInventoryFinished implements IabHelper.QueryInventoryFinishedListener {

    private final List<String> subscriptions;

    public  static boolean hasSubscription;

    public SubscriptionOnQueryInventoryFinished(List<String> subscriptions) {
        this.subscriptions = subscriptions;
    }


    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
        if (result.isFailure()) {
            Log.d(Constants.APP_TAG, "QueryForSubscriptionDetailsListener FAIL Cannot get the list of the subscriptions " + result);
            return;
        }
        if (inv != null) {
            for (String subscription : subscriptions) {
                if (inv.hasDetails(subscription)) {
                    Log.d(Constants.APP_TAG, "Scu: " + inv.getSkuDetails(subscription).getSku());
                    Log.d(Constants.APP_TAG, "Scu Title: " + inv.getSkuDetails(subscription).getTitle());
                    Log.d(Constants.APP_TAG, "Scu Type: " + inv.getSkuDetails(subscription).getType());
                    Log.d(Constants.APP_TAG, "Scu Price: " + inv.getSkuDetails(subscription).getPrice());
                    Log.d(Constants.APP_TAG, "Scu Desc: " + inv.getSkuDetails(subscription).getDescription());
                    Log.d(Constants.APP_TAG, "Purchase status: " + (inv.hasPurchase(subscription) ? "YES" : "NO"));

                    Log.d(Constants.APP_TAG, "-------------------------------------------------------");
                    if(inv.hasPurchase(subscription)){
                        hasSubscription = true;
                        return;
                    }else{
                        hasSubscription =false;
                    }
                }
            }
        }
    }
}
