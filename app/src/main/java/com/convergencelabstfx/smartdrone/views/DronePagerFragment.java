package com.convergencelabstfx.smartdrone.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.FragmentDronePagerBinding;
import com.google.android.material.tabs.TabLayoutMediator;


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

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mPagerAdapter = new DronePagerAdapter(getActivity());
        mBinding.pager.setAdapter(mPagerAdapter);
        new TabLayoutMediator(
                mBinding.tabLayout,
                mBinding.pager,
                (tab, position) -> {
                    switch (position) {
                        case DRONE_FRAG:
                            tab.setIcon(getResources().getDrawable(R.drawable.ic_drone_sound));
                            break;
                        case SETTINGS_FRAG:
                            tab.setIcon(getResources().getDrawable(R.drawable.ic_settings));
                            break;
                    }
                }
        ).attach();
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
                    return new DroneFragment();
                case SETTINGS_FRAG:
                    return new DroneSettingsFragment();
            }
            return new DroneFragment();
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

}