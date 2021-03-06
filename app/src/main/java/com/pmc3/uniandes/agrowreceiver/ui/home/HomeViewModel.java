package com.pmc3.uniandes.agrowreceiver.ui.home;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mServerStatus;



    private final MutableLiveData<String> mRecibidos;
    private final MutableLiveData<Boolean> isServerOn;

    public HomeViewModel() {
        mServerStatus = new MutableLiveData<>();
        mServerStatus.setValue("Estado del servidor: ");
        mRecibidos = new MutableLiveData<>();
        mRecibidos.setValue("Paquetes recibidos: ");
        isServerOn = new MutableLiveData<>();
        isServerOn.setValue(false);
    }

    public LiveData<String> getText() {
        return mServerStatus;
    }
    public MutableLiveData<String> getmRecibidos() {
        return mRecibidos;
    }

    public MutableLiveData<Boolean> getIsServerOn() {
        return isServerOn;
    }

}