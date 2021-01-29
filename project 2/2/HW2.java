package chapter3;

import java.io.*;

public class HW2 {
	private RandomAccessFile input, output;
	public HW2() throws IOException {
		try {
			input = new RandomAccessFile("nightscape.jpg", "r"); // 읽기 모드
			output = new RandomAccessFile("copy.jpg", "rw"); // 읽기 쓰기 모드
		} catch (IOException e) {
			System.err.println("error");
		}
		int inData, count = 0, i = 0;
		byte[] data = new byte[1024];
		double per;
		while ((inData = input.read(data)) >= 0) { // 사진파일데이터 -> 메모리
			output.write(data, 0, inData); // copy로 복사
			count += inData; // inData=read메소드가 읽은 바이트 수
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
