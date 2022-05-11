package com.pmc3.uniandes.agrowreceiver.ui;

import android.app.Application;

// https://www.techotopia.com/index.php/An_Android_Room_Database_and_Repository_Tutorial
import androidx.lifecycle.MutableLiveData;

import com.pmc3.uniandes.agrowreceiver.ServerThread;
import com.pmc3.uniandes.agrowreceiver.data.AppDatabase;
import com.pmc3.uniandes.agrowreceiver.data.DataPacketDAO;

import java.io.IOException;

public class DataRepository {
    public static final int SERVER_PORT = 12345;

    public final MutableLiveData<Boolean> isServerOn;
    public AppDatabase database;
    DataPacketDAO dataPacketDAO;

    ServerThread serverThread;
    private Application application;

    public DataRepository(Application application) {
        this.application = application;
        isServerOn = new MutableLiveData<>();
        isServerOn.postValue(false);

        database = AppDatabase.getDatabase(application);
        dataPacketDAO = database.dataPacketDAO;

    }

    public void turnServerOn() throws IOException {
        serverThread = new ServerThread(SERVER_PORT, isServerOn, dataPacketDAO, application);
        isServerOn.postValue(true);
        serverThread.start();
    }

    public void turnServerOff() throws InterruptedException, IOException {
        isServerOn.postValue(false);
        serverThread.closeServer();

    }



}
