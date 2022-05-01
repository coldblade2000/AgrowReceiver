package com.pmc3.uniandes.agrowreceiver;

import android.provider.ContactsContract;
import android.util.Log;

import com.pmc3.uniandes.agrowreceiver.data.DataPacket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientServerThread extends Thread{
    private static final String TAG = "ClientServerThread";

    private PrintWriter out;
    private BufferedReader in;
    private ServerThread serverThread;
    private Socket clientSocket;

    public ClientServerThread(ServerThread serverThread, Socket clientSocket) {
        this.serverThread = serverThread;

        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            /*out.println("IDENTIFY");
            String identification = in.readLine();*/
            out.println("SEND_DATA");
            StringBuilder sb = new StringBuilder();

            String line;
            while ( (line = in.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            String jsonPayload = sb.toString();
            Log.d(TAG, "JSON payload received");
            out.println("DATA_RECEIVED");
            clientSocket.close();
            Log.d(TAG, "Client socket closed");
            try {
                JSONObject jsonObject = new JSONObject(jsonPayload);
                try {
                    DataPacket dataPacket = new DataPacket();
                    dataPacket.deviceID = jsonObject.getString("deviceID");
                    dataPacket.dataHash = jsonObject.getString("dataHash");
                    dataPacket.payloadJSON = jsonObject.getString("payloadJSON");
                    serverThread.uploadData(dataPacket);
                } catch (JSONException e) {
                    Log.e(TAG, "Alguno de los datos del JSON no se encontraron");
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                Log.e(TAG, "JSON malformado");
                e.printStackTrace();
            }

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
