package rock_paper_scissors;

import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

import RockPaperScissors.PlayGame;

import java.net.*;

public class ClientThread extends Thread {

	private Rock_paper_Client ct_client; // ChatClient ��ü
	private Socket ct_sock; // Ŭ���̾�Ʈ ����
	private DataInputStream ct_in; // �Է� ��Ʈ��
	private DataOutputStream ct_out; // ��� ��Ʈ��
	private StringBuffer ct_buffer; // ����
	private Thread thisThread;
	private PlayRoom room;
	public String MyId = null;

	private static final String SEPARATOR = "|";
	private static final String DELIMETER = "`";

	// �޽��� ��Ŷ �ڵ� �� ������ ����

	// ������ �����ϴ� �޽��� �ڵ�
	private static final int REQ_LOGON = 1001;
	private static final int REQ_AGREE = 1004;
	private static final int RE_AGREE = 1005;
	private static final int NO_GAME = 1006;
	private static final int YES_GAME = 1007;
	private static final int REQ_ENTERROOM = 1011;
	private static final int REQ_SENDWORDS = 1021;
	private static final int REQ_WHISPER = 1022;
	private static final int REQ_LOGOUT = 1031;
	private static final int REQ_QUITROOM = 1041;
	private static final int REQ_RESULT = 1050;

	// �����κ��� ���۵Ǵ� �޽��� �ڵ�
	private static final int YES_LOGON = 2001;
	private static final int NO_LOGON = 2002;
	private static final int YES_ENTERROOM = 2011;
	private static final int NO_ENTERROOM = 2012;
	private static final int MDY_USERIDS = 2013; // �濡 �ִ� ������ �α׿�â���ִ� ������ �����ϱ�����
	private static final int MDY_LOGONIDS = 2014;
	private static final int YES_SENDWORDS = 2021;
	private static final int YES_WHISPER = 2023;
	private static final int NO_SENDWORDS = 2022;
	private static final int YES_LOGOUT = 2031;
	private static final int NO_LOGOUT = 2032;
	private static final int YES_QUITROOM = 2041;
	private static final int YES_RESULT = 2047;

	// ���� �޽��� �ڵ�
	private static final int MSG_ALREADYUSER = 3001;
	private static final int MSG_SERVERFULL = 3002;
	private static final int MSG_CANNOTOPEN = 3011;
	private static final int ERR_ALREADY = 3004;
	private static final int ERR_REJECTION = 3007;
	private static final int ERR_NOUSER = 3008;
	

	private static MessageBox msgBox, logonbox;

	/*
	 * ����ȣ��Ʈ�� ������ ���� ������ ���� : java ChatClient ȣ��Ʈ�̸� ��Ʈ��ȣ To DO .....
	 */

	// ����ȣ��Ʈ���� ����ϱ� ���Ͽ� ���� ������
	// ������ Ŭ���̾�Ʈ�� ���� �ý����� ����Ѵ�.
	public ClientThread(Rock_paper_Client client) {
		try {
			ct_sock = new Socket(InetAddress.getLocalHost(), 2777); // ������ TCP ����
			ct_in = new DataInputStream(ct_sock.getInputStream());
			ct_out = new DataOutputStream(ct_sock.getOutputStream());
			ct_buffer = new StringBuffer(4096);
			thisThread = this; //
			ct_client = client; // ��ü������ �Ҵ�
		} catch (IOException e) {
			MessageBoxLess msgout = new MessageBoxLess(client, "���ῡ��", "������ ������ �� �����ϴ�.");
			msgout.setVisible(true);
		}
	}

