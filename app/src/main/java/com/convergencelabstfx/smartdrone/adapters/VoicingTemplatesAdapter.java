package com.convergencelabstfx.smartdrone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.convergencelabstfx.keyfinder.harmony.VoicingTemplate;
import com.convergencelabstfx.smartdrone.R;
import com.convergencelabstfx.smartdrone.databinding.VoicingTemplateItemBinding;

import java.util.ArrayList;

public class VoicingTemplatesAdapter extends ArrayAdapter<VoicingTemplate> {

    public VoicingTemplatesAdapter(Context context, ArrayList<VoicingTemplate> templates) {
        super(context, 0, templates);
    }

    @NonNull
    @Override
    public View getView(int i, View view, @NonNull ViewGroup container) {
        // todo: instantiate view
        final VoicingTemplateItemBinding binding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(getContext()),
                        R.layout.voicing_template_item,
                        container,
                        false);
        final VoicingTemplate curTemplate = this.getItem(i);
        final StringBuilder sb = new StringBuilder();
        if (curTemplate.getBassTones().size() != 0) {
            sb.append("Bass: ");
            sb.append(curTemplate.getBassTones().get(0));
            for (int ix = 1; ix < curTemplate.getBassTones().size(); i++ ) {
                sb.append(", ");
                sb.append(curTemplate.getBassTones().get(i));
            }
            sb.append('\n');
        }
        if (curTemplate.getChordTones().size() != 0) {
            sb.append("Chord: ");
            sb.append(curTemplate.getChordTones().get(0));
            for (int ix = 1; ix < curTemplate.getChordTones().size(); i++ ) {
                sb.append(", ");
                sb.append(curTemplate.getChordTones().get(i));
            }
        }

        binding.textView.setText(sb.toString());
        return binding.getRoot();
    }

}
