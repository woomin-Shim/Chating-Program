package chapter5;

import java.io.*;
import java.net.*;

public class InetExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String hostname;
		BufferedReader br;
		printLocalAddress();
		br = new BufferedReader(new InputStreamReader(System.in));
		try {
			do {
				System.out.println("ȣ��Ʈ �̸� �� IP �ּҸ� �Է��ϼ���.");
				if((hostname = br.readLine()) != null) {
					printRemoteAddress(hostname);
				}
			}while(hostname != null);
			System.out.println("���α׷��� �����մϴ�.");
		}catch(IOException ex) {
			System.out.println("�Է¿���!");
		}
	}
	static void printLocalAddress() {
		try {
			InetAddress myself = InetAddress.getLocalHost();
			System.out.println("���� ȣ��Ʈ �̸� : "+myself.getHostName());
			System.out.println("���� ȣ��Ʈ ��¥ �̸� : "+myself.getCanonicalHostName());
			System.out.println("���� IP �ּ� : "+myself.getHostAddress());
			System.out.println("���� ȣ��Ʈ class : "+ipClass(myself.getAddress()));
			System.out.println("���� ȣ��Ʈ InetAddress : "+myself.toString());
			System.out.println("���� ȣ��Ʈ ������ �ּ� : "+myself.getLoopbackAddress());
			
		}catch(UnknownHostException ex) {
			System.out.println(ex);
		}
	}
	static void printRemoteAddress(String hostname) {
		try {
			System.out.println("ȣ��Ʈ�� ã�� �ֽ��ϴ�."+hostname+"...");
			
			InetAddress[] machine = InetAddress.getAllByName(hostname);
			
			for(InetAddress i : machine)
			{
				System.out.println("���� ȣ��Ʈ �̸� : "+i.getHostName());
				System.out.println("���� ȣ��Ʈ ��¥ �̸� : "+i.getCanonicalHostName());
				System.out.println("���� IP �ּ� : "+i.getHostAddress());
				System.out.println("���� ȣ��Ʈ class : "+ipClass(i.getAddress()));
				System.out.println("���� ȣ��Ʈ InetAddress : "+i.toString());
			}

		}catch(UnknownHostException ex) {
			System.out.println(ex);
		}
	}
	static char ipClass(byte[] ip) {
		int highByte = 0xff & ip[0];
		return (highByte < 128) ? 'A' : (highByte<192) ? 'B' : (highByte < 224) ? 'C' : (highByte < 240) ? 'D' : 'E';
	}
}
