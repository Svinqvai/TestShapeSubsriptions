package learn2.program.testshapesubsriptions.nav;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import learn2.program.testshapesubsriptions.billing_util.IabHelper;
import learn2.program.testshapesubsriptions.billing_util.Purchase;

public class BaseActivity extends AppCompatActivity {

    protected List<String> subscriptionsList;
    protected String ONE_MONTH = "learn2.program.testsubscriptiononemonth";
    protected String THREE_MONTHS = "learn2.program.testsubscriptionthreemonth";
    protected String SIX_MONTHS = "learn2.program.testsubscriptionsixmonth";
    protected String ONE_YEAR = "learn2.program.testsubscriptiononeyear";

    protected static final int RC_REQUEST = 17323;
    protected IabHelper mHelper;
    protected  String PAYLOAD = "";
    protected String base44EncodedPublicKey = "BAQADIwme/hHtYNS0g56XBFiv07MXny5NVYimTWSTRjgigoHQO13k5FOPDq8/5gH7MnPQ53E1q4pKox63m02O1hds4StQT1AFNlagXNqEbrhy3x9vmLjpcJb7WH33osYmDXbfygeGewU0zg17Pl+wweNO1TOWkREZ+UKdU+l6iD/ImLy3lCnjfyVH3pl5opl5i79EZM6tudwe1crXZZfe3V7yRZuMqg9DwNHGd7VbGfW3VQfkpfGk++Hvulj9zQV69XkgPIrft/86Sl9VPyBTD0nMFggkWMhAfcN759gUM0/voqD1fx8s8dx3PWS4Xzo3pom7uZ8edFdgFUQ17NJ42ECPtgvAEQACKgCBIIMA8QACOAAFEQAB0w9GikhqkgBNAj";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptionsList = Arrays.asList(ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR);
        mHelper = new IabHelper(this, getBit() + revertKey(base44EncodedPublicKey));
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

    protected String revertKey(String key) {
        StringBuilder builder = new StringBuilder(key).reverse();
        Log.d(Constants.APP_TAG, builder.toString());
        return builder.toString();
    }

    protected String getBit() {
        return "MIIBI";
    }

  protected   void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(Constants.APP_TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }


    protected boolean verifyDeveloperPayload(Purchase purchase) {
        return purchase != null && PAYLOAD.equalsIgnoreCase(purchase.getDeveloperPayload());
    }
}
