package com.pmc3.uniandes.agrowreceiver;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import com.pmc3.uniandes.agrowreceiver.ui.DataRepository;

import java.io.IOException;

public class MainViewModel extends ViewModel {

    public DataRepository repository;

    public MainViewModel(Application application) {
        this.repository = new DataRepository(application);
    }

    public void serverStart() throws IOException {
        repository.turnServerOn();
    }

    public void serverStop() throws IOException, InterruptedException {
        repository.turnServerOff();
    }
}