	public void run() {

		try {
			Thread currThread = Thread.currentThread(); // ���� �������� �����带 �޾ƿ�
			while (currThread == thisThread) { // ����� LOG_OFF���� thisThread=null;�� ���Ͽ�
				String recvData = ct_in.readUTF();
				StringTokenizer st = new StringTokenizer(recvData, SEPARATOR); // SEPATATOR�� recvdata �ɰ�
				int command = Integer.parseInt(st.nextToken());
				switch (command) {

				/* ������ �������� �޽����� Ȯ���ϴ� �κ� */
				// �α׿� ���� �޽��� PACKET : YES_LOGON|�����ð�|ID1`ID2`ID3...
				case YES_LOGON: {
					logonbox.dispose(); // �޼����ڽ��� �����ش�.
					ct_client.cc_tfStatus.setText("�α׿��� �����߽��ϴ�."); // chatClient�� ����� cc_tfStatus(�ٸ� Ŭ������ ���)
					String date = st.nextToken(); // ��ȭ�� �����ð� -> �α׿� �ð����� �����ϱ�

					ct_client.cc_tfDate.setText(date);
					String ids = st.nextToken(); // ��ȭ�� ����� ����Ʈ
					StringTokenizer users = new StringTokenizer(ids, DELIMETER); // ID1`ID2`ID3...
					while (users.hasMoreTokens()) {
						ct_client.cc_lstMember.add(users.nextToken()); // add to list object
					}

					ct_client.cc_tfLogon.setEditable(false);
					break;
				}

				// �α׿� ���� �Ǵ� �α׿��ϰ� ��ȭ���� �������� ���� ����
				// PACKET : NO_LOGON|errCode
				case NO_LOGON: {
					int errcode = Integer.parseInt(st.nextToken());
					if (errcode == MSG_ALREADYUSER) {
						logonbox.dispose();
						msgBox = new MessageBox(ct_client, "�α׿�", "�̹� �ٸ� ����ڰ� �ֽ��ϴ�.");
						msgBox.setVisible(true);
					} else if (errcode == MSG_SERVERFULL) {
						logonbox.dispose();
						msgBox = new MessageBox(ct_client, "�α׿�", "��ȭ���� �����Դϴ�.");
						msgBox.setVisible(true);
					}
					break;
				}

				// ��� ���� �޼���
				case REQ_AGREE: {
					String id = st.nextToken(); // �α����� ���̵�
					String counter = st.nextToken(); // ���� ���̵�(Ŭ�� ��)
					String message = counter + "��,  " + id + "�԰��� ����� �����Ͻðڽ��ϱ�?";
					int value = JOptionPane.showConfirmDialog(null, message, "��� ��û", JOptionPane.YES_NO_OPTION);

					if (value == 1) { // no ��ư
						try {
							ct_buffer.setLength(0);
							ct_buffer.append(NO_GAME);
							ct_buffer.append(SEPARATOR);
							ct_buffer.append(id);
							ct_buffer.append(SEPARATOR);
							ct_buffer.append(counter);  //
							send(ct_buffer.toString());
						} catch (IOException e) {
							System.out.println(e);
						}
					} else {
						try {
							ct_buffer.setLength(0);
							ct_buffer.append(YES_GAME);
							ct_buffer.append(SEPARATOR);
							ct_buffer.append(id);
							ct_buffer.append(SEPARATOR);
							ct_buffer.append(counter);
							send(ct_buffer.toString());

						} catch (IOException e) {
							System.out.println(e);
						}
						
						 ct_client.dispose(); 
						 room = new PlayRoom(this, "����������",id,counter);
						 room.pack(); 
						 room.setVisible(true);
						 
					}
					break;
				}

				// ���� ������
				case YES_GAME: {
					ct_client.dispose(); // �α׿� â�� �����.
					String counter = st.nextToken();
					ct_client.dispose(); 
					 room = new PlayRoom(this, "����������", MyId ,counter);
					 room.pack(); 
					 room.setVisible(true);
					break;
				}

				case NO_GAME: {
					int code = Integer.parseInt(st.nextToken());
					try {
						String idTo = st.nextToken();
						String id = st.nextToken();

					if (code == ERR_REJECTION) {
						String message = idTo + "��,  " + id + "���� ������ �ź��Ͽ����ϴ�.";
						JOptionPane.showConfirmDialog(null, message, "���� ��û", JOptionPane.ERROR_MESSAGE);
						break;
					} /*else if (code == ERR_NOUSER) {
						String message = idTo + "��,  " + id + "���� �� �濡 �������� �ʽ��ϴ�.";
						JOptionPane.showConfirmDialog(null, message, "���� ��û", JOptionPane.ERROR_MESSAGE);
						break;
					} */
					else if (code == ERR_ALREADY) {
						String message = idTo + "��,  " + id + "���� ���� ���ӿ� �������Դϴ�.";
						JOptionPane.showConfirmDialog(null, message, "���� ��û", JOptionPane.ERROR_MESSAGE);
						break;
					}
				}catch(NoSuchElementException e) {}
				}
				
				case YES_RESULT:{
					try{  
	            	   String result = st.nextToken(); // ��ȭ�� �������� ID�� ���Ѵ�. 
	                     
	                      room.dr_taContents.setText(result);
	                      room.dr_taContents.append("\n�ѹ� �� �ҷ��� ���� ��ưŬ��!");
	                   }catch(NullPointerException e){
	                	   
	                   }catch(NoSuchElementException e) {}
	                  
	                   break;
	            	   
	               }

				// ������������ ���� �� ���� ���� �޽��� PACKET : YES_ENTERROOM
				/*case YES_ENTERROOM: {
					ct_client.dispose(); // �α׿� â�� �����.
					//room = new DisplayRoom(this, "��ȭ��");
					room.pack();
					room.setVisible(true); // ��ȭ�� â�� ����Ѵ�.
					break;
				}

				// ��ȭ�� ���� �� ���� ���� �޽��� PACKET : NO_ENTERROOM|errCode
				case NO_ENTERROOM: {
					int roomerrcode = Integer.parseInt(st.nextToken());
					if (roomerrcode == MSG_CANNOTOPEN) {
						msgBox = new MessageBox(ct_client, "��ȭ������", "�α׿µ� ����ڰ� �ƴմϴ�.");
						msgBox.setVisible(true);
					}
					break;
				} */

				// ��ȭ�濡 ������ ����� ����Ʈ�� update �Ѵ�.
				// PACKET : MDY_USERIDS|id1'id2'id3.....
				case MDY_USERIDS: {
					ct_client.cc_lstPlayer.removeAll(); // ��� ID�� �����Ѵ�.
					try {
					String ids = st.nextToken(); // ��ȭ�� ������ ����Ʈ
					StringTokenizer roomusers = new StringTokenizer(ids, DELIMETER);
					while (roomusers.hasMoreTokens()) {
						ct_client.cc_lstPlayer.add(roomusers.nextToken());
					}
					}catch(NoSuchElementException e) {
					}
					break;
				}

				// PACKET : MDY_LOGONIDS|id1'id2'id3.....
				case MDY_LOGONIDS: {
					ct_client.cc_lstMember.removeAll(); // ��� ID�� �����Ѵ�.
					
					String ids = st.nextToken(); // �α׿� ����Ʈ
					StringTokenizer logonusers = new StringTokenizer(ids, DELIMETER);
					while (logonusers.hasMoreTokens()) {
						ct_client.cc_lstMember.add(logonusers.nextToken());
					}
				
					break;
				}

			

				// LOGOUT �޽��� ó��
				// PACKET : YES_LOGOUT|Ż����id|Ż���� ���� id1, id2,....
				case YES_LOGOUT: {
					ct_client.cc_tfStatus.setText("�α׾ƿ� ����");
					ct_client.cc_lstMember.removeAll();
					ct_client.cc_tfLogon.setEditable(true);
					ct_client.cc_tfDate.setText("�α׿� �ð��� ǥ�õ˴ϴ�.");
					break;
				}

				// ��� �޽���(YES_QUITROOM) ó�� PACKET : YES_QUITROOM
				case YES_QUITROOM: { // �� ȭ���� �ݰ� �ٽ� �α���â�� �߰� ����� ��
					String id = st.nextToken();

					room.dispose();
					ct_client.setVisible(true);
					break;
				}

				} // switch ����

				Thread.sleep(200);

			} // while ����(������ ����)

		} catch (InterruptedException e) {
			System.out.println(e);
			release();

		} catch (IOException e) {
			System.out.println(e);
			release();
		}
	}

