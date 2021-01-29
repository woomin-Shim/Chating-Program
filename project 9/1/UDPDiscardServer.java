package chapter11;
import java.net.*;
import java.util.*;
import java.io.*;

public class UDPDiscardServer {
	public static final int PORT = 13;
	public static final int MAX_PACKET_SIZE = 65508;
	public static void main(String args[]) {
		DatagramPacket send_packet;
		Date now;
		DatagramSocket theSocket;
		byte[] buffer = new byte[MAX_PACKET_SIZE];
		try {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			theSocket = new DatagramSocket(PORT);
			while(true) {
				try {
					theSocket.receive(packet);
					String data = new String(packet.getData(), 0, packet.getLength());
					System.out.println(data);
					
					now = new Date();
					byte[] Data = now.toString().getBytes("ASCII");
					
					send_packet = new DatagramPacket(Data, Data.length, packet.getAddress(), packet.getPort());
					theSocket.send(send_packet);
					packet.setLength(buffer.length);
				}catch(IOException e) {
					System.out.println(e);
				}
			}
		}catch(SocketException se) {
			System.out.println(se);
		}
	}
}
