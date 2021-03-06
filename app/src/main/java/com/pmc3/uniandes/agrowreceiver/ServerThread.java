package com.pmc3.uniandes.agrowreceiver;

import android.app.Application;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pmc3.uniandes.agrowreceiver.data.DataPacket;
import com.pmc3.uniandes.agrowreceiver.data.DataPacketDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerThread extends Thread {
    private static final String TAG = "ServerThread";
    private final BroadcastThread broadcastThread;

    private ServerSocket socket;
    private final int port;
    private LiveData<Boolean> isServerOn;
    private DataPacketDAO dataPacketDAO;
    private Application application;
    public String broadcastMessage;

    public ServerThread(int port, LiveData<Boolean> isServerOn, DataPacketDAO dataPacketDAO, Application application) {
        this.dataPacketDAO = dataPacketDAO;
        this.application = application;
        this.socket = null;
        this.port = port;
        this.isServerOn = isServerOn;
        this.broadcastThread = null;
        broadcastMessage = "Example IP address";
    }

    @Override
    public void run() {
        setName("ServerThread");
        Log.d(TAG, "Condition status: " + (isServerOn.getValue() != null && isServerOn.getValue()));

//        manualSocketTest();
        try {
            Log.d(TAG, "run: Server socket bound on port "+ port);
            socket = new ServerSocket(port);
            socket.setReuseAddress(true);
            if (socket.getInetAddress() != null) {
                Log.d(TAG, "getINetAddress: " + socket.getInetAddress().toString());
            }
            BroadcastThread broadcastThread = new BroadcastThread(broadcastMessage, isServerOn, application);
            Log.d(TAG, "Initialized broadcast thread");
            broadcastThread.setName("Broadcast thread");
            broadcastThread.start();
            Log.d(TAG, "Started broadcast thread");
            Log.d(TAG, "Condition status: " + (isServerOn.getValue() != null && isServerOn.getValue()));
            while (isServerOn.getValue() != null && isServerOn.getValue()) {
                Log.d(TAG, "Server socket waiting for connection");
                Socket clientSocket = socket.accept();
                if (clientSocket.isConnected()) {
                    Log.d(TAG, "Client socket connected!");
                    ClientServerThread clientServerThread = new ClientServerThread(this, clientSocket);
                    clientServerThread.setName("Client name");
                    clientServerThread.start();
                }
            }
            closeServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void manualSockgetTest() {
        try {
            Log.d(TAG, "Running server");
            Socket socket = new Socket("192.168.81.145", 10000);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Log.d(TAG, "run: Outputting to laptop");
            out.println("Android sends to laptop");
            String input = in.readLine();
            Log.d(TAG, "input: "+ input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void uploadData(DataPacket dataPacket) {
        Log.d(TAG, "uploadData: " + dataPacket);
        dataPacketDAO.insertAll(dataPacket);
    }

    public void closeServer() throws IOException {
        Log.d(TAG, "closeServer: Closing server");
        socket.close();
    }

}
