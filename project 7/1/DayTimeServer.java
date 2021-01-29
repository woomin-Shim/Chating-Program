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
			//13번 포트에서 클라이언트의 접속 요청을 기다리는 서버소켓 객체 생성
			while(true) {
				try {
					theSocket = theServer.accept();
					//클라이언트의 접속요청을 기다리고 클라이언트의 소켓과 연결된 서버측의 소켓을 생성
					OutputStream os = theSocket.getOutputStream();
					//클라이언트에 데이터를 전송할 OutputStream 객체를 생성
					writer = new BufferedWriter(new OutputStreamWriter(os));
					//클라이언트에 데이터를 전송하는 BufferedWriter 객체 생성
					InputStream is = theSocket.getInputStream();
					reader = new BufferedReader(new InputStreamReader(is));
					Date now = new Date(); //날짜
					writer.write(now.toString()+"\r\n");  //날짜 전송
					writer.flush();
					theSocket.shutdownOutput();  //송신 채널 종료 
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
