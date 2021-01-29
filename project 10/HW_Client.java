package chapter13;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

public class HW_Client extends Frame implements ActionListener, KeyListener {
	TextArea display;
	TextField wtext, ltext;
	Label mlbl, wlbl, loglbl;
	BufferedWriter output;
	BufferedReader input;
	Socket client;
	StringBuffer clientdata;
	String serverdata;
	String ID;
	Button Off;
	Panel plabel, ptotal, pword;
	DatagramPacket outgoing, incoming;
	DatagramSocket ds;
	MulticastSocket socket;
    InetAddress ia, group;
	Thread listener;
	int port = 1000;
	byte[] data;
	
	public ClientThread thread;
	private static final String SEPARATOR = "|";
	private static final int REQ_LOGON = 1001;
	private static final int REQ_LOGOUT = 1002;
	private static final int REQ_SENDWORDS = 1021;

	public HW_Client() {
		      super("클라이언트");
		      mlbl = new Label("멀티캐스트 채팅 서버에 가입을 요청합니다!");
		      add(mlbl, BorderLayout.NORTH);

		      display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
		      display.setEditable(false);
		      add(display, BorderLayout.CENTER);

		       ptotal = new Panel(new BorderLayout());
		 
		      pword = new Panel(new BorderLayout());
		      wlbl = new Label("대화말");
		      wtext = new TextField(44); //전송할 데이터를 입력하는 필드
		      wtext.addKeyListener(this); 
		      pword.add(wlbl, BorderLayout.WEST);
		      pword.add(wtext, BorderLayout.EAST);
		      ptotal.add(pword, BorderLayout.CENTER);

		      plabel = new Panel(new BorderLayout());
		      loglbl = new Label("로그온");
		      ltext = new TextField(40); //전송할 데이터를 입력하는 필드
		      ltext.addActionListener(this); 
		      plabel.add(loglbl, BorderLayout.WEST);
		      plabel.add(ltext, BorderLayout.CENTER);
		      ptotal.add(plabel, BorderLayout.SOUTH);

		      add(ptotal, BorderLayout.SOUTH);

		      addWindowListener(new WinListener());
		      setSize(400,250);
		      setVisible(true);
	   }

	public void runClient() {
		try {
			clientdata = new StringBuffer(1024);
			ds = new DatagramSocket();
			outgoing = new DatagramPacket(new byte[1], 1, InetAddress.getLocalHost(), 3000);
			incoming = new DatagramPacket(new byte[65508], 65508);
			//mlbl.setText("접속 완료 사용할 아이디를 입력하세요."); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent ae) {
			if (ID == null) {
				ID = ltext.getText();
				try {
					clientdata.setLength(0); 
					clientdata.append(REQ_LOGON);
					clientdata.append(SEPARATOR);
					clientdata.append(ID);

					data = new String(clientdata).getBytes();
					outgoing.setData(data);
					outgoing.setLength(data.length);
					ds.send(outgoing);
					//ltext.setVisible(false);
					
					ds.receive(incoming); //서버가 보낸 멀티캐스트 그룹 주소를 받아야 함
					String addr = new String(incoming.getData(), 0, incoming.getLength());
					StringTokenizer st = new StringTokenizer(addr, SEPARATOR);
					String group_addr = st.nextToken();
					group_addr = group_addr.replace("/", "");  //받는 그룹 주소의 처음부분이 '/'로 시작하기 때문에 공백으로 대체 
					group = InetAddress.getByName(group_addr);
					port = Integer.parseInt(st.nextToken());
					display.append("멀티캐스트 채팅 그룹 주소는 " +  group_addr + " : " + port + "입니다.\r\n");
					
					socket = new MulticastSocket(port);
					socket.joinGroup(group);  // 서버가 보내온 그룹 주소에 가입
					
					
					ltext.setVisible(false);
					Off = new Button("로그아웃");
					plabel.add(Off, BorderLayout.CENTER);
					Off.addActionListener(this);
					mlbl.setText(ID + "(으)로 로그인 하였습니다.");
					plabel.validate();
					
					//listener = new Thread(socket);
					thread = new ClientThread(socket);
					thread.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				mlbl.setText("로그인 후 사용하세요!");
			}

		 if (ae.getSource() == Off) {
			mlbl.setText("멀티캐스트 채팅 서버에 가입을 요청합니다!");
			Off.setVisible(false);
			try {
				ltext.setText("");
				clientdata.setLength(0);
				clientdata.append(REQ_LOGOUT);
				clientdata.append(SEPARATOR);
				clientdata.append(ID);
				data = new String(clientdata).getBytes();

				outgoing.setData(data);
				outgoing.setLength(data.length);
				ds.send(outgoing);

				plabel.remove(Off);
				ltext.setVisible(true);
				
				try {  // 그룹에서 탈퇴하기 
					socket.leaveGroup(group);
				}finally {
					socket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		HW_Client c = new HW_Client();
		c.runClient();
	}

	class WinListener extends WindowAdapter { 
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}

	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
			String message = new String();
			message = wtext.getText();
			if (ID == null) {
				mlbl.setText("로그인 후 사용하세요!");
				wtext.setText("");
			} else {
				try {
					clientdata.setLength(0);
					clientdata.append(REQ_SENDWORDS);
					clientdata.append(SEPARATOR);
					clientdata.append(ID);
					clientdata.append(SEPARATOR);
					clientdata.append(message);
					clientdata.append("\r\n");
					data = new String(clientdata).getBytes();
					outgoing.setData(data);
					outgoing.setLength(data.length);
					ds.send(outgoing);
					wtext.setText("");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class ClientThread extends Thread {
		MulticastSocket socket;
		public ClientThread(MulticastSocket ms) {
			socket = ms;
		}
		public void run() {
			try {
				while (!Thread.interrupted()) {
					incoming.setLength(incoming.getData().length);
					socket.receive(incoming);
					String message = new String(incoming.getData(), 0, incoming.getLength());
					display.append(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void keyReleased(KeyEvent ke) {
	}

	public void keyTyped(KeyEvent ke) {
	}
}
