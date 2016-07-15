package learn2.program.testshapesubsriptions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;


public class MainActivity extends AppCompatActivity {
    Button buttonOne;
    Button buttonTwo;

    private static final int RC_REQUEST = 10001;
    private IabHelper mHelper;
    String base64EncodedPublicKey = "";
    private static final String ITEM_SKU = "android.test.purchase.subscription";

    private static List<String> subscriptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        subscriptions = Arrays.asList("oneMonth", "threeMonths", "sixMonths", "oneYear");

        buttonOne = (Button) findViewById(R.id.buttonOne);
        buttonTwo = (Button) findViewById(R.id.buttonTwo);

        buttonOne.setOnClickListener(btnOneCL);
        buttonOne.setEnabled(false);
        buttonTwo.setOnClickListener(btnTwoCL);
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(Constants.APP_TAG, "IabHelper.OnIabSetupFinishedListener() --> Setup finished.");

                if (!result.isSuccess()) {
                    Log.d(Constants.APP_TAG, "Problem setting up in-app billing: " + result);
                    return;
                } else {
                    Log.d(Constants.APP_TAG, "SUCCESS: " + result);
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;
                mHelper.queryInventoryAsync(true, subscriptions, new SubscriptionOnQueryInventoryFinished(subscriptions));

            }
        });


    }

    private final View.OnClickListener btnOneCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "Btn One", Toast.LENGTH_SHORT).show();
        }
    };

    private final View.OnClickListener btnTwoCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "Btn Two", Toast.LENGTH_SHORT).show();
            buy();
            consume();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Constants.APP_TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        if (requestCode == RC_REQUEST && resultCode == RESULT_OK) {
            if (mHelper != null && !mHelper.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);

            }
        }
    }


    private void buy() {
        if (mHelper != null && mHelper.subscriptionsSupported()) {
            mHelper.launchSubscriptionPurchaseFlow(MainActivity.this, subscriptions.get(0), RC_REQUEST, new SubscriptionPurchaseListener(subscriptions, mHelper), "token" + 3232);
        }
    }

    private void consume() {
        if (mHelper != null && mHelper.subscriptionsSupported()) {
            mHelper.queryInventoryAsync(new SubscriptionOnQueryInventoryFinished(subscriptions));
        }
    }
}
