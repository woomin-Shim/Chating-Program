package Chapter1;
import java.io.*;

public class Hw_01 {
	public static void main(String[] args) {
		int bytesRead; //read() �޼ҵ尡 ���� ����Ʈ �� ����
		byte[] buffer = new byte[256];
		FileInputStream fin = null;
		for(int i=0; i < args.length; i++)  {  
			try {
				fin = new FileInputStream(args[i]);  //Ŀ�ǵ� ���ο��� �����̸� �Է¹ް� buffer �޸𸮿� ����
				while((bytesRead = fin.read(buffer)) >= 0) {  //���� �� ������ -1, ��Ʈ�� ��
					System.out.println("FileName : " + args[i]);  // ���� �̸� ��� 
					System.out.write(buffer, 0, bytesRead);   // ������ ���� ����Ϳ� ��� 
					System.out.println();
					System.out.println("-------------------");
				}				
			}catch(IOException e) {
				System.err.println("��Ʈ�����κ��� �����͸� ���� �� �����ϴ�.");
			}finally {
				try {
					if(fin!=null) fin.close();
				} catch(IOException e) {}
			}
		}
	}

}
