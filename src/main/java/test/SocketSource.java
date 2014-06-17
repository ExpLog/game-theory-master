package test;

import java.io.*;
import java.net.Socket;

public class SocketSource {
	public static void main(String[] args) throws Exception{
		String host = "localhost";
		int port = 8080;
		Socket clientSocket = new Socket(host,port);
		
		BufferedReader reader = 
				new BufferedReader(
						new InputStreamReader(
								clientSocket.getInputStream()));

        DataOutputStream write = new DataOutputStream(
                clientSocket.getOutputStream());

		System.out.println(reader.readLine());
        write.writeUTF("name player1");

        while(true) {
            System.out.println(reader.readLine());
        }
	}
}