package chapter5;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.applet.*;

public class GetHostInfor extends Frame implements ActionListener
{
	TextField hostname, hostclass;
	Button getinfor;
	TextArea display;
	
	public static void main(String[] args)
	{
		GetHostInfor host = new GetHostInfor("InetAddress Ŭ����");
		host.setVisible(true);
	}
	
	public GetHostInfor(String str)
	{
		super(str);
		addWindowListener(new WinListener());
		setLayout(new BorderLayout());
		
		Panel inputpaner = new Panel();
		inputpaner.setLayout(new BorderLayout());
		inputpaner.add("North", new Label("ȣ��Ʈ �̸�"));
		hostname = new TextField("", 30);
		inputpaner.add("Center", hostname);
		
		getinfor = new Button("ȣ��Ʈ ���� ���");
		inputpaner.add("South", getinfor);
		getinfor.addActionListener(this);
		add("North", inputpaner);
		
		Panel outputpanel = new Panel();
		outputpanel.setLayout(new BorderLayout());
		display = new TextArea("", 24, 40);
		display.setEditable(false);
		outputpanel.add("North", new Label("���ͳ� �ּ�"));
		outputpanel.add("Center", display);
		add("Center", outputpanel);
		
		Panel outputpanel2 = new Panel();
		outputpanel2.setLayout(new BorderLayout());
		outputpanel2.add("North", new Label("Ŭ����"));
		hostclass = new TextField("", 30);
		outputpanel2.add("Center", hostclass);
		hostclass.setEditable(false);
		add("South", outputpanel2);
		
		setSize(270, 300);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String name = hostname.getText();
		
		try
		{
			InetAddress inet = InetAddress.getByName(name);
			
			String ip = inet.getHostName() + "\n-��ǥ �ּ�\n";
			display.append(ip);
			
			ip = inet.getHostAddress() + "\n-���� �ּ�\n";
			display.append(ip);
			
			InetAddress[] machine = InetAddress.getAllByName(name);
			for(InetAddress i : machine)
			{
				ip = i.getHostAddress() + "\n";
				display.append(ip);
			}	
			
			hostclass.setText(ipClass(inet.getAddress()));
		}
		catch (UnknownHostException e2)
		{
			String ip = name + ": �ش� ȣ��Ʈ�� �����ϴ�.\n";
			display.append(ip);
		}
	}
	
	static String ipClass(byte[] ip)
	{
		int highByte = 0xff & ip[0];
		return (highByte < 128) ? "A" : (highByte<192) ? "B" : (highByte < 224) ? "C" : (highByte < 240) ? "D" : "E";
	}

	class WinListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent we)
		{
			System.exit(0);

		}
	}
}