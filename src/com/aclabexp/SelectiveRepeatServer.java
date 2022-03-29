package com.aclabexp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class SelectiveRepeatServer
{
    static ServerSocket serverSocket;
    static DataInputStream dataInputStream;
    static DataOutputStream dataOutputStream;

    public static void main(String[] args) throws SocketException
    {
        try {
            int[] packets = { 30, 40, 50, 60, 70, 80, 90, 100 };
            // create a server socket on port 8777
            serverSocket = new ServerSocket(8777);

            System.out.println("Waiting for connection...");
            // we are waiting for connection
            Socket client = serverSocket.accept();

            // for placing data on the input and output stream of the network communication
            dataInputStream = new DataInputStream(client.getInputStream());
            dataOutputStream = new DataOutputStream(client.getOutputStream());

            int packetsSent = packets.length;
            System.out.println("The number of packets sent: " + packetsSent);
            // length of the data which it has to write
            dataOutputStream.write(packetsSent);
            dataOutputStream.flush();

            for (int packet : packets) {
                // writing data one-by-one
                dataOutputStream.write(packet);
                dataOutputStream.flush();
            }

            // check which packet was asked to be retransmitted
            int x = dataInputStream.read();
            // writing that single packet
            dataOutputStream.write(packets[x]);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                dataInputStream.close();
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
