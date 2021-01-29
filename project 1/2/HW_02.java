package Chapter1;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class HW_02 extends Frame implements ActionListener{
	Label lfile, lfile2, ltext;
	TextField tfile, tfile2;     // 텍스트 필드 
	TextArea tadata;
	String filename, filename2;
	Button confirm, confirm2;   //확인 버튼 
	
	
	public HW_02(String str) {
		super(str); //생성자 
		setLayout(new FlowLayout());
		lfile = new Label("입력파일");
		add(lfile);
		tfile = new TextField(20);  // 파일이름 입력하는 텍스트 필드
		tfile.addActionListener(this);
		add(tfile);
		confirm = new Button("확인");
		confirm.addActionListener(this);  // 버튼객체에 대한 이벤트
		add(confirm);
		lfile2 = new Label("출력파일");
		add(lfile2);
		tfile2 = new TextField(20);
		tfile2.addActionListener(this);
		add(tfile2);
		confirm2 = new Button("확인");
		confirm2.addActionListener(this);
		add(confirm2);
		ltext = new Label("파일내용");
		add(ltext);
		tadata = new TextArea(3, 35);  // 출력 파일 내용 보여줌
		add(tadata);
		addWindowListener(new WinListener());
	}
	
	public static void main(String args[]) {
		HW_02 text = new HW_02("실습 02");
		text.setSize(300,270);
		text.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae) { //버튼을 클릭하면 실행 
		int bytesRead;
		byte[] buffer = new byte[256];
		FileInputStream fin = null;
		FileOutputStream fout = null;
		filename = tfile.getText();
		filename2 = tfile2.getText();
		
		if(ae.getSource() == confirm) { // 첫번째 확인(입력파일)	
				try {
					fin = new FileInputStream(filename);
					fin.read(buffer);
					String data = new String(buffer);
				}catch(IOException e) {
				System.err.println(e.toString());	
				}
		}
		else {   //두번째 확인 (출력파일)
			try {
				fin = new FileInputStream(filename);
				fout = new FileOutputStream(filename2);
				while((bytesRead = fin.read(buffer)) >= 0) {
					fout.write(buffer, 0, bytesRead);    // 입력파일의 내용을 출력파일로 복사 
				}
				fin.read(buffer);
				String data = new String(buffer);
				tadata.setText(data + "/n");
				System.out.println(buffer);
			}catch(IOException e) {
				System.err.println(e.toString());
			
			}finally {
				try {
					if(fin!= null) fin.close();
					if(fout!=null)fout.close();
				}catch(IOException e) {}
			}
	
		}
	}
	

		class WinListener extends WindowAdapter
		{
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		}
			
}
