package com.convergencelabstfx.smartdrone.v2.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.convergencelabstfx.smartdrone.v2.models.SignalProcessorKt;

public class DroneViewModel extends ViewModel {

    // todo: remove; just a place holder field
    public MutableLiveData<String> mTestField = new MutableLiveData<>("test");

    private SignalProcessorKt mSignalProcessor = new SignalProcessorKt();

    public DroneViewModel() {
        // empty, for now
    }

    public SignalProcessorKt getSignalProcessor() {
        return mSignalProcessor;
    }

    public void setSignalProcessor(SignalProcessorKt signalProcessor) {
        mSignalProcessor = signalProcessor;
    }
}
