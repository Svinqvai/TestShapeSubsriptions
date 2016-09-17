package learn2.program.testshapesubsriptions.subscriptions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import learn2.program.testshapesubsriptions.R;
import learn2.program.testshapesubsriptions.billing_util.IabException;
import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;
import learn2.program.testshapesubsriptions.billing_util.Inventory;
import learn2.program.testshapesubsriptions.billing_util.Purchase;
import learn2.program.testshapesubsriptions.nav.BaseActivity;
import learn2.program.testshapesubsriptions.nav.Constants;
import learn2.program.testshapesubsriptions.nav.SubscriptionActivity;

public class MainActivity extends BaseActivity {
    private boolean hasSubscription;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
                new GetUserSubscription().execute();
            }
        });

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SubscriptionActivity.class));
            }
        });
    }

    private class GetUserSubscription extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            try {
                Inventory inventory = mHelper.queryInventory(true, null, subscriptionsList);

                for (String key : subscriptionsList) {
                    Purchase purchase = inventory.getPurchase(key);
                    if (purchase != null && purchase.isAutoRenewing()) {
                        hasSubscription = verifyDeveloperPayload(purchase);
                        break;
                    }
                }
            } catch (IabException e) {
                alert("Error querying inventory. Another async operation in progress.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final TextView textView = (TextView) findViewById(R.id.textView);
            final String text = hasSubscription ? "User has subscription" : "User does NOT have a subscription";
            textView.setText(text);
        }
    }
}