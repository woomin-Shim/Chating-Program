package chapter11;

import java.io.*;
import java.net.*;

public class UDPDiscardClient {
	public static final int PORT = 13;
	public static String hostname = "localhost";

	public static void main(String args[]) {
		BufferedReader input;
		DatagramPacket packet;
		DatagramPacket receive_packet;
		DatagramSocket theSocket;
		try {
			InetAddress server = InetAddress.getByName(hostname);
			theSocket = new DatagramSocket();
			String theLine = new String("Thank you");
			byte[] data = theLine.getBytes("ASCII");
			
			packet = new DatagramPacket(data, data.length, server, PORT);
			theSocket.send(packet);
			
			receive_packet = new DatagramPacket(data, data.length);
			theSocket.receive(receive_packet);
			System.out.println(new String(receive_packet.getData()));	
		}catch(UnknownHostException e)
	{
			System.out.println(e);
		}catch(
	SocketException e)
	{
			System.out.println(e);
		}catch(
	IOException e)
	{
		System.out.println(e);
	}
}
}
