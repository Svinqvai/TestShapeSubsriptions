package learn2.program.testshapesubsriptions;

import android.util.Log;

import java.util.List;

import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;
import learn2.program.testshapesubsriptions.billing_util.Purchase;


public class SubscriptionPurchaseListener implements IabHelper.OnIabPurchaseFinishedListener {

    private final List<String> subscriptions;

    private final IabHelper mHelper;

    private final int position;

    public SubscriptionPurchaseListener(List<String> subscriptions,IabHelper mHelper,int position){
        this.subscriptions = subscriptions;
        this.mHelper = mHelper;
        this.position = position;
    }


    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        if (result.isFailure()){
            Log.d(Constants.APP_TAG, "SubscriptionPurchaseListener FAIL " + result);
            return;
        }

        if (info != null) {
            if(info.getSku().equalsIgnoreCase(subscriptions.get(position))){
                mHelper.consumeAsync(info,new SubscriptionConsumeFinishedListener());

                Log.d(Constants.APP_TAG, "Scu: " + info.getSku());
                Log.d(Constants.APP_TAG, "Order ID : " + info.getOrderId());
                Log.d(Constants.APP_TAG, "DeveloperPayload: " + info.getDeveloperPayload());
            }
        }
    }
}
