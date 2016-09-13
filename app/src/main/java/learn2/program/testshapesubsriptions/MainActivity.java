package learn2.program.testshapesubsriptions;

import android.annotation.SuppressLint;
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

import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;
import learn2.program.testshapesubsriptions.billing_util.Inventory;
import learn2.program.testshapesubsriptions.billing_util.Purchase;

public class MainActivity extends AppCompatActivity {

    private Button oneMonthBtn;

    private Button threeMonthsBtn;

    private Button sixMonthsBtn;

    private String mInfiniteGasSku = "";

    private String mSelectedSubscriptionPeriod;

    private boolean mSubscribedToInfiniteGas;

    private ImageView pearImgView;

    private List<String> subscriptionsList;

    private List<Button> buttons;

    boolean mAutoRenewEnabled = false;

    private String ONE_MONTH = "learn2.program.testsubscriptiononemonth";
    private String THREE_MONTHS = "learn2.program.testsubscriptionthreemonth";
    private String SIX_MONTHS = "learn2.program.testsubscriptionsixmonth";
    private String ONE_YEAR = "learn2.program.testsubscriptiononeyear";

    private static final int RC_REQUEST = 17323;
    private IabHelper mHelper;
    //String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvgtPCE24JN71QUFgdFde8Zu7mop3ozX4SWP3xd8s8xf1Dqov/0MUg957NcfAhMWkggFMn0DTByPV9lS68/tfrIPgkX96VQz9jluvH++kGfpkfQV3WfGbV7dGHNwD9gqMuZRy7V3efZZXrc1ewdut6MZE97i5lpo5lp3HVyfjnCl3yLmI/Di6l+UdKU+ZERkWOT1ONeww+lP71gz0UweGegyfbXDmYso33HW7bJcpjLmv9x3yhrbEqNXgalNFA1TQtS4sdh1O20m36xoKp4q1E35QPnM7Hg5/8qDPOF5k31OQHogigjRTSWTmiYVN5ynXM70viFBX65g0SNYtHh/emwIDAQAB";
    String base44EncodedPublicKey ="BAQADIwme/hHtYNS0g56XBFiv07MXny5NVYimTWSTRjgigoHQO13k5FOPDq8/5gH7MnPQ53E1q4pKox63m02O1hds4StQT1AFNlagXNqEbrhy3x9vmLjpcJb7WH33osYmDXbfygeGewU0zg17Pl+wweNO1TOWkREZ+UKdU+l6iD/ImLy3lCnjfyVH3pl5opl5i79EZM6tudwe1crXZZfe3V7yRZuMqg9DwNHGd7VbGfW3VQfkpfGk++Hvulj9zQV69XkgPIrft/86Sl9VPyBTD0nMFggkWMhAfcN759gUM0/voqD1fx8s8dx3PWS4Xzo3pom7uZ8edFdgFUQ17NJ42ECPtgvAEQACKgCBIIMA8QACOAAFEQAB0w9GikhqkgBNAj";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oneMonthBtn = (Button) findViewById(R.id.oneMonthBtn);
        threeMonthsBtn = (Button) findViewById(R.id.threeMonthsBtn);
        sixMonthsBtn = (Button) findViewById(R.id.sixMonthsBtn);
        final Button oneYearBtn = (Button) findViewById(R.id.oneYearBtn);
        pearImgView = (ImageView) findViewById(R.id.pearImgView);

        subscriptionsList = Arrays.asList(ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR);
        buttons = Arrays.asList(oneMonthBtn, threeMonthsBtn, sixMonthsBtn, oneYearBtn);

        mHelper = new IabHelper(this, getBit() +revertKey(base44EncodedPublicKey));

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
                    mHelper.queryInventoryAsync(true, null, subscriptionsList, mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });

        if (mSubscribedToInfiniteGas) {
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

            for (String id : subscriptionsList) {
                Purchase purchase = inventory.getPurchase(id);
                if (purchase != null && purchase.isAutoRenewing()) {
                    mInfiniteGasSku = id;
                    mAutoRenewEnabled = true;
                    mSubscribedToInfiniteGas = verifyDeveloperPayload(purchase);
                    break;
                } else {
                    mInfiniteGasSku = "";
                    mAutoRenewEnabled = false;
                }
            }

            if (mSubscribedToInfiniteGas) {
                pearImgView.setVisibility(View.VISIBLE);
            }


            for (int i = 0; i < subscriptionsList.size(); i++) {

                if (inventory.getSkuDetails(subscriptionsList.get(i)) != null) {
                    String price = inventory.getSkuDetails(subscriptionsList.get(i)).getPrice() + " /per month";
                    buttons.get(i).setText(price);
                    buttons.get(i).setOnClickListener(subscriptionCL);
                }
            }
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


            if (purchase.getSku().equals(ONE_MONTH) || purchase.getSku().equals(THREE_MONTHS)
                    || purchase.getSku().equals(SIX_MONTHS) || purchase.getSku().equals(ONE_YEAR)) {

                alert("Thank you for subscription");
                mSubscribedToInfiniteGas = true;
                mAutoRenewEnabled = purchase.isAutoRenewing();
                mSelectedSubscriptionPeriod = purchase.getSku();
                pearImgView.setVisibility(View.VISIBLE);
            }
        }
    };


    boolean verifyDeveloperPayload(Purchase purchase) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Constants.APP_TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }
    private String revertKey(String key){
        StringBuilder builder = new StringBuilder(key).reverse();
        Log.d(Constants.APP_TAG,builder.toString());
        return builder.toString();
    }

   private String getBit(){
       return "MIIBI";
   }
}