package learn2.program.testshapesubsriptions;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private Button oneMonthBtn;

    private Button threeMonthsBtn;

    private Button sixMonthsBtn;

    private Button oneYearBtn;
    //=========================================================
    private Button buyAppleBtn;

    private Button eatAppleBtn;

    private ImageView appleImageView;

    private int apple;

    //=========================================================
    String mInfiniteGasSku = "";

    private String mSelectedSubscriptionPeriod;

    private boolean mSubscribedToInfiniteGas;

    private ImageView pearImgView;

    private boolean mAutoRenewEnabled;

    private String ONE_MONTH = "learn2.program.testsubscriptiononemonth";
    private String THREE_MONTHS = "learn2.program.testsubscriptionthreemonth";
    private String SIX_MONTHS = "learn2.program.testsubscriptionsixmonth";
    private String ONE_YEAR = "learn2.program.testsubscriptiononeyear";
    private String APPLE_SKU = "learn2.program.buyapple";

    private static final int RC_REQUEST = 17323;
    private IabHelper mHelper;
    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvgtPCE24JN71QUFgdFde8Zu7mop3ozX4SWP3xd8s8xf1Dqov/0MUg957NcfAhMWkggFMn0DTByPV9lS68/tfrIPgkX96VQz9jluvH++kGfpkfQV3WfGbV7dGHNwD9gqMuZRy7V3efZZXrc1ewdut6MZE97i5lpo5lp3HVyfjnCl3yLmI/Di6l+UdKU+ZERkWOT1ONeww+lP71gz0UweGegyfbXDmYso33HW7bJcpjLmv9x3yhrbEqNXgalNFA1TQtS4sdh1O20m36xoKp4q1E35QPnM7Hg5/8qDPOF5k31OQHogigjRTSWTmiYVN5ynXM70viFBX65g0SNYtHh/emwIDAQAB";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        oneMonthBtn = (Button) findViewById(R.id.oneMonthBtn);
        threeMonthsBtn = (Button) findViewById(R.id.threeMonthsBtn);
        sixMonthsBtn = (Button) findViewById(R.id.sixMonthsBtn);
        oneYearBtn = (Button) findViewById(R.id.oneYearBtn);
        pearImgView = (ImageView) findViewById(R.id.pearImgView);

        //=============================================================
        buyAppleBtn = (Button) findViewById(R.id.buyAppleBtn);
        eatAppleBtn = (Button) findViewById(R.id.eatAppleBtn);
        appleImageView = (ImageView) findViewById(R.id.appleImageView);

        buyAppleBtn.setOnClickListener(buyAppleCL);
        eatAppleBtn.setOnClickListener(consumeAppleCL);
        loadData();
        //=============================================================

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

                List<String> additionalSkuList = new ArrayList<>();
                additionalSkuList.add(ONE_MONTH);
                additionalSkuList.add(THREE_MONTHS);
                additionalSkuList.add(SIX_MONTHS);
                additionalSkuList.add(ONE_YEAR);

                try {
                    mHelper.queryInventoryAsync(true, null,additionalSkuList,
                            mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });

        if (!mSubscribedToInfiniteGas || !mAutoRenewEnabled) {
            //Display all buttons
        } else {
            if (mInfiniteGasSku.equalsIgnoreCase(ONE_MONTH)) {
                oneMonthBtn.setVisibility(View.GONE);
            } else if (mInfiniteGasSku.equalsIgnoreCase(THREE_MONTHS)) {
                threeMonthsBtn.setVisibility(View.GONE);
            } else if (mInfiniteGasSku.equalsIgnoreCase(SIX_MONTHS)) {
                sixMonthsBtn.setVisibility(View.GONE);
            } else if (mInfiniteGasSku.equalsIgnoreCase(ONE_YEAR)) {
                oneYearBtn.setVisibility(View.GONE);
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
            Purchase oneMonthPurchase = inventory.getPurchase(ONE_MONTH);
            Purchase threeMonthsPurchase = inventory.getPurchase(THREE_MONTHS);
            Purchase sixMonthsPurchase = inventory.getPurchase(SIX_MONTHS);
            Purchase oneYearPurchase = inventory.getPurchase(ONE_YEAR);

            if (oneMonthPurchase != null && oneMonthPurchase.isAutoRenewing()) {
                mInfiniteGasSku = ONE_MONTH;
                mAutoRenewEnabled = true;
            } else if (threeMonthsPurchase != null && threeMonthsPurchase.isAutoRenewing()) {
                mInfiniteGasSku = THREE_MONTHS;
                mAutoRenewEnabled = true;
            } else if (sixMonthsPurchase != null && sixMonthsPurchase.isAutoRenewing()) {
                mInfiniteGasSku = SIX_MONTHS;
                mAutoRenewEnabled = true;
            } else if (oneYearPurchase != null && oneYearPurchase.isAutoRenewing()) {
                mInfiniteGasSku = ONE_YEAR;
                mAutoRenewEnabled = true;
            } else {
                mInfiniteGasSku = "";
                mAutoRenewEnabled = false;
            }

            mSubscribedToInfiniteGas = (oneMonthPurchase != null && verifyDeveloperPayload(oneMonthPurchase))
                    || (threeMonthsPurchase != null && verifyDeveloperPayload(threeMonthsPurchase)
                    || (sixMonthsPurchase != null && verifyDeveloperPayload(sixMonthsPurchase))
                    || (oneYearPurchase != null && verifyDeveloperPayload(oneYearPurchase)));

            if (mSubscribedToInfiniteGas) {
                pearImgView.setVisibility(View.VISIBLE);
            }


            String oneMonthPrice = inventory.getSkuDetails(ONE_MONTH).getPrice();
            oneMonthBtn.setText("One Month         " + oneMonthPrice + " /per month");
            String threeMonthsPrice = inventory.getSkuDetails(THREE_MONTHS).getPrice();
            threeMonthsBtn.setText("Three Month         " + threeMonthsPrice + " /per month");
            String sixMonthsPrice = inventory.getSkuDetails(SIX_MONTHS).getPrice();
            sixMonthsBtn.setText("Six Month         " + sixMonthsPrice + " /per month");
            String oneYearPrice = inventory.getSkuDetails(ONE_YEAR).getPrice();
            oneYearBtn.setText("One Year         " + oneYearPrice + " /per month");

            //=================================================

            Purchase gasPurchase = inventory.getPurchase(APPLE_SKU);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                try {
                    mHelper.consumeAsync(inventory.getPurchase(APPLE_SKU), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming gas. Another async operation in progress.");
                }
                return;
            }

            updateUI();
        }
    };


    private final View.OnClickListener subscriptionCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!mHelper.subscriptionsSupported()) {
                complain("Subscriptions not supported on your device yet. Sorry!");
                return;
            }

            if (view == oneMonthBtn) {
                mSelectedSubscriptionPeriod = ONE_MONTH;
            } else if (view == threeMonthsBtn) {
                mSelectedSubscriptionPeriod = THREE_MONTHS;
            } else if (view == sixMonthsBtn) {
                mSelectedSubscriptionPeriod = SIX_MONTHS;
            } else {
                mSelectedSubscriptionPeriod = ONE_YEAR;
            }
            //TODO generate it
            String payload = "";
            List<String> oldSkus = null;
            if (!TextUtils.isEmpty(mInfiniteGasSku)
                    && !mInfiniteGasSku.equals(mSelectedSubscriptionPeriod)) {
                // The user currently has a valid subscription, any purchase action is going to
                // replace that subscription
                oldSkus = new ArrayList<>();
                oldSkus.add(mInfiniteGasSku);
            }

            //buy subscription
            try {
                mHelper.launchPurchaseFlow(MainActivity.this, mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
                        oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
            } catch (IabHelper.IabAsyncInProgressException e) {
                complain("Error launching purchase flow. Another async operation in progress.");
            }
            mSelectedSubscriptionPeriod = "";
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


            if (purchase.getSku().equals(ONE_MONTH)
                    || purchase.getSku().equals(THREE_MONTHS)
                    || purchase.getSku().equals(SIX_MONTHS)
                    || purchase.getSku().equals(ONE_YEAR)) {

                Log.d(Constants.APP_TAG, "Subscription purchased.");
                Log.d(Constants.APP_TAG, " PurchaseFinishedListener Scu: " + purchase.getSku());
                Log.d(Constants.APP_TAG, "PurchaseFinishedListener Order ID : " + purchase.getOrderId());
                Log.d(Constants.APP_TAG, "PurchaseFinishedListener DeveloperPayload: " + purchase.getDeveloperPayload());
                Log.d(Constants.APP_TAG, "PurchaseFinishedListener Type: " + purchase.getItemType());
                Log.d(Constants.APP_TAG, "PurchaseFinishedListener Purchase State: " + purchase.getPurchaseState());
                alert("Thank you for subscription");
                mSubscribedToInfiniteGas = true;
                mAutoRenewEnabled = purchase.isAutoRenewing();
                mSelectedSubscriptionPeriod = purchase.getSku();
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
        Log.e(Constants.APP_TAG, "****  Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(Constants.APP_TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    private String generatePayload() {
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

    //============================================================


    void saveData() {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt("apple", apple);
        editor.apply();
    }

    void loadData() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        apple = sp.getInt("apple", 0);
    }


    private final View.OnClickListener buyAppleCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                mHelper.launchPurchaseFlow(MainActivity.this, APPLE_SKU, RC_REQUEST, mPurchaseFinishedListener, "");
            } catch (IabHelper.IabAsyncInProgressException e) {
                complain("Error launching purchase flow. Another async operation in progress.");
            }
        }
    };


    private final View.OnClickListener consumeAppleCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (apple > 0) {
                apple--;
                saveData();
            }
            alert("Apple eaten");
        }
    };


    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                if (apple < 1) {
                    apple++;
                }
                saveData();
                updateUI();
            } else {
                complain("Error while consuming: " + result);
            }

        }
    };


    private void updateUI() {
        appleImageView.setVisibility(apple <= 0 ? View.GONE : View.VISIBLE);
    }


}


//TODO  Generate payload
//TODO Construct the key from some strings at runtime
//TODO amend the library
// Payload is additional argument  =that you want google play  to pass back along with purchase information
// product id = sku they are used to start purchase flow and other requests
//getPurchases return all the active subscriptions
// To test I need to copy the apk on my tablet and install it via File Manager

//To upgrade or downgrade subscription I need to call getBuyIntentToReplaceSkus(). !!!!!!!!!!!!!!!!!!!!!!!!
//his method is passed the new SKU the user wants to buy, and all the old SKUs that are superseded by it.
//https://developer.android.com/google/play/billing/billing_subscriptions.html


//https://www.youtube.com/watch?v=mnA0gaQWtAM&list=PLOU2XLYxmsIJ6j7lT1xoqANWEJd-YbZqI&index=7


//https://support.google.com/googleplay/android-developer/answer/3131213