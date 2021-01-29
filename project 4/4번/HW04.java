package chapter5;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.applet.*;

public class HW04 extends Frame implements ActionListener {
	TextField hostname; // 호스트 이름을 입력받는 필드
	Button getinfor; // 입력된 호스트에 관한 IP 정보를 읽는 버튼
	TextArea display, display2; // 구해진 IP에 관한 정보를 출력하는 필드

	public static void main(String args[]) {
		HW04 host = new HW04("InetAddress 클래스");
		host.setVisible(true);
	}

	public HW04(String str) {
		super(str);
		addWindowListener(new WinListener());
		setLayout(new BorderLayout());
		Panel inputpanel = new Panel(); // 첫번째 패널
		inputpanel.setLayout(new BorderLayout());
		inputpanel.add("North", new Label("호스트 이름 : "));
		hostname = new TextField("", 30);
		getinfor = new Button("호스트 정보 얻기");
		inputpanel.add("Center", hostname);
		inputpanel.add("South", getinfor);
		getinfor.addActionListener(this); // 이벤트 등록
		add("North", inputpanel); // 패널을 프레임에 부착

		Panel outputpanel = new Panel(); // 두번째 패널
		outputpanel.setLayout(new BorderLayout());
		display = new TextArea("", 24, 30);
		display.setEditable(false);
		outputpanel.add("North", new Label("인터넷 주소"));
		outputpanel.add("Center", display);
		add("Center", outputpanel);

		Panel panel_3 = new Panel(); // 세번째 패널
		panel_3.setLayout(new BorderLayout());
		display2 = new TextArea("", 24, 30);
		display2.setEditable(false);
		panel_3.add("North", new Label("클래스 유형"));
		panel_3.add("Center", display2);
		add("South", panel_3);
		setSize(270, 600);
	}

	public void actionPerformed(ActionEvent e) {
		String name = hostname.getText(); // 입력된 호스트 이름을 구한다.
		String ip = null;
		try {
			InetAddress[] inet = InetAddress.getAllByName(name); // InetAddress 객체생성
			for (int i = 0; i < inet.length; i++) {
				ip = inet[i].getHostName() + "\n";
				display.append(ip);
				ip = inet[i].getHostAddress() + "\n\n";
				display.append(ip);
			}
			ip = ipClass(inet[0].getAddress()) + "\n"; // 대표IP 주소
			display2.append(ip);
			int	hash = inet[0].hashCode();
			display2.append(Integer.toString(hash));
		} catch (UnknownHostException ue) {
			String i = name + "해당 호스트가 없습니다.\n";
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
