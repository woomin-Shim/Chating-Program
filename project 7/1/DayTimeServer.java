package com.java.Chapter7_8;
import java.io.*;
import java.net.*;
import java.util.Date;
public class DayTimeServer {
	public final static int daytimeport=13;
	public static void main(String args[]) {
		ServerSocket theServer;
		Socket theSocket = null;
		BufferedWriter writer;
		BufferedReader reader;
		try {
			theServer = new ServerSocket(daytimeport);
			//13�� ��Ʈ���� Ŭ���̾�Ʈ�� ���� ��û�� ��ٸ��� �������� ��ü ����
			while(true) {
				try {
					theSocket = theServer.accept();
					//Ŭ���̾�Ʈ�� ���ӿ�û�� ��ٸ��� Ŭ���̾�Ʈ�� ���ϰ� ����� �������� ������ ����
					OutputStream os = theSocket.getOutputStream();
					//Ŭ���̾�Ʈ�� �����͸� ������ OutputStream ��ü�� ����
					writer = new BufferedWriter(new OutputStreamWriter(os));
					//Ŭ���̾�Ʈ�� �����͸� �����ϴ� BufferedWriter ��ü ����
					InputStream is = theSocket.getInputStream();
					reader = new BufferedReader(new InputStreamReader(is));
					Date now = new Date(); //��¥
					writer.write(now.toString()+"\r\n");  //��¥ ����
					writer.flush();
					theSocket.shutdownOutput();  //�۽� ä�� ���� 
					System.out.println(reader.readLine());
					theSocket.close();
				}catch(IOException e) {
					System.out.println(e);
				}finally {
					try {
						if(theSocket != null) theSocket.close();
					}catch(IOException e) {
						System.out.println(e);
					}
				}
			}
		}catch(IOException e) {
			System.out.println(e);
		}
	}
}
