package learn2.program.testshapesubsriptions;

import android.util.Log;

import java.util.List;

import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;
import learn2.program.testshapesubsriptions.billing_util.Purchase;


public class SubscriptionPurchaseListener implements IabHelper.OnIabPurchaseFinishedListener {

    private final List<String> subscriptions;

    private final IabHelper mHelper;

    public SubscriptionPurchaseListener(List<String> subscriptions,IabHelper mHelper){
        this.subscriptions = subscriptions;
        this.mHelper = mHelper;
    }


    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        if (result.isFailure()){
            Log.d(Constants.APP_TAG, "SubscriptionPurchaseListener FAIL " + result);
            return;
        }

        if (info != null) {
            //TODO not 0 but the selected item
            if(info.getSku().equalsIgnoreCase(subscriptions.get(0))){
                mHelper.consumeAsync(info,new SubscriptionConsumeFinishedListener());

                Log.d(Constants.APP_TAG, "Scu: " + info.getSku());
                Log.d(Constants.APP_TAG, "Order ID : " + info.getOrderId());
                Log.d(Constants.APP_TAG, "DeveloperPayload: " + info.getDeveloperPayload());
            }
        }
    }
}
