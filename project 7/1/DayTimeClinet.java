package com.java.Chapter7_8;
import java.io.*;
import java.net.*;
public class DayTimeClinet {
	public static void main(String args[]) {
		Socket theSocket;
		String host;
		InputStream is;
		OutputStream os;
		BufferedReader reader;
		BufferedWriter writer;
		if(args.length>0) {
			host = args[0]; // 원격호스트를 입력받음
		}
		else {
			host = "localhost"; //로컬 호스트를 원격 호스트로 사용
		}
		
		try {
			theSocket = new Socket(host, 13);  //daytime 서버에 접속
			is = theSocket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is));
			os = theSocket.getOutputStream();
			writer = new BufferedWriter(new OutputStreamWriter(os));
			String theTime = reader.readLine(); //날짜를 읽음
			System.out.println("호스트의 시간은 " + theTime + "이다");
			writer.write("Thank you!");
			writer.flush();
			theSocket.close();
		}catch(UnknownHostException e) {
			System.err.println(args[0] + " 호스트를 찾을 수 없습니다.");
		}catch(IOException e) {
			System.err.println(e);
		}
	}
}
