package com.pmc3.uniandes.agrowreceiver;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.pmc3.uniandes.agrowreceiver.ui.DataRepository;

import java.io.IOException;

public class MainViewModel extends AndroidViewModel {

    public DataRepository repository;
    public MutableLiveData<String> ipAddress;

    public MainViewModel(Application application) {
        super(application);
        this.repository = new DataRepository(application);

    }

    public void serverStart() throws IOException {
        repository.turnServerOn();
    }

    public void serverStop() throws IOException, InterruptedException {
        repository.turnServerOff();
    }
}
