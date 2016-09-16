package learn2.program.testshapesubsriptions.subscriptions;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import learn2.program.testshapesubsriptions.nav.Constants;
import learn2.program.testshapesubsriptions.R;


public class SubscriptionFragment extends Fragment {

    private static final String TITLE = "title";

    private static final String COLOR_TAG = "ColorTag";

    private static final String PRICE_TAG = "price";
    public static final int CURRENCY_LENGTH = 3;

    private SubscriptionClickedListener mCallBack;

    private Resources res;

    public static SubscriptionFragment newInstance(final String message, final int color, final String price, final String description) {
        SubscriptionFragment f = new SubscriptionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, message);
        bundle.putInt(COLOR_TAG, color);
        bundle.putString(PRICE_TAG, price);
        bundle.putString(Constants.DESCRIPTION_TAG, description);
        f.setArguments(bundle);
        return f;
    }

    public interface SubscriptionClickedListener {
        void onSubscriptionClicked(int position);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        res = context.getResources();
        try {
            mCallBack = (SubscriptionClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        final View view = inflater.inflate(R.layout.subscription_item, container, false);
        if (arguments != null) {
            final String title = arguments.getString(TITLE);
            final int color = arguments.getInt(COLOR_TAG);
            final String description = arguments.getString(Constants.DESCRIPTION_TAG);

            final TextView subscriptionPlanTV = (TextView) view.findViewById(R.id.subscriptionPlanTV);
            final Button buyBtn = (Button) view.findViewById(R.id.buyBtn);
            final TextView priceTV = (TextView) view.findViewById(R.id.priceTV);
            final TextView descriptionTV = (TextView) view.findViewById(R.id.descriptionTV);

            subscriptionPlanTV.setText(title);
            subscriptionPlanTV.setBackgroundColor(color);
            buyBtn.setBackgroundColor(color);

            final String priceWithOutCurrency = arguments.getString(PRICE_TAG).substring(CURRENCY_LENGTH);
            final String currency = arguments.getString(PRICE_TAG).substring(0, CURRENCY_LENGTH);
            final String[] price = priceWithOutCurrency.split("\\.");

            final StringBuilder sb = new StringBuilder();
            sb.append(price[0]).append(".<small><small><small><sup>").append(price[1]).append(" ").append(currency).append("</sup></small></small></small>");

            priceTV.setText(Html.fromHtml(sb.toString()));
            descriptionTV.setText(description);

            buyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position;
                    if (title.equals(res.getString(R.string.one_month))) {
                        position = 1;
                    } else if (title.equals(res.getString(R.string.three_months))) {
                        position = 2;
                    } else if (title.equals(res.getString(R.string.six_months))) {
                        position = CURRENCY_LENGTH;
                    } else  {
                        position = 4;
                    }
                    mCallBack.onSubscriptionClicked(position);
                }
            });
        }
        return view;
    }
}