package chapter5;
import java.io.*;
import java.net.*;

public class HW03 {
	public static void main(String args[]) {
		String hostname;
		BufferedReader br;
		printLocalAddress(); // ���� ȣ��Ʈ�� �̸� �� IP �ּ� ���
		br = new BufferedReader(new InputStreamReader(System.in));
		try {
			do {
				System.out.println("ȣ��Ʈ �̸� �� IP �ּҸ� �Է��ϼ���.");
				if((hostname = br.readLine()) != null)
					printRemoteAddress(hostname); //����ȣ��Ʈ�� �ּ� ���
			}while(hostname != null);
			System.out.println("���α׷��� �����մϴ�.");
		}catch(IOException e) {
			System.out.println("�Է� ����!");
		}
	}
	static void printLocalAddress() {
		try {
			InetAddress myself = InetAddress.getLocalHost();
			System.out.println("���� ȣ��Ʈ �̸� : " + myself.getHostName());
			System.out.println("���� ȣ��Ʈ ��¥ �̸� : " + myself.getCanonicalHostName());
			System.out.println("���� IP �ּ� : " + myself.getHostAddress());
			System.out.println("���� ������ �ּ� : " + myself.getLoopbackAddress());
			System.out.println("���� ȣ��Ʈ class : " + ipClass(myself.getAddress()));
			System.out.println("���� ȣ��Ʈ InetAddress : " + myself.toString());
		}catch(IOException ex) {
			System.out.println(ex);
		}
	}
	static void printRemoteAddress(String hostname) {
		try {
			System.out.println("ȣ��Ʈ�� ã�� �ֽ��ϴ�. " + hostname + "...");
			InetAddress machine = InetAddress.getByName(hostname);
			System.out.println("���� ȣ��Ʈ �̸� : " + machine.getHostName());
			System.out.println("���� ȣ��Ʈ ��¥ �̸� : " + machine.getCanonicalHostName());
			System.out.println("���� IP �ּ� : " + machine.getHostAddress());
			System.out.println("���� ȣ��Ʈ class : " + ipClass(machine.getAddress()));
			System.out.println("���� ȣ��Ʈ InetAddress : " + machine.toString());
		}catch(UnknownHostException ex) {
			System.out.println(ex);
		}
	}
	static char ipClass(byte[] ip) {
		int highByte = 0xff & ip[0];
		return(highByte<128) ? 'A' : (highByte<192) ? 'B' : (highByte<224)? 'C' : (highByte<240)? 'D' : 'E';
	}
}
