package learn2.program.testshapesubsriptions;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;
import learn2.program.testshapesubsriptions.billing_util.Inventory;
import learn2.program.testshapesubsriptions.billing_util.Purchase;

public class MainActivity extends AppCompatActivity {

    Button oneMonthBtn;

    Button threeMonthsBtn;

    Button sixMonthsBtn;

    Button oneYearBtn;

    ImageView pearImgView;

    public static int subscriptionIndex = -1;


    private String currentSubscriptionPeriod;

    private boolean hasSubscription;

    private boolean mAutoRenewEnabled;


    private static final int RC_REQUEST = 10023;
    private IabHelper mHelper;
    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvgtPCE24JN71QUFgdFde8Zu7mop3ozX4SWP3xd8s8xf1Dqov/0MUg957NcfAhMWkggFMn0DTByPV9lS68/tfrIPgkX96VQz9jluvH++kGfpkfQV3WfGbV7dGHNwD9gqMuZRy7V3efZZXrc1ewdut6MZE97i5lpo5lp3HVyfjnCl3yLmI/Di6l+UdKU+ZERkWOT1ONeww+lP71gz0UweGegyfbXDmYso33HW7bJcpjLmv9x3yhrbEqNXgalNFA1TQtS4sdh1O20m36xoKp4q1E35QPnM7Hg5/8qDPOF5k31OQHogigjRTSWTmiYVN5ynXM70viFBX65g0SNYtHh/emwIDAQAB";

    private static List<String> subscriptionsKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subscriptionsKeys = Arrays.asList("learn2.program.testsubscriptiononemonth", "learn2.program.testsubscriptionthreemonth", "learn2.program.testsubscriptionsixmonth", "learn2.program.testsubscriptiononeyear");

        oneMonthBtn = (Button) findViewById(R.id.oneMonthBtn);
        threeMonthsBtn = (Button) findViewById(R.id.threeMonthsBtn);
        sixMonthsBtn = (Button) findViewById(R.id.sixMonthsBtn);
        oneYearBtn = (Button) findViewById(R.id.oneYearBtn);
        pearImgView = (ImageView) findViewById(R.id.pearImgView);

        oneMonthBtn.setOnClickListener(subscriptionCL);
        threeMonthsBtn.setOnClickListener(subscriptionCL);
        sixMonthsBtn.setOnClickListener(subscriptionCL);
        oneYearBtn.setOnClickListener(subscriptionCL);

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(Constants.APP_TAG, "IabHelper.OnIabSetupFinishedListener() --> Setup finished.");

                if (!result.isSuccess()) {
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }
                if (mHelper == null) {
                    return;
                }
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });

        if (!hasSubscription || !mAutoRenewEnabled) {
            //Display all buttons
        } else {
            if (currentSubscriptionPeriod.equalsIgnoreCase(subscriptionsKeys.get(0))) {
                oneMonthBtn.setEnabled(false);
            }
            if (currentSubscriptionPeriod.equalsIgnoreCase(subscriptionsKeys.get(0))) {
                oneMonthBtn.setEnabled(false);
                threeMonthsBtn.setEnabled(false);
            }
        }
    }


    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (mHelper == null) {
                return;
            }

            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }
            Log.d(Constants.APP_TAG, "Query inventory was successful.");

            // First find out which subscription is auto renewing
            for (String subscriptionKey : subscriptionsKeys) {
                Purchase purchase = inventory.getPurchase(subscriptionKey);
                if (purchase != null && purchase.isAutoRenewing()) {
                    currentSubscriptionPeriod = subscriptionKey;
                    mAutoRenewEnabled = true;
                } else {
                    currentSubscriptionPeriod = "";
                    mAutoRenewEnabled = false;
                }

                if (purchase != null && verifyDeveloperPayload(purchase)) {
                    hasSubscription = true;
                }
            }
        }
    };
    //TODO on start decide which buttons to show based on current subscription


    private final View.OnClickListener subscriptionCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String mSelectedSubscriptionPeriod;
            if (view == oneMonthBtn) {
                mSelectedSubscriptionPeriod = subscriptionsKeys.get(0);
            } else if (view == threeMonthsBtn) {
                mSelectedSubscriptionPeriod = subscriptionsKeys.get(1);
            } else if (view == sixMonthsBtn) {
                mSelectedSubscriptionPeriod = subscriptionsKeys.get(2);
            } else {
                mSelectedSubscriptionPeriod = subscriptionsKeys.get(3);
            }

            //TODO  Generate payload
            //TODO Construct the key from some strings at runtime
            //TODO amend the library
            // Payload is additional argument  =that you want google play  to pass back along with purchase information
            // product id = sku they are used to start purchase flow and other requests
            //getPurchases return all the active subscriptions

            //To upgrade or downgrade subscription I need to call getBuyIntentToReplaceSkus(). !!!!!!!!!!!!!!!!!!!!!!!!
            //his method is passed the new SKU the user wants to buy, and all the old SKUs that are superseded by it.
            //https://developer.android.com/google/play/billing/billing_subscriptions.html



            //https://www.youtube.com/watch?v=mnA0gaQWtAM&list=PLOU2XLYxmsIJ6j7lT1xoqANWEJd-YbZqI&index=7
            String payload = "";
            List<String> oldSkus = null;
            if (!TextUtils.isEmpty(currentSubscriptionPeriod)) {
                // The user currently has a valid subscription, any purchase action is going to
                // replace that subscription
                oldSkus = new ArrayList<>();
                oldSkus.add(currentSubscriptionPeriod);
            }


            try {
                mHelper.launchPurchaseFlow(MainActivity.this, mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
                        oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
            } catch (IabHelper.IabAsyncInProgressException e) {
                complain("Error launching purchase flow. Another async operation in progress.");
            }
        }
    };


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(Constants.APP_TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            if (mHelper == null) {
                return;
            }

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }
            Log.d(Constants.APP_TAG, "Purchase successful.");


            if (purchase != null) {
                for (String subscriptionKey : subscriptionsKeys) {
                    if (purchase.getSku().equalsIgnoreCase(subscriptionKey)) {
                        Log.d(Constants.APP_TAG, "Subscription purchased.");
                        alert("Thank you for subscription");
                        hasSubscription = true;
                        mAutoRenewEnabled = purchase.isAutoRenewing();
                        currentSubscriptionPeriod = purchase.getSku();
                    }
                }
            }
        }
    };


    boolean verifyDeveloperPayload(Purchase purchase) {
        String payload = purchase.getDeveloperPayload();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Constants.APP_TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        if (requestCode == RC_REQUEST && resultCode == RESULT_OK) {
            if (mHelper != null && !mHelper.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    void complain(String message) {
        Log.e(Constants.APP_TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(Constants.APP_TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    private String generateTocken() {
        String token = "token-";
        Random random = new Random();
        return token + random.nextInt(99999);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Constants.APP_TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }
}