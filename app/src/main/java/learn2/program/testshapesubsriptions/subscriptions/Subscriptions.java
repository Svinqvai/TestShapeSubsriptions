package learn2.program.testshapesubsriptions.subscriptions;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import learn2.program.testshapesubsriptions.Constants;
import learn2.program.testshapesubsriptions.MainActivity;
import learn2.program.testshapesubsriptions.R;
import learn2.program.testshapesubsriptions.ZoomOutPageTransformer;


public class Subscriptions extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent, final Bundle savedState) {

        final View view = inflater.inflate(R.layout.subscriptions, parent, false);
        final CustomViewPager pager = (CustomViewPager) view.findViewById(R.id.pager);
        pager.setClipToPadding(false);
        final List<Fragment> fragments = getFragments();
        final SubscriptionsAdapter pageAdapter = new SubscriptionsAdapter(getActivity().getSupportFragmentManager(), fragments);
        pager.setAdapter(pageAdapter);
        pager.setCurrentItem(1);
        pager.setPageTransformer(true, new ZoomOutPageTransformer(0.90f, 0.7f));


        return view;
    }

    private List<Fragment> getFragments() {
        final List<Fragment> fList = new ArrayList<>();
        final Resources res = getResources();
        fList.add(SubscriptionFragment.newInstance(res.getString(R.string.one_month), Color.rgb(251, 193, 85),
                "$3.", MainActivity.prices.get(0), res.getString(R.string.give_it_a_try)));
        fList.add(SubscriptionFragment.newInstance(res.getString(R.string.three_months), Color.rgb(200, 226, 106),
                "$2.", "99", res.getString(R.string.save_percents, 25)));
        fList.add(SubscriptionFragment.newInstance(res.getString(R.string.six_months), Color.rgb(26, 206, 233),
                "$2.", "49", res.getString(R.string.save_percents, 37)));
        fList.add(SubscriptionFragment.newInstance(res.getString(R.string.one_year), Color.rgb(85, 26, 139),
                "$1.", "99", res.getString(R.string.save_percents, 50)));
        return fList;
    }
}