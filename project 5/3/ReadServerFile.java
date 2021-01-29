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
		super("ȣ��Ʈ ���� �б�");
		setLayout(new BorderLayout());
		
		enter = new TextField("URL�� �Է��ϼ���.");
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
			
			line2 = "�������� : " + url.getProtocol()+"\n";
			contents1.append(line2);
			line2 = "ȣ��Ʈ : " + url.getHost()+"\n";
			contents1.append(line2);
			line2 = "��Ʈ : " + url.getPort()+"\n";
			contents1.append(line2);
			line2 = "���� : " + url.getFile()+"\n";
			contents1.append(line2);
			line2 = "�ؽ��ڵ� : " + url.hashCode()+"\n";
			contents1.append(line2);
			
			contents2.setText("������ �д� ���Դϴ�....");

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
			contents2.setText("URL ������ �߸��Ǿ����ϴ�.");
		}
		catch (IOException e2)
		{
			contents2.setText(e2.toString());
		}
		catch (Exception e2)
		{
			contents2.setText("ȣ��Ʈ ��ǻ���� ���ϸ��� �� �� �ֽ��ϴ�.");
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