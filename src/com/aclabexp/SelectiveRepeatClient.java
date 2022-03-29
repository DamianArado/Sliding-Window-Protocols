package com.aclabexp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class SelectiveRepeatClient
{
    static Socket connection;

    public static void main(String[] args) throws SocketException
    {
        try {
            int[] frames = new int[8];
            int n = 0;
            InetAddress address = InetAddress.getByName("Localhost");
            System.out.println(address);
            // new socket is created to this address (IP address of the server)
            connection = new Socket(address, 8777);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());

            // reading the length (number of frames)
            int framesNum = dataInputStream.read();
            System.out.println("Number of frames: " + framesNum);

            for (int i = 0; i < framesNum; i++) {
                // reading, storing and printing
                frames[i] = dataInputStream.read();
                System.out.println(frames[i]);
            }
            // changing contents of frame number 5, to show error
            frames[5] = -1;
            for (int i = 0; i < framesNum; i++) {
                // sending acknowledgement
                System.out.println("Received frame is: " + frames[i]);
            }

            for (int i = 4; i < framesNum; i++) {
                // negative ACK
                if(frames[i] == -1) {
                    System.out.println("Request for retransmit packets from packet number: " + (i + 1) + " again!");
                    n = i;
                    // request retransmission
                    dataOutputStream.write(n);
                    dataOutputStream.flush();
                }
                System.out.println();
                frames[n] = dataInputStream.read();
                System.out.println("Received frame: " + frames[n]);

                System.out.println("Quiting...");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
