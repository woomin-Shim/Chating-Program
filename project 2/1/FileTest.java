package com.java.Chapter3;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.awt.event.*;

public class FileTest extends Frame implements ActionListener {
	private TextField enter;
	private TextArea output, output2;
	
	public FileTest() {
		super("File Ŭ���� �׽�Ʈ");
		enter = new TextField("���� �� ���丮���� �Է��ϼ���");
		enter.addActionListener(this);
		output = new TextArea();
		output2 = new TextArea();
		add(enter, BorderLayout.NORTH);
		add(output, BorderLayout.CENTER);
		add(output2, BorderLayout.SOUTH);
		addWindowListener(new WinListener());
		setSize(400,400);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		File name = new File(e.getActionCommand()); //�ؽ�Ʈ�ʵ��� �����̸��� ����
		Long lastModified = name.lastModified(); // ���������ð� 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy�� MM�� dd��(E����) hh�� mm��", Locale.KOREA); //���糯¥�ð��� ǥ�� 
		String date = dateFormat.format(lastModified); // �ּۼ����ð��� y�� M�� d�� ��������  
		if(name.exists()) {
		  try {
			output.setText(name.getName() + "�� �����Ѵ�.\n" +
		(name.isFile()? "�����̴�.\n" : "������ �ƴϴ�.\n") +
		(name.isDirectory()? "���丮�̴�.\n" : "���丮�� �ƴϴ�.\n") +
		(name.isAbsolute()? "�������̴�.\n" : "�����ΰ� �ƴϴ�.\n") +
		"������ ������¥�� : " + date +
		"\n������ ���̴� : " + name.length() +
		"\n������ ��δ� : " + name.getPath() +
		"\n�����δ� : " + name.getAbsolutePath() +
		"\n���԰�δ� : " + name.getCanonicalPath() +  // try exception ���� �ʿ� 
		"\n���� ���丮�� : " + name.getParent() );
		  } catch(IOException e1) {
			  System.err.println(e1.toString());
		  }
			if(name.isFile()) {
				try {
					RandomAccessFile r = new RandomAccessFile(name, "r");
					StringBuffer buf = new StringBuffer();
					String text;
					output2.append("\n\n");
					while((text = r.readLine()) != null)
						buf.append(text + "\n");
					output2.append(buf.toString());
				}catch(IOException e2) {
				}
			}
			else if(name.isDirectory()) {
				String directory[] = name.list();
				output2.append("\n\n���丮�� ������: \n");
				for(int i=0; i<directory.length; i++)
					output2.append(directory[i] + "\n");
			}
		}
			else {
				output.setText(e.getActionCommand() + "�� �������� �ʴ´�.\n");
			}
		}
	
		public static void main(String args[]) {
			FileTest f = new FileTest();
		}
		class WinListener extends WindowAdapter {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		}
	}


