package chapter4;

import java.io.*;

public class HW_02 {
	public static void main(String args[]) throws IOException {
		String text;
		int bytesRead;
		byte[] buffer = new byte[256];
		FileOutputStream fout = new FileOutputStream("HW02.txt");
		FileInputStream fin = new FileInputStream("Hw02.txt");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		PrintStream write = new PrintStream(fout);
		int num = 1;
		while (true) {
			try {
				text = br.readLine();
				if (text == null) {
					break;
				}
			} catch (IOException e) {
				System.err.println(e);
				break;
			}
			text = num + " : " + text;
			write.println(text);
			num++;
		}

		try {
			fout.close();
		} catch (IOException e) {
			System.err.println(e);
		}
		try {
			while ((bytesRead = fin.read(buffer)) >= 0) {
				System.out.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			System.err.println(e);
		}
		try {
			fin.close();
		} catch (IOException e) {
			System.out.println("error");
		}
	}
}