	// ��Ʈ��ũ �ڿ��� �����Ѵ�.
	public void release() {
	};

	// Logon ��Ŷ(REQ_LOGON|ID)�� �����ϰ� �����Ѵ�.
	public void requestLogon(String id) {
		try {
			MyId = id; // chatClient���� id�� �� �޼ҵ�� �޾Ƶ��̴ϱ� MYid�� �����س��´�.
			logonbox = new MessageBox(ct_client, "�α׿�", "������ �α׿� ���Դϴ�.");
			logonbox.setVisible(true);
			ct_buffer.setLength(0); // Logon ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_LOGON);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString()); // Logon ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void requestLogout() {
		if (MyId != null) { // �α����� �����϶��� �α׾ƿ��� �� �� �ִ�.
			try {
				// MyId = id; //chatClient���� id�� �� �޼ҵ�� �޾Ƶ��̴ϱ� MYid�� �����س��´�.
				ct_buffer.setLength(0); // Logout ��Ŷ�� �����Ѵ�.
				ct_buffer.append(REQ_LOGOUT);
				ct_buffer.append(SEPARATOR);
				ct_buffer.append(MyId);
				send(ct_buffer.toString()); // Logout ��Ŷ�� �����Ѵ�.
			} catch (IOException e) {
				System.out.println(e);
			}

		}
	}

	// EnterRoom ��Ŷ(REQ_ENTERROOM|ID)�� �����ϰ� �����Ѵ�.
	public void requestEnterRoom(String id) {
		try {
			ct_buffer.setLength(0); // EnterRoom ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_ENTERROOM);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString()); // EnterRoom ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// QuitRoom ��Ŷ(REQ_QUITROOM|MyID)�� �����ϰ� �����Ѵ�.
	public void requestQuitRoom() {
		try {
			ct_buffer.setLength(0); // EnterRoom ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_QUITROOM);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(MyId); // ����ϴ� ���̵� ����
			send(ct_buffer.toString()); // EnterRoom ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// ������ �³����� ������ �޼����� ������. (REQ_AGREE | ID | ���� )
	public void requestAgree(String counter) {
		try {
			// MyId = id; // chatClient���� id�� �� �޼ҵ�� �޾Ƶ��̴ϱ� MYid�� �����س��´�.
			logonbox = new MessageBox(ct_client, "START", "���濡�� ������ ��û�Ͽ����ϴ�.");
			logonbox.setVisible(true);
			ct_buffer.setLength(0); // Logon ��Ŷ�� �����Ѵ�.
			ct_buffer.append(REQ_AGREE);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(MyId); // ���� �α׿��� ���̵�
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(counter);
			send(ct_buffer.toString()); // Logon ��Ŷ�� �����Ѵ�.
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	//���� ���� �� �Է��� �޾� ���̵�� �Է°�� ������ 
	public void showResult(String counter, String word) {
		try {
		ct_buffer.setLength(0);
		ct_buffer.append(REQ_RESULT);
		ct_buffer.append(SEPARATOR);
		ct_buffer.append(MyId);
		ct_buffer.append(SEPARATOR);
		ct_buffer.append(counter);
		ct_buffer.append(SEPARATOR);
		ct_buffer.append(word);
		
		send(ct_buffer.toString()); 
	}catch (IOException e) {
		System.out.println(e);
	}
	}

	// Ŭ���̾�Ʈ���� �޽����� �����Ѵ�.
	private void send(String sendData) throws IOException {
		ct_out.writeUTF(sendData);
		ct_out.flush();
	}
}
