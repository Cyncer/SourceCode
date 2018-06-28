package com.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.app.android.cync.R;


public class BaseContainerFragment extends Fragment {

    public void replaceFragment(Fragment fragment, boolean addToBackStack,
                                String tag) {

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }

        // if (getChildFragmentManager().getBackStackEntryCount() > 0) {
        // transaction.setCustomAnimations(R.anim.enter, R.anim.exit,
        // R.anim.pop_enter, R.anim.pop_exit);
        // }
//        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
//		transaction.setCustomAnimations(R.anim.slide_in_from_right,
//                R.anim.slide_out_to_left);
        transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        transaction.replace(R.id.container_body, fragment, tag);
        transaction.commit();
        getFragmentManager().executePendingTransactions();
    }

    public boolean popFragment() {
        boolean isPop = false;
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            isPop = true;
//            FragmentTransaction transaction = getFragmentManager()
//                    .beginTransaction();

            getFragmentManager().popBackStack();

//            transaction.setCustomAnimations(R.anim.slide_in_from_left,
//                    R.anim.slide_out_to_right);
        }
        return isPop;
    }

}
