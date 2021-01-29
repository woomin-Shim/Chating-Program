package chapter7_8;
import java.io.*;
import java.net.*;

public class HW3 {
	public static void main(String args[]) {
		Socket theSocket;
		try {
			theSocket = new Socket(args[0], 80);
			System.out.println("toString :" + theSocket.toString());
			theSocket.setSendBufferSize(70);
			System.out.println("getSendBuffersize : " + theSocket.getSendBufferSize());
			theSocket.setReceiveBufferSize(70);
			System.out.println("getReceiveBuffersize : " + theSocket.getReceiveBufferSize());
			theSocket.setKeepAlive(true);
			System.out.println("getKeepAlive : " + theSocket.getKeepAlive());
			theSocket.setTcpNoDelay(true);
			System.out.println("SetTcpNodelay : " + theSocket.getTcpNoDelay());
			theSocket.setReuseAddress(true);
			System.out.println("reuseAddress : " + theSocket.getReuseAddress());
			
		}catch (IOException e) {
			System.out.println(e);
		}
	}
}
