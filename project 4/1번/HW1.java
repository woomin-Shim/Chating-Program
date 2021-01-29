package chapter4;

import java.io.*;

public class HW1 {
	public static void main(String args[]) {
		String buf;
		FileReader fin = null;
		FileWriter fout = null;
		if (args.length != 2) { // �μ��� �ҽ����ϸ�� ������ϸ��� �Է��ؾ� ��
			System.out.println("�ҽ����� �� ��������� �����Ͻʽÿ�.");
			System.exit(1);
		}
		try {
			fin = new FileReader(args[0]); // �ҽ� ���ϰ� ����� �Է� ���� ��Ʈ��
			fout = new FileWriter(args[1]); // ��� ���ϰ� ����� �Է� ���� ��Ʈ��
		} catch (IOException e) {
			System.out.println(e);
			System.exit(1);
		}

		LineNumberReader read = new LineNumberReader(fin);
		PrintWriter write = new PrintWriter(fout); // �⺻ fout��½�Ʈ���� ����
		int num = 1;
		while (true) {
			try {
				buf = read.readLine(); // ������ �����͸� ����
				if (buf == null)
					break;
			} catch (IOException e) {
				System.out.println(e);
				break;
			}
			buf = num + " : " + buf; // [��ȣ : ���α׷� ����] �������� ����
			write.println(buf);
			num++;
		}
		try {
			fin.close();
			fout.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
