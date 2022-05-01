package com.pmc3.uniandes.agrowreceiver.ui;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;

// https://www.techotopia.com/index.php/An_Android_Room_Database_and_Repository_Tutorial
import com.pmc3.uniandes.agrowreceiver.ServerThread;
import com.pmc3.uniandes.agrowreceiver.data.AppDatabase;
import com.pmc3.uniandes.agrowreceiver.data.DataPacketDAO;

import java.io.IOException;
import java.net.ServerSocket;

public class DataRepository {
    public static final int SERVER_PORT = 5678;

    public final MutableLiveData<Boolean> isServerOn;
    public AppDatabase database;
    DataPacketDAO dataPacketDAO;

    ServerThread serverThread;

    public DataRepository(Application application) {
        isServerOn = new MutableLiveData<>();
        isServerOn.setValue(false);

        database = AppDatabase.getDatabase(application);
        dataPacketDAO = database.dataPacketDAO;

    }

    public void turnServerOn() throws IOException {
        serverThread = new ServerThread(SERVER_PORT, isServerOn, dataPacketDAO);
        isServerOn.setValue(true);
    }

    public void turnServerOff() throws InterruptedException, IOException {
        isServerOn.setValue(false);
        serverThread.closeServer();

    }



}
