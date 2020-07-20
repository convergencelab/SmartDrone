package com.convergencelabstfx.smartdrone.v2.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.FragmentDronePagerBinding;


public class DronePagerFragment extends Fragment {

    private static final int NUM_PAGES = 2;
    private static final int DRONE_FRAG = 0;
    private static final int SETTINGS_FRAG = 1;

    private FragmentStateAdapter mPagerAdapter;

    private FragmentDronePagerBinding mBinding;

    public DronePagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_drone_pager, container, false
        );
        mPagerAdapter = new DronePagerAdapter(getActivity());
        mBinding.pager.setAdapter(mPagerAdapter);

        return mBinding.getRoot();
    }

    private class DronePagerAdapter extends FragmentStateAdapter {

        public DronePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case DRONE_FRAG:
                    return new DroneFragment2();
                case SETTINGS_FRAG:
                    return new DroneSettingsFragment2();
            }
            return new DroneFragment2();
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

}