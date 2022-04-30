package com.pmc3.uniandes.agrowreceiver.ui.home;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mServerStatus;
    private final MutableLiveData<String> mRecibidos;

    public HomeViewModel() {
        mServerStatus = new MutableLiveData<>();
        mServerStatus.setValue("Estado del servidor: ");
        mRecibidos = new MutableLiveData<>();
        mRecibidos.setValue("Paquetes recibidos: ");
    }

    public LiveData<String> getText() {
        return mServerStatus;
    }
}