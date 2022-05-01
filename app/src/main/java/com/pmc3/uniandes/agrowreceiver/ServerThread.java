package com.pmc3.uniandes.agrowreceiver;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.pmc3.uniandes.agrowreceiver.data.DataPacket;
import com.pmc3.uniandes.agrowreceiver.data.DataPacketDAO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread{
    private static final String TAG = "ServerThread";

    private ServerSocket socket;
    private final int port;
    private MutableLiveData<Boolean> isServerOn;
    private DataPacketDAO dataPacketDAO;

    public ServerThread(int port, MutableLiveData<Boolean> isServerOn, DataPacketDAO dataPacketDAO) {
        this.dataPacketDAO = dataPacketDAO;
        this.socket = null;
        this.port = port;
        this.isServerOn = isServerOn;
    }

    @Override
    public void run() {
        isServerOn.postValue(true);
        try {
            socket = new ServerSocket(port);
            while (isServerOn.getValue() != null && isServerOn.getValue()){
                Log.d(TAG, "Server socket waiting for connection");
                Socket clientSocket = socket.accept();
                if (clientSocket.isConnected()){
                    Log.d(TAG, "Client socket connected!");
                    ClientServerThread clientServerThread = new ClientServerThread(this, clientSocket);
                    clientServerThread.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void uploadData(DataPacket dataPacket){
        Log.d(TAG, "uploadData: " + dataPacket);
        dataPacketDAO.insertAll(dataPacket);
    }

    public void closeServer() throws IOException {
        socket.close();
    }

}
