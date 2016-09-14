package learn2.program.testshapesubsriptions.subscriptions;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import learn2.program.testshapesubsriptions.Constants;
import learn2.program.testshapesubsriptions.R;


public class SubscriptionFragment extends Fragment {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private static final String COLOR_TAG = "ColorTag";

    private static final String PRICE_TAG = "price";

    private static final String PRICE_TWO = "priceTwo";

    public static SubscriptionFragment newInstance(String message, int color, String price, String priceTwo, String description) {
        SubscriptionFragment f = new SubscriptionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MESSAGE, message);
        bundle.putInt(COLOR_TAG, color);
        bundle.putString(PRICE_TAG, price);
        bundle.putString(PRICE_TWO, priceTwo);
        bundle.putString(Constants.DESCRIPTION_TAG, description);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        final View view = inflater.inflate(R.layout.subscription_item, container, false);
        if (arguments != null) {
            final String message = getArguments().getString(EXTRA_MESSAGE);
            final int color = getArguments().getInt(COLOR_TAG);
            final String price = getArguments().getString(PRICE_TAG);
            final String priceTwo = getArguments().getString(PRICE_TWO);
            final String description = getArguments().getString(Constants.DESCRIPTION_TAG);

            final TextView subscriptionPlanTV = (TextView) view.findViewById(R.id.subscriptionPlanTV);
            final Button buyBtn = (Button) view.findViewById(R.id.buyBtn);
            final TextView priceTV = (TextView) view.findViewById(R.id.priceTV);
            final TextView descriptionTV = (TextView) view.findViewById(R.id.descriptionTV);

            subscriptionPlanTV.setText(message);
            subscriptionPlanTV.setBackgroundColor(color);
            buyBtn.setBackgroundColor(color);
            priceTV.setText(Html.fromHtml(price + "<small><small><sup>" + priceTwo + "</sup></small></small>"));
            descriptionTV.setText(description);

            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(arguments.getString(EXTRA_MESSAGE))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });
        }
        return view;
    }
}