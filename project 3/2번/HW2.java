package chapter3;

import java.io.*;

public class HW2 {
	private RandomAccessFile input, output;
	public HW2() throws IOException {
		try {
			input = new RandomAccessFile("nightscape.jpg", "r"); // �б� ���
			output = new RandomAccessFile("copy.jpg", "rw"); // �б� ���� ���
		} catch (IOException e) {
			System.err.println("error");
		}
		int inData, count = 0, i = 0;
		byte[] data = new byte[1024];
		double per;
		while ((inData = input.read(data)) >= 0) { // �������ϵ����� -> �޸�
			output.write(data, 0, inData); // copy�� ����
			count += inData; // inData=read�޼ҵ尡 ���� ����Ʈ ��
			per = ((double) count / input.length()) * 100;
			if ((int) per % 10 == 0) {
				if ((i - (int) (per / 10)) == -1) { 
					i = (int) (per / 10);
					System.out.println(i * 10 + " * ");
				}
			}
		}
	}

	public static void main(String args[]) throws IOException {
		new HW2();
	}
}
