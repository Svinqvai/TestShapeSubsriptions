package learn2.program.testshapesubsriptions.nav;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import learn2.program.testshapesubsriptions.R;
import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;
import learn2.program.testshapesubsriptions.billing_util.Inventory;
import learn2.program.testshapesubsriptions.billing_util.Purchase;
import learn2.program.testshapesubsriptions.billing_util.SkuDetails;
import learn2.program.testshapesubsriptions.subscriptions.CustomViewPager;
import learn2.program.testshapesubsriptions.subscriptions.SubscriptionFragment;
import learn2.program.testshapesubsriptions.subscriptions.SubscriptionsAdapter;

public class SubscriptionActivity extends BaseActivity implements SubscriptionFragment.SubscriptionClickedListener {

    private String subscriptionKey = "";

    private String mSelectedSubscriptionPeriod;

    private boolean hasSubscription;

    private ImageView pearImgView;

    boolean mAutoRenewEnabled = false;

    private CustomViewPager pager;

    private long purchaseDate;

    public ArrayList<String> prices;

    private List<Fragment> fragments;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscriptions);

        prices = new ArrayList<>();

        pager = (CustomViewPager) findViewById(R.id.pager);

        pearImgView = (ImageView) findViewById(R.id.pearImgView);

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

                try {
                    mHelper.queryInventoryAsync(true, null, subscriptionsList, mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    alert("Error querying inventory. Another async operation in progress.");
                }
            }
        });

        //There was no other way to make view pager to show all 4 pages.
        new Thread(new Runnable() {
            @Override
            public void run() {
                pager.setClipToPadding(false);
                fragments = getFragments();
                final SubscriptionsAdapter pageAdapter = new SubscriptionsAdapter(getSupportFragmentManager(), fragments);
                pager.setAdapter(pageAdapter);
                pager.setCurrentItem(1);
                pager.setPageTransformer(true, new ZoomOutPageTransformer(0.90f, 0.7f));
            }
        }).start();
    }


    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (mHelper == null) {
                return;
            }

            if (result.isFailure()) {
                alert("Failed to query inventory: " + result);
                return;
            }
            Log.d(Constants.APP_TAG, "Query inventory was successful.");

            for (String key : subscriptionsList) {
                Purchase purchase = inventory.getPurchase(key);
                if (purchase != null && purchase.isAutoRenewing()) {
                    subscriptionKey = key;
                    mAutoRenewEnabled = true;
                    hasSubscription = verifyDeveloperPayload(purchase);
                    purchaseDate = purchase.getPurchaseTime();
                    break;
                } else {
                    subscriptionKey = "";
                    mAutoRenewEnabled = false;
                }
            }
            //TODO remove if below left for test purposes
            if (hasSubscription) {
                pearImgView.setVisibility(View.VISIBLE);
            }

            for (int i = 0; i < subscriptionsList.size(); i++) {
                final SkuDetails details = inventory.getSkuDetails(subscriptionsList.get(i));
                if (details != null) {
                    prices.add(details.getPrice());
                }
            }

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(purchaseDate);
            cal.add(Calendar.DAY_OF_YEAR, 14);
            TextView trialPeriodTv = (TextView) findViewById(R.id.trialPeriodTv);
            trialPeriodTv.setText(cal.getTime().toString());


        }

    };


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(Constants.APP_TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (mHelper == null) {
                return;
            }

            if (result.isFailure()) {
                alert("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                alert("Error purchasing. Authenticity verification failed.");
                return;
            }
            Log.d(Constants.APP_TAG, "Purchase successful.");


            if (purchase.getSku().equals(ONE_MONTH) || purchase.getSku().equals(THREE_MONTHS)
                    || purchase.getSku().equals(SIX_MONTHS) || purchase.getSku().equals(ONE_YEAR)) {

                alert("Thank you for subscription");
                hasSubscription = true;
                mAutoRenewEnabled = purchase.isAutoRenewing();
                mSelectedSubscriptionPeriod = purchase.getSku();
                pearImgView.setVisibility(View.VISIBLE);
            }
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

    private List<Fragment> getFragments() {
        final List<Fragment> fragments = new ArrayList<>();
        final Resources res = getResources();
        fragments.add(SubscriptionFragment.newInstance(res.getString(R.string.one_month), Color.rgb(251, 193, 85),
                prices.get(0), res.getString(R.string.give_it_a_try)));
        fragments.add(SubscriptionFragment.newInstance(res.getString(R.string.three_months), Color.rgb(200, 226, 106),
                prices.get(1), res.getString(R.string.save_percents, 25)));
        fragments.add(SubscriptionFragment.newInstance(res.getString(R.string.six_months), Color.rgb(26, 206, 233),
                prices.get(2), res.getString(R.string.save_percents, 37)));
        fragments.add(SubscriptionFragment.newInstance(res.getString(R.string.one_year), Color.rgb(85, 26, 139),
                prices.get(3), res.getString(R.string.save_percents, 50)));
        return fragments;
    }

    @Override
    public void onSubscriptionClicked(int position) {
        if (!mHelper.subscriptionsSupported()) {
            alert("Subscriptions not supported on your device yet. Sorry!");
            return;
        }

        if (position == 1) {
            mSelectedSubscriptionPeriod = ONE_MONTH;
        } else if (position == 2) {
            mSelectedSubscriptionPeriod = THREE_MONTHS;
        } else if (position == 3) {
            mSelectedSubscriptionPeriod = SIX_MONTHS;
        } else {
            mSelectedSubscriptionPeriod = ONE_YEAR;
        }
        List<String> oldSkus = null;
        if (!TextUtils.isEmpty(subscriptionKey) && !subscriptionKey.equals(mSelectedSubscriptionPeriod)) {
            // The user currently has a valid subscription, any purchase action is going to replace that subscription
            oldSkus = new ArrayList<>();
            oldSkus.add(subscriptionKey);
        }

        //buy subscription
        try {
            mHelper.launchPurchaseFlow(SubscriptionActivity.this, mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
                    oldSkus, RC_REQUEST, mPurchaseFinishedListener, PAYLOAD);
        } catch (IabHelper.IabAsyncInProgressException e) {
            alert("Error launching purchase flow. Another async operation in progress.");
        }
        mSelectedSubscriptionPeriod = "";
    }
}