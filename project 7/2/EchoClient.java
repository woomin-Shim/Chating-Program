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
			host = args[0]; //���� ȣ��Ʈ�� �Է¹���
		}
		else {
			host = "localhost"; //���� ȣ��Ʈ�� ���� ȣ��Ʈ�� ���
		}
		
		try {
			theSocket = new Socket(host, 7); //echo ������ �����Ѵ�.
			is = theSocket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is));  //�����κ��� ������ ����
			userInput = new BufferedReader(new InputStreamReader(System.in));  // Ű����κ��� ������ ����
			os = theSocket.getOutputStream();
			writer = new BufferedWriter(new OutputStreamWriter(os));
			System.out.println("������ ������ �Է��Ͻÿ�.");
			while(true) {
				line = userInput.readLine();  //�����͸� �Է��Ѵ�.
				if(line == null) break; //���α׷� ���� 
				writer.write(line + '\r' + '\n');
				writer.flush(); //������ ������ ����
				System.out.println(reader.readLine());  //�ٽ� �����ؼ� ȭ�鿡 ��� 
			}
		}catch(UnknownHostException e) {
			System.err.println(args[0]+" ȣ��Ʈ�� ã�� �� �����ϴ�.");
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
