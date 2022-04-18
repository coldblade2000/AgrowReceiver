package com.pmc3.uniandes.agrowreceiver;

import static com.pmc3.uniandes.agrowreceiver.ui.home.HomeFragment.SERVER_PORT;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerThread extends Thread{


    private ServerSocket socket;
    private final int port;

    public ServerThread(ServerSocket socket, int port) {
        this.socket = socket;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new ServerSocket(SERVER_PORT);
            while ()
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
