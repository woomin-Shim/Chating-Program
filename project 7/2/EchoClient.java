package com.java.Chapter7_8;
import java.io.*;
import java.net.*;
public class EchoClient {
	public static void main(String args[]) {
		Socket theSocket = null;
		String host;
		InputStream is;
		BufferedReader reader, userInput;
		OutputStream os;
		BufferedWriter writer;
		String line;
		if(args.length > 0) {
			host = args[0]; //원격 호스트를 입력받음
		}
		else {
			host = "localhost"; //로컬 호스트를 원격 호스트로 사용
		}
		
		try {
			theSocket = new Socket(host, 7); //echo 서버에 접속한다.
			is = theSocket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is));  //서버로부터 데이터 수신
			userInput = new BufferedReader(new InputStreamReader(System.in));  // 키보드로부터 데이터 수신
			os = theSocket.getOutputStream();
			writer = new BufferedWriter(new OutputStreamWriter(os));
			System.out.println("전송할 문장을 입력하시오.");
			while(true) {
				line = userInput.readLine();  //데이터를 입력한다.
				if(line == null) break; //프로그램 종료 
				writer.write(line + '\r' + '\n');
				writer.flush(); //서버에 데이터 전송
				System.out.println(reader.readLine());  //다시 수신해서 화면에 출력 
			}
		}catch(UnknownHostException e) {
			System.err.println(args[0]+" 호스트를 찾을 수 없습니다.");
		}catch(IOException e) {
			System.err.println(e);
		}finally {
			try {
				theSocket.close();
			}catch(IOException e) {
				System.out.println(e);
			}
		}
	}
}
