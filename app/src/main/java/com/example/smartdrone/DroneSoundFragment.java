package com.example.smartdrone;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;


/**
 * Drone Sound Fragment.
 */
public class DroneSoundFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.drone_sound_preferences, rootKey);
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        TextView textView = new TextView(getActivity());
//        textView.setText(R.string.hello_blank_fragment);
//        return textView;
//    }

}
