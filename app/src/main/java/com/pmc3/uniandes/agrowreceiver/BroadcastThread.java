package com.pmc3.uniandes.agrowreceiver;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

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
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                Log.d("Enumeration", "     " + intf.getName() + " " + intf.getDisplayName());
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    Log.d("Enumeration", "        " + enumIpAddr.nextElement().toString());
                }
            }
            // HARDCODED NETWORK INTERFACE!
            NetworkInterface networkInterface = NetworkInterface.getByName("swlan0");
            if (networkInterface == null){
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            InetAddress desiredAddress = null;

            while (addresses.hasMoreElements()){
                InetAddress address = addresses.nextElement();
                if (!address.toString().contains(":")){
                    desiredAddress = address;
                    Log.d(TAG, "Found the address: "+ desiredAddress.toString());
                    break;
                }
            }

            message = "AGROW_COLLECTOR_IP="+desiredAddress.toString().trim().substring(1);

            Log.d(TAG, "Message: "+ message);

            InetAddress group = InetAddress.getByName("FF7E:230::1234");
            MulticastSocket multicastSocket = new MulticastSocket(25506);
            multicastSocket.joinGroup(group);
            Log.d(TAG, "run: Joined group");
            /*datagramSocket.setBroadcast(true);
            if (datagramSocket.getInetAddress() != null) {
                Log.d(TAG, "getINetAddress: " + datagramSocket.getInetAddress().toString());
            }
            if (datagramSocket.getLocalAddress() != null) {
                Log.d(TAG, "getLocalAddress: " + datagramSocket.getLocalAddress().toString());
            }*/
            while (isServerOn.getValue() != null && isServerOn.getValue()) {
                Log.d(TAG, "run: Broadcast iteration");
                byte[] buffer = message.getBytes();
                DatagramPacket packet
                        = new DatagramPacket(buffer, buffer.length, group, 25506);
                multicastSocket.send(packet);
                Log.d(TAG, "Sending multicast packet");
                Thread.sleep(5000);
            }
            multicastSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
