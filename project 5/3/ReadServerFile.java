package example;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageProducer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ReadServerFile extends Frame implements ActionListener
{
	private TextField enter;
	private TextArea contents1, contents2;
	
	public ReadServerFile()
	{
		super("호스트 파일 읽기");
		setLayout(new BorderLayout());
		
		enter = new TextField("URL를 입력하세요.");
		enter.addActionListener(this);
		add(enter, BorderLayout.NORTH);
		
		contents1 = new TextArea("");
		add(contents1, BorderLayout.CENTER);
		
		contents2 = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
		add(contents2, BorderLayout.SOUTH);
		
		addWindowListener(new WinListener());
		setSize(350, 300);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		URL url;
		InputStream is;
		BufferedReader input;
		String line, line2, type = null;
		StringBuffer buffer = new StringBuffer();
		String location = e.getActionCommand();
		
		try
		{
			url = new URL(location);
			is = url.openStream();
			input = new BufferedReader(new InputStreamReader(is));
			Object o = url.getContent();
			URLConnection uc = url.openConnection();
			uc.connect();
			type = uc.getContentType();
			
			line2 = "프로토콜 : " + url.getProtocol()+"\n";
			contents1.append(line2);
			line2 = "호스트 : " + url.getHost()+"\n";
			contents1.append(line2);
			line2 = "포트 : " + url.getPort()+"\n";
			contents1.append(line2);
			line2 = "파일 : " + url.getFile()+"\n";
			contents1.append(line2);
			line2 = "해시코드 : " + url.hashCode()+"\n";
			contents1.append(line2);
			
			contents2.setText("파일을 읽는 중입니다....");

			if (o instanceof ImageProducer)
				contents2.setText("<image>");
			else if (type.contains("audio"))
				contents2.setText("<audio>");
			else if (type.contains("video"))
				contents2.setText("<video>");
			else if(o instanceof InputStream)
			{
				while((line = input.readLine()) != null)
					buffer.append(line).append('\n');
				contents2.setText(buffer.toString());
			}
			
			input.close();
		}
		catch (MalformedURLException e2)
		{
			contents2.setText("URL 형식이 잘못되었습니다.");
		}
		catch (IOException e2)
		{
			contents2.setText(e2.toString());
		}
		catch (Exception e2)
		{
			contents2.setText("호스트 컴퓨터의 파일만을 열 수 있습니다.");
		}
	}
	
	public static void main(String[] args)
	{
		ReadServerFile read = new ReadServerFile();
	}
	
	class WinListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent we)
		{
			System.exit(0);
		}
	}
}