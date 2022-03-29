package com.aclabexp;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class GoBackNServer
{
    public static void main(String[] args) throws Exception
    {
        System.out.println(".............Server............");
        System.out.println(".....Waiting for connection.....");

        // we want to know the IP Address of the parameter localhost
        InetAddress ipAddress = InetAddress.getByName("Localhost");
        // create a server socket on Port 17700 which helps to establish communication with client
        ServerSocket serverSocket = new ServerSocket(17700);
        // create a client socket waiting for connection
        Socket client = new Socket();
        // server accepts the client request and connection is established
        client = serverSocket.accept();

        // For reading and writing using the socket
        BufferedInputStream inputStream = new BufferedInputStream(client.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());

        System.out.println("Received request for sending frames.");
        // reading from network: number of frames required by the client
        int x = inputStream.read();
        boolean[] frames = new boolean[x];

        // reading the choice
        int xChoice = inputStream.read();
        System.out.println("Sending......");

        // No Error: We send frames and wait for acknowledgement
        if (xChoice == 0) {
            for(int i = 0; i < x; ++i) {
                System.out.println("Sending frame number: " + i);
                // sending frame
                outputStream.write(i);
                outputStream.flush();
                System.out.println("Waiting for acknowledgement.");

                try {
                    // timer for thread to sleep to receive acknowledgement
                    Thread.sleep(7000);
                } catch (Exception e) {
                    System.out.println(e);
                }

                // received acknowledgement
                int ackNum = inputStream.read();
                System.out.println("Received acknowledgement for frame: " + i + " as " + ackNum);
            }
            // Error: Not sending the frames
        } else {
            for (int i = 0; i < x; ++i) {
                if (i == 2) {
                    System.out.println("Sending frame number: " + i);
                    // frame is not passed to the output stream of socket
                } else {
                    System.out.println("Sending frame number: " + i);
                    // writing on output stream of network
                    outputStream.write(i);
                    outputStream.flush();
                    System.out.println("Waiting for acknowledgement...");

                    try {
                        Thread.sleep(7000);
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    int ackNum = inputStream.read();
                    // received acknowledgement successfully,
                    if (ackNum != 255) {
                        System.out.println("Received ACK for frame number: " + i + " as " + ackNum);
                        frames[i] = true;
                    }
                }
            }
            // check which frames have not been ACKed
            for (int frameNum = 0; frameNum < x; ++frameNum) {
                // received negative ACK (-1) from client
                if (!frames[frameNum]) {
                    // resend all the frames from that moment
                    System.out.println("Resending frame: " + frameNum);
                    outputStream.write(frameNum);
                    outputStream.flush();
                    System.out.println("Waiting for acknowledgement");
                    try {
                        Thread.sleep(7000);
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    int ackNum = inputStream.read();
                    System.out.println("Received ACK for frame number: " + frameNum + " as " + ackNum);
                    frames[frameNum] = true;
                }
            }
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
        System.out.println("Quiting..");
    }
}
