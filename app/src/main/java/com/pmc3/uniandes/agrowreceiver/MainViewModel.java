package com.pmc3.uniandes.agrowreceiver;

import android.app.Application;
import android.arch.lifecycle.ViewModel;

import com.pmc3.uniandes.agrowreceiver.ui.DataRepository;

public class MainViewModel extends ViewModel {

    public DataRepository repository;

    public MainViewModel(Application application) {
        this.repository = new DataRepository(application);
    }
}
