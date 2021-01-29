package com.java.Chapter7_8;

import java.io.*;
import java.net.*;

public class EchoServer {
	public static void main(String args[]) {
		ServerSocket theServer;
		Socket theSocket = null;

		try {
			theServer = new ServerSocket(7);
			// 7�� ��Ʈ���� Ŭ���̾�Ʈ�� ���� ��û�� ��ٸ��� �������� ��ü ����
			theSocket = theServer.accept();
			// Ŭ���̾�Ʈ�� ���ӿ�û�� ��ٸ��� Ŭ���̾�Ʈ�� ���ϰ� ����� ���� �� ���� ����
			EchoThread thread = new EchoThread(theSocket);
			thread.run();
		} catch (UnknownHostException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			if (theSocket != null) {
				try {
					theSocket.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		}
	}
}

class EchoThread extends Thread {
	InputStream is;
	BufferedReader reader;
	OutputStream os;
	BufferedWriter writer;
	String line;
	Socket theSocket = null;

	public EchoThread(Socket so) {
		theSocket = so;
	}

	public void run() {
		try {
			is = theSocket.getInputStream();
			// Ŭ���̾�Ʈ�� ������ �����͸� ���� InputStream ��ü ����
			reader = new BufferedReader(new InputStreamReader(is));
			// Ŭ���̾�Ʈ�� ������ �����͸� ���� BufferedReader ��ü ����
			os = theSocket.getOutputStream();
			// Ŭ���̾�Ʈ�� �����͸� ������ outputStream��ü ����
			writer = new BufferedWriter(new OutputStreamWriter(os));
			// Ŭ���̾�Ʈ�� �����͸� �����ϴ� BufferedWriter ��ü ����
			while ((line = reader.readLine()) != null) { // Ŭ���̾�Ʈ�� ������ ����
				System.out.println(line); // ���� �����͸� ȭ�鿡 ���
				writer.write(line + '\r' + '\n'); // Ŭ���̾�Ʈ�� ������ ������
				writer.flush(); // Ŭ���̾�Ʈ�� ������ ������
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
