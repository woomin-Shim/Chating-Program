package com.java.Chapter6;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageProducer;
import java.net.*;
import java.io.*;
public class ReadServerFile extends Frame implements ActionListener {
	private TextField enter;
	private TextArea contents, contents2;
	public ReadServerFile() {
		super("ȣ��Ʈ ���� �б�");
		setLayout(new BorderLayout());
		enter = new TextField("URL�� �Է��ϼ���!");
		enter.addActionListener( this );
		add(enter, BorderLayout.NORTH);
		
		contents=new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
		add(contents, BorderLayout.CENTER);
		contents2 = new TextArea("", 0, 0);
		add(contents2, BorderLayout.SOUTH);
		
		addWindowListener(new WinListener());
		setSize(400, 500);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		URL url;
		InputStream is;
		BufferedReader input;
		String line;
		StringBuffer buffer = new StringBuffer();
		String location = e.getActionCommand(); // �ؽ�Ʈ �ʵ忡 �Էµ� URL ���ϱ�
		try {
			url = new URL(location);
			Object o = url.getContent();
			is = url.openStream(); //location(ȣ��Ʈ)�� �����Ű�� inputstream ��ü ����
			input = new BufferedReader(new InputStreamReader(is));
			URLConnection uc = url.openConnection();
			uc.connect();
			
			contents.append("Protocol : " + url.getProtocol() + "\n");
			contents.append("host name : " + url.getHost() + "\n");
			contents.append("port no : " + url.getPort() + "\n");
			contents.append("file name : " + url.getFile() + "\n");
			contents.append("hash code : " + url.hashCode() + "\n");
			
			if(o instanceof ImageProducer) // �̹��� ������ ������ 
				contents2.setText("image");
			else if(uc.getContentType().contains("audio"))
				contents2.setText("audio");
			else if(uc.getContentType().contains("video"))
				contents2.setText("video");
			else if(o instanceof InputStream) {
				input = new BufferedReader(new InputStreamReader(is));
				while((line = input.readLine()) != null)
					buffer.append(line).append('\n');
				contents2.setText(buffer.toString());
				input.close();
			}
			
		}catch(MalformedURLException e2) {
			contents2.setText("URL ������ �߸��Ǿ����ϴ�.");
		}catch(IOException io) {
			contents.setText(io.toString());
		}catch(Exception ex) {
			contents2.setText("ȣ��Ʈ ��ǻ���� ���ϸ��� �� �� �ֽ��ϴ�.");
		}
	}
	 public static void main(String args[]){
	      ReadServerFile read = new ReadServerFile();
	   }
	   class WinListener extends WindowAdapter
	   {
	      public void windowClosing(WindowEvent we){
	         System.exit(0);
	      }
	   }
}
