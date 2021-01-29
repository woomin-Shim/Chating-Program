package Chapter1;
import java.io.*;

public class Hw_01 {
	public static void main(String[] args) {
		int bytesRead; //read() 메소드가 읽은 바이트 수 저장
		byte[] buffer = new byte[256];
		FileInputStream fin = null;
		for(int i=0; i < args.length; i++)  {  
			try {
				fin = new FileInputStream(args[i]);  //커맨드 라인에서 파일이름 입력받고 buffer 메모리에 저장
				while((bytesRead = fin.read(buffer)) >= 0) {  //파일 끝 만날때 -1, 스트림 끝
					System.out.println("FileName : " + args[i]);  // 파일 이름 출력 
					System.out.write(buffer, 0, bytesRead);   // 파일의 내용 모니터에 출력 
					System.out.println();
					System.out.println("-------------------");
				}				
			}catch(IOException e) {
				System.err.println("스트림으로부터 데이터를 읽을 수 없습니다.");
			}finally {
				try {
					if(fin!=null) fin.close();
				} catch(IOException e) {}
			}
		}
	}

}
