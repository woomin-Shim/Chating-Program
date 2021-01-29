package chapter13;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
public class HW_Server extends Frame {
	TextArea display;
	Label info;
	String clientdata;
	String serverdata = "";
	// List<ServerThread> list;
	int port= 1111;
	MulticastSocket socket;
	InetAddress group;
	Socket sock;
	DatagramSocket ds;
	DatagramPacket outgoing, incoming; // �۽�, ���� ��������Ŷ ����
	byte[] data = new byte[1024];
	
	private static final String SEPARATOR = "|";
	private static final int REQ_LOGON = 1001;
	private static final int REQ_LOGOUT = 1002;
	private static final int REQ_SENDWORDS = 1021;

	public HW_Server() {
		super("����");
		info = new Label();
		add(info, BorderLayout.CENTER);
		display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
		display.setEditable(false);
		add(display, BorderLayout.SOUTH);
		addWindowListener(new WinListener());
		setSize(300, 250);
		setVisible(true);
	}

	public void runServer() {
	
		try {
			group = InetAddress.getByName("239.255.10.10");
			ds = new DatagramSocket(3000);
			socket = new MulticastSocket();
			outgoing = new DatagramPacket(new byte[1], 1);
			incoming = new DatagramPacket(new byte[65508], 65508);
			info.setText("��Ƽĳ��Ʈ ä�� �׷� �ּ� : " + group.getHostAddress());

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		try {
			while (true) {
				incoming.setLength(incoming.getData().length);
				ds.receive(incoming);
				clientdata = new String(incoming.getData(), 0, incoming.getLength());
				//System.out.println(clientdata);
				StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
				int command = Integer.parseInt(st.nextToken());
				// int cnt = cs.list.size();
				
				switch (command) { 
				case REQ_LOGON: { // ��1001|���̵𡱸� ������ ���
					String ID = st.nextToken();
					// display.append("��Ƽĳ��Ʈ ä�� �׷� �ּҴ� " +ia.getHostAddress() +":" + port +
					// "�Դϴ�\r\n");
					display.append("Ŭ���̾�Ʈ�� " + ID + "(��)�� �α��� �Ͽ����ϴ�.\r\n");

					//String msg = "��Ƽĳ��Ʈ ä�� �׷� �ּҴ� " + group.getHostAddress() + ":" + port + "�Դϴ�\r\n";
					//String msg = group.getHostAddress();
					String msg = group + SEPARATOR + Integer.toString(port);
					data = msg.toString().getBytes();
					outgoing.setData(data);
					outgoing.setLength(data.length);
					outgoing.setAddress(incoming.getAddress());
					outgoing.setPort(incoming.getPort());
					ds.send(outgoing);
					break;
					
					/*try {
					socket.setSoTimeout(100000);
					socket.setTimeToLive(1);
					socket.joinGroup(group);
				}catch(UnknownHostException e) {
					System.out.println(e);
				} */
				}
				case REQ_LOGOUT: { // 1004|���̵� �α׾ƿ�
					String ID = st.nextToken();
					display.append("Ŭ���̾�Ʈ " + ID + "�� �α׾ƿ� �Ͽ����ϴ�.\r\n");
					
					//socket.leaveGroup(group);
					//socket.close();
					break;
				}
				case REQ_SENDWORDS: { // ��1021|���̵�|��ȭ������ ����
					String ID = st.nextToken();
					String message = st.nextToken();
					String sendm = ID + ":" + message;
					data = new String(sendm).getBytes();
					outgoing.setData(data);
					outgoing.setLength(data.length);
					outgoing.setAddress(group);
					outgoing.setPort(port);
					socket.send(outgoing);
					display.append(sendm);
					break;
				}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			sock.close();
		} catch (IOException ea) {
			ea.printStackTrace();
		}

	}

	public static void main(String args[]) {
		HW_Server s = new HW_Server();
		s.runServer();
	}

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {

			System.exit(0);
		}
	}
}


