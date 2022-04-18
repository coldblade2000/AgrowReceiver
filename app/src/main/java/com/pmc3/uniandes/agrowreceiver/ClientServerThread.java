package com.pmc3.uniandes.agrowreceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientServerThread extends Thread{

    private PrintWriter out;
    private BufferedReader in;
    private Socket clientSocket;

    public ClientServerThread(Socket clientSocket) {

        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.println("IDENTIFY");
            String identification = in.readLine();
            out.println("SEND_DATA");
            StringBuilder sb = new StringBuilder();

            String line;
            while ( (line = in.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            String jsonPayload = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws IOException{
        in.close();
        out.close();
        clientSocket.close();
    }
}
