package com.pmc3.uniandes.agrowreceiver;

import static com.pmc3.uniandes.agrowreceiver.ui.DataRepository.BROADCAST_PORT;

import android.app.Application;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

public class BroadcastThread extends Thread {
    private static final String TAG = "BroadcastThread";
    private String message;
    private Application application;
    private InetAddress address;
    private LiveData<Boolean> isServerOn;

    public BroadcastThread(String message, LiveData<Boolean> isServerOn, Application application) throws UnknownHostException {
        this.message = message;
        this.application = application;
        this.address = InetAddress.getByName("255.255.255.255");
        this.isServerOn = isServerOn;
    }


    public void run() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                Log.d("Enumeration", "     " + intf.getName() + " " + intf.getDisplayName());
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    Log.d("Enumeration", "        " + enumIpAddr.nextElement().toString());
                }
            }
            // HARDCODED NETWORK INTERFACE!
            NetworkInterface networkInterface = NetworkInterface.getByName("swlan0");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            InetAddress desiredAddress = null;
            ArrayList<InetAddress> inetAddresses = new ArrayList<>();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                inetAddresses.add(address);
                Log.d(TAG, "Found the address: " + address.toString());
            }
            desiredAddress = inetAddresses.get(1);

            message = "AGROW_COLLECTOR_IP=" + desiredAddress.toString().trim().substring(1);

            Log.d(TAG, "Message: " + message);

            HashMap<String, InetAddress> sensorIDs = new HashMap<>();

            DatagramSocket serverSocket = new DatagramSocket(BROADCAST_PORT);
            byte[] receiveData = new byte[100];
            byte[] sendData = message.getBytes();


            while (isServerOn.getValue() != null && isServerOn.getValue()) {
                //Receives broadcast of possible server
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String( receivePacket.getData());
                if (!sentence.startsWith("AGROW_SENSOR_ID=")){
                    // Filters out packets that aren't AGROW sensors
                    continue;
                }
                sentence = sentence.substring(0, sentence.indexOf(0));
                Log.d(TAG, "RECEIVED: " + sentence);
                String sensorID = sentence.split("=")[1].trim();
                InetAddress IPAddress = receivePacket.getAddress();
                if (sensorIDs.containsKey(sensorID)){
                    continue;
                }else{
                    sensorIDs.put(sensorID, IPAddress);
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, BROADCAST_PORT+1);
                    serverSocket.send(sendPacket);
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo(); // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);

    }
}
