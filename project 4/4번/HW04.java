package chapter5;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.applet.*;

public class HW04 extends Frame implements ActionListener {
	TextField hostname; // ȣ��Ʈ �̸��� �Է¹޴� �ʵ�
	Button getinfor; // �Էµ� ȣ��Ʈ�� ���� IP ������ �д� ��ư
	TextArea display, display2; // ������ IP�� ���� ������ ����ϴ� �ʵ�

	public static void main(String args[]) {
		HW04 host = new HW04("InetAddress Ŭ����");
		host.setVisible(true);
	}

	public HW04(String str) {
		super(str);
		addWindowListener(new WinListener());
		setLayout(new BorderLayout());
		Panel inputpanel = new Panel(); // ù��° �г�
		inputpanel.setLayout(new BorderLayout());
		inputpanel.add("North", new Label("ȣ��Ʈ �̸� : "));
		hostname = new TextField("", 30);
		getinfor = new Button("ȣ��Ʈ ���� ���");
		inputpanel.add("Center", hostname);
		inputpanel.add("South", getinfor);
		getinfor.addActionListener(this); // �̺�Ʈ ���
		add("North", inputpanel); // �г��� �����ӿ� ����

		Panel outputpanel = new Panel(); // �ι�° �г�
		outputpanel.setLayout(new BorderLayout());
		display = new TextArea("", 24, 30);
		display.setEditable(false);
		outputpanel.add("North", new Label("���ͳ� �ּ�"));
		outputpanel.add("Center", display);
		add("Center", outputpanel);

		Panel panel_3 = new Panel(); // ����° �г�
		panel_3.setLayout(new BorderLayout());
		display2 = new TextArea("", 24, 30);
		display2.setEditable(false);
		panel_3.add("North", new Label("Ŭ���� ����"));
		panel_3.add("Center", display2);
		add("South", panel_3);
		setSize(270, 600);
	}

	public void actionPerformed(ActionEvent e) {
		String name = hostname.getText(); // �Էµ� ȣ��Ʈ �̸��� ���Ѵ�.
		String ip = null;
		try {
			InetAddress[] inet = InetAddress.getAllByName(name); // InetAddress ��ü����
			for (int i = 0; i < inet.length; i++) {
				ip = inet[i].getHostName() + "\n";
				display.append(ip);
				ip = inet[i].getHostAddress() + "\n\n";
				display.append(ip);
			}
			ip = ipClass(inet[0].getAddress()) + "\n"; // ��ǥIP �ּ�
			display2.append(ip);
			int	hash = inet[0].hashCode();
			display2.append(Integer.toString(hash));
		} catch (UnknownHostException ue) {
			String i = name + "�ش� ȣ��Ʈ�� �����ϴ�.\n";
			display.append(ip);
		}
	}
	
	static char ipClass(byte[] ip) {
		int highByte = 0xff & ip[0];
		return (highByte < 128) ? 'A' : (highByte < 192) ? 'B' : (highByte < 224) ? 'C' : (highByte < 240) ? 'D' : 'E';
	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}
