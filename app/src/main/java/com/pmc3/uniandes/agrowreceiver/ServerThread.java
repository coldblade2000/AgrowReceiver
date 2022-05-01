package com.pmc3.uniandes.agrowreceiver;

import android.net.wifi.WifiManager;
import android.util.Log;

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
    private MutableLiveData<Boolean> isServerOn;
    private DataPacketDAO dataPacketDAO;
    public String broadcastMessage;

    public ServerThread(int port, MutableLiveData<Boolean> isServerOn, DataPacketDAO dataPacketDAO) {
        this.dataPacketDAO = dataPacketDAO;
        this.socket = null;
        this.port = port;
        this.isServerOn = isServerOn;
        this.broadcastThread = null;
        broadcastMessage = "Example IP address";
    }

    @Override
    public void run() {
        isServerOn.postValue(true);
        /*try {
            Log.d(TAG, "Running server");
            Socket socket = new Socket("192.168.81.145", 10000);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Log.d(TAG, "run: Outputting to laptop");
            out.println("Android sends to laptop");
            String input = in.read();
            Log.d(TAG, "input: "+ input);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            socket = new ServerSocket(port);
            if (socket.getInetAddress() != null) {
                Log.d(TAG, "getINetAddress: " + socket.getInetAddress().toString());
            }
            BroadcastThread broadcastThread = new BroadcastThread(broadcastMessage, isServerOn);
            broadcastThread.start();
            while (isServerOn.getValue() != null && isServerOn.getValue()) {
                Log.d(TAG, "Server socket waiting for connection");
                Socket clientSocket = socket.accept();
                if (clientSocket.isConnected()) {
                    Log.d(TAG, "Client socket connected!");
                    ClientServerThread clientServerThread = new ClientServerThread(this, clientSocket);
                    clientServerThread.start();
                }
            }
            closeServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void uploadData(DataPacket dataPacket) {
        Log.d(TAG, "uploadData: " + dataPacket);
        dataPacketDAO.insertAll(dataPacket);
    }

    public void closeServer() throws IOException {
        socket.close();
    }

}
