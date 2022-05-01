package com.pmc3.uniandes.agrowreceiver;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class BroadcastThread extends Thread {
    private static final String TAG = "BroadcastThread";
    private String message;
    private InetAddress address;
    private LiveData<Boolean> isServerOn;

    public BroadcastThread(String message, LiveData<Boolean> isServerOn) throws UnknownHostException {
        this.message = message;
        this.address = InetAddress.getByName("255.255.255.255");
        this.isServerOn = isServerOn;
    }


    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            if (socket.getInetAddress() != null) {
                Log.d(TAG, "getINetAddress: " + socket.getInetAddress().toString());
            }
            if (socket.getLocalAddress() != null) {
                Log.d(TAG, "getLocalAddress: " + socket.getLocalAddress().toString());
            }
            while (isServerOn.getValue() != null && isServerOn.getValue()) {

                byte[] buffer = message.getBytes();
                DatagramPacket packet
                        = new DatagramPacket(buffer, buffer.length, address, 4445);
                socket.send(packet);
                Log.d(TAG, "Sending broadcast packet");
                Thread.sleep(5000);
            }
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
