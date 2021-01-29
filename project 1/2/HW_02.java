package Chapter1;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class HW_02 extends Frame implements ActionListener{
	Label lfile, lfile2, ltext;
	TextField tfile, tfile2;     // �ؽ�Ʈ �ʵ� 
	TextArea tadata;
	String filename, filename2;
	Button confirm, confirm2;   //Ȯ�� ��ư 
	
	
	public HW_02(String str) {
		super(str); //������ 
		setLayout(new FlowLayout());
		lfile = new Label("�Է�����");
		add(lfile);
		tfile = new TextField(20);  // �����̸� �Է��ϴ� �ؽ�Ʈ �ʵ�
		tfile.addActionListener(this);
		add(tfile);
		confirm = new Button("Ȯ��");
		confirm.addActionListener(this);  // ��ư��ü�� ���� �̺�Ʈ
		add(confirm);
		lfile2 = new Label("�������");
		add(lfile2);
		tfile2 = new TextField(20);
		tfile2.addActionListener(this);
		add(tfile2);
		confirm2 = new Button("Ȯ��");
		confirm2.addActionListener(this);
		add(confirm2);
		ltext = new Label("���ϳ���");
		add(ltext);
		tadata = new TextArea(3, 35);  // ��� ���� ���� ������
		add(tadata);
		addWindowListener(new WinListener());
	}
	
	public static void main(String args[]) {
		HW_02 text = new HW_02("�ǽ� 02");
		text.setSize(300,270);
		text.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae) { //��ư�� Ŭ���ϸ� ���� 
		int bytesRead;
		byte[] buffer = new byte[256];
		FileInputStream fin = null;
		FileOutputStream fout = null;
		filename = tfile.getText();
		filename2 = tfile2.getText();
		
		if(ae.getSource() == confirm) { // ù��° Ȯ��(�Է�����)	
				try {
					fin = new FileInputStream(filename);
					fin.read(buffer);
					String data = new String(buffer);
				}catch(IOException e) {
				System.err.println(e.toString());	
				}
		}
		else {   //�ι�° Ȯ�� (�������)
			try {
				fin = new FileInputStream(filename);
				fout = new FileOutputStream(filename2);
				while((bytesRead = fin.read(buffer)) >= 0) {
					fout.write(buffer, 0, bytesRead);    // �Է������� ������ ������Ϸ� ���� 
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
