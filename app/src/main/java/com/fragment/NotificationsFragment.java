package com.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.android.cync.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ketul.patel on 8/1/16.
 */
public class NotificationsFragment extends BaseContainerFragment {
    Activity mActivity;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        Init(rootView);
//        txtLabel=(TextView) rootView.findViewById(R.id.label);
//        txtLabel.setText("Notification");

        // Inflate the layout for this fragment
        return rootView;
    }
    public void Init(View view){
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

//viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        viewPager.setOffscreenPageLimit(position);
//    }
//
//    @Override
//    public void onPageSelected(int position) {
////        viewPager.setOffscreenPageLimit(position);
//        viewPager.setCurrentItem(position);
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//
//    }
//});

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary_black));
        tabLayout.setSelectedTabIndicatorHeight(10);
        tabLayout.setupWithViewPager(viewPager);

//        System.out.println("Counting===" + tabLayout.getTabCount());
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity=activity;
    }


//    public void updateCount(int Ct)
//    {
//        ((NavigationDrawerActivity)getActivity()).UpdateNotifCount(Ct);
//    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new GeneralTabFragment(), "GENERAL");
        adapter.addFragment(new ChatTabFragment(), "CHAT");
        viewPager.setAdapter(adapter);


    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}