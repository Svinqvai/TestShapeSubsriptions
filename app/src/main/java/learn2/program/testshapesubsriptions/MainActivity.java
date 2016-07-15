package learn2.program.testshapesubsriptions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.IabResult;


public class MainActivity extends AppCompatActivity {

    Button oneMonthBtn;

    Button threeMonthsBtn;

    Button sixMonthsBtn;

    Button oneYearBtn;

    ImageView pearImgView;

    private int itemClicked;


    private static final int RC_REQUEST = 10001;
    private IabHelper mHelper;
    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvgtPCE24JN71QUFgdFde8Zu7mop3ozX4SWP3xd8s8xf1Dqov/0MUg957NcfAhMWkggFMn0DTByPV9lS68/tfrIPgkX96VQz9jluvH++kGfpkfQV3WfGbV7dGHNwD9gqMuZRy7V3efZZXrc1ewdut6MZE97i5lpo5lp3HVyfjnCl3yLmI/Di6l+UdKU+ZERkWOT1ONeww+lP71gz0UweGegyfbXDmYso33HW7bJcpjLmv9x3yhrbEqNXgalNFA1TQtS4sdh1O20m36xoKp4q1E35QPnM7Hg5/8qDPOF5k31OQHogigjRTSWTmiYVN5ynXM70viFBX65g0SNYtHh/emwIDAQAB";

    private static List<String> subscriptionsKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subscriptionsKeys = Arrays.asList("learn2.program.testSubscriptionOneMonth", "learn2.program.testSubscriptionThreeMonth", "learn2.program.testSubscriptionSixMonth", "learn2.program.testSubscriptionOneYear");

        oneMonthBtn = (Button) findViewById(R.id.oneMonthBtn);
        threeMonthsBtn = (Button) findViewById(R.id.threeMonthsBtn);
        sixMonthsBtn = (Button) findViewById(R.id.sixMonthsBtn);
        oneYearBtn = (Button) findViewById(R.id.oneYearBtn);
        pearImgView = (ImageView) findViewById(R.id.pearImgView);


        oneMonthBtn.setOnClickListener(subscriptionCL);
        threeMonthsBtn.setOnClickListener(subscriptionCL);
        sixMonthsBtn.setOnClickListener(subscriptionCL);
        oneYearBtn.setOnClickListener(subscriptionCL);
        pearImgView.setOnClickListener(imageViewCL);

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
                mHelper.queryInventoryAsync(true, subscriptionsKeys, new SubscriptionOnQueryInventoryFinished(subscriptionsKeys));

            }
        });
    }


    private final View.OnClickListener subscriptionCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view == oneMonthBtn) {
               // Toast.makeText(MainActivity.this, "One Month", Toast.LENGTH_SHORT).show();
                itemClicked = 0;
            } else if (view == threeMonthsBtn) {
             //   Toast.makeText(MainActivity.this, "Three Months", Toast.LENGTH_SHORT).show();
                itemClicked = 1;
            } else if (view == sixMonthsBtn) {
          //      Toast.makeText(MainActivity.this, "Six Months", Toast.LENGTH_SHORT).show();
                itemClicked = 2;
            } else {
          //      Toast.makeText(MainActivity.this, "One Year", Toast.LENGTH_SHORT).show();
                itemClicked = 3;
            }
            buy();
        }
    };

    private final View.OnClickListener imageViewCL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
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
            mHelper.launchSubscriptionPurchaseFlow(MainActivity.this, subscriptionsKeys.get(itemClicked), RC_REQUEST, new SubscriptionPurchaseListener(subscriptionsKeys, mHelper,itemClicked), "token" + 3232);
            pearImgView.setVisibility(View.VISIBLE);
        }
    }

    private void consume() {
        if (mHelper != null && mHelper.subscriptionsSupported()) {
            mHelper.queryInventoryAsync(new SubscriptionOnQueryInventoryFinished(subscriptionsKeys));
            pearImgView.setVisibility(View.GONE);
        }
    }
}
