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
		super("호스트 파일 읽기");
		setLayout(new BorderLayout());
		enter = new TextField("URL를 입력하세요!");
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
		String location = e.getActionCommand(); // 텍스트 필드에 입력된 URL 구하기
		try {
			url = new URL(location);
			Object o = url.getContent();
			is = url.openStream(); //location(호스트)과 연결시키는 inputstream 객체 생성
			input = new BufferedReader(new InputStreamReader(is));
			URLConnection uc = url.openConnection();
			uc.connect();
			
			contents.append("Protocol : " + url.getProtocol() + "\n");
			contents.append("host name : " + url.getHost() + "\n");
			contents.append("port no : " + url.getPort() + "\n");
			contents.append("file name : " + url.getFile() + "\n");
			contents.append("hash code : " + url.hashCode() + "\n");
			
			if(o instanceof ImageProducer) // 이미지 파일을 읽으면 
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
			contents2.setText("URL 형식이 잘못되었습니다.");
		}catch(IOException io) {
			contents.setText(io.toString());
		}catch(Exception ex) {
			contents2.setText("호스트 컴퓨터의 파일만을 열 수 있습니다.");
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
