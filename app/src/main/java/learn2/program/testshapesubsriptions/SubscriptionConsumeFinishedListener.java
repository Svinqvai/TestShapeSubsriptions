package learn2.program.testshapesubsriptions;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;
import learn2.program.testshapesubsriptions.billing_util.Purchase;

public class SubscriptionConsumeFinishedListener implements IabHelper.OnConsumeFinishedListener{

    SubscriptionConsumeFinishedListener(){

    }

    @Override
    public void onConsumeFinished(Purchase purchase, IabResult result) {
        if(result.isFailure()){
            Log.d(Constants.APP_TAG, "SubscriptionConsumeFinishedListener FAIL " + result);
        }else {
            Log.d(Constants.APP_TAG, "SubscriptionConsumeFinishedListener SUCCESS " + result);
        }
    }
}
