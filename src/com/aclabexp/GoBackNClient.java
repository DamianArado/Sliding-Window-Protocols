package com.aclabexp;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.out;

public class GoBackNClient
{
    public static void main(String[] args) throws IOException
    {
        // get ip address from the host name
        InetAddress address = InetAddress.getByName("Localhost");
        out.println(address);

        // create a socket on the ip address and the same port as that of the server
        Socket connection = new Socket(address, 17700);

        // For communication using socket
        BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        // accept user input from the console
        Scanner scanner = new Scanner(System.in);

        System.out.println("...........Client...........");
        System.out.println("Connect");
        System.out.println("Enter the number of frames to be requested to the server");
        int framesNum = scanner.nextInt();

        outputStream.write(framesNum);
        outputStream.flush();

        System.out.println("Enter the type of transmission: Error = 1, No Error = 0");
        int choice = scanner.nextInt();
        outputStream.write(choice);

        int check = 0, i, j;

        // No Error
        if (choice == 0) {
            for (j = 0; j < framesNum; ++j) {
                // reading frame
                i = inputStream.read();
                System.out.println("Received frame number: " + i);
                System.out.println("Sending acknowledgement for frame number: " + i);
                // sending acknowledgement
                outputStream.write(i);
                outputStream.flush();
            }
            outputStream.flush();
        // Error
        } else {
            for (j = 0; j < framesNum; ++j) {
                // receive from the network stream
                i = inputStream.read();

                if (i == check) {
                    // frame received successfully
                    System.out.println("i: " + i + ", check: " + check);
                    System.out.println("Received frame number: " + i);
                    System.out.println("Sending acknowledgement for frame number: " + i);
                    outputStream.write(i);
                    ++check;
                } else {
                    --j;
                    System.out.println("Discarded frame number: " + i);
                    System.out.println("Sending negative ACK");
                    outputStream.write(-1);
                }
                outputStream.flush();
            }
        }
        inputStream.close();
        outputStream.close();
        System.out.println("Quiting..");
    }
}
