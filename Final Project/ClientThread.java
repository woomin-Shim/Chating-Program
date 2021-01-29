package rock_paper_scissors;

import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

import RockPaperScissors.PlayGame;

import java.net.*;

public class ClientThread extends Thread {

	private Rock_paper_Client ct_client; // ChatClient 객체
	private Socket ct_sock; // 클라이언트 소켓
	private DataInputStream ct_in; // 입력 스트림
	private DataOutputStream ct_out; // 출력 스트림
	private StringBuffer ct_buffer; // 버퍼
	private Thread thisThread;
	private PlayRoom room;
	public String MyId = null;

	private static final String SEPARATOR = "|";
	private static final String DELIMETER = "`";

	// 메시지 패킷 코드 및 데이터 정의

	// 서버에 전송하는 메시지 코드
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

	// 서버로부터 전송되는 메시지 코드
	private static final int YES_LOGON = 2001;
	private static final int NO_LOGON = 2002;
	private static final int YES_ENTERROOM = 2011;
	private static final int NO_ENTERROOM = 2012;
	private static final int MDY_USERIDS = 2013; // 방에 있는 유저와 로그온창에있는 유저를 구분하기위해
	private static final int MDY_LOGONIDS = 2014;
	private static final int YES_SENDWORDS = 2021;
	private static final int YES_WHISPER = 2023;
	private static final int NO_SENDWORDS = 2022;
	private static final int YES_LOGOUT = 2031;
	private static final int NO_LOGOUT = 2032;
	private static final int YES_QUITROOM = 2041;
	private static final int YES_RESULT = 2047;

	// 에러 메시지 코드
	private static final int MSG_ALREADYUSER = 3001;
	private static final int MSG_SERVERFULL = 3002;
	private static final int MSG_CANNOTOPEN = 3011;
	private static final int ERR_ALREADY = 3004;
	private static final int ERR_REJECTION = 3007;
	private static final int ERR_NOUSER = 3008;
	

	private static MessageBox msgBox, logonbox;

	/*
	 * 원격호스트와 연결을 위한 생성자 실행 : java ChatClient 호스트이름 포트번호 To DO .....
	 */

	// 로컬호스트에서 사용하기 위하여 만든 생성자
	// 서버와 클라이언트가 같은 시스템을 사용한다.
	public ClientThread(Rock_paper_Client client) {
		try {
			ct_sock = new Socket(InetAddress.getLocalHost(), 2777); // 서버로 TCP 연결
			ct_in = new DataInputStream(ct_sock.getInputStream());
			ct_out = new DataOutputStream(ct_sock.getOutputStream());
			ct_buffer = new StringBuffer(4096);
			thisThread = this; //
			ct_client = client; // 객체변수에 할당
		} catch (IOException e) {
			MessageBoxLess msgout = new MessageBoxLess(client, "연결에러", "서버에 접속할 수 없습니다.");
			msgout.setVisible(true);
		}
	}

	public void run() {

		try {
			Thread currThread = Thread.currentThread(); // 현재 실행중인 쓰레드를 받아옴
			while (currThread == thisThread) { // 종료는 LOG_OFF에서 thisThread=null;에 의하여
				String recvData = ct_in.readUTF();
				StringTokenizer st = new StringTokenizer(recvData, SEPARATOR); // SEPATATOR로 recvdata 쪼갬
				int command = Integer.parseInt(st.nextToken());
				switch (command) {

				/* 서버가 보내오는 메시지를 확인하는 부분 */
				// 로그온 성공 메시지 PACKET : YES_LOGON|개설시각|ID1`ID2`ID3...
				case YES_LOGON: {
					logonbox.dispose(); // 메세지박스를 없애준다.
					ct_client.cc_tfStatus.setText("로그온이 성공했습니다."); // chatClient의 멤버인 cc_tfStatus(다른 클래스이 멤버)
					String date = st.nextToken(); // 대화방 개설시간 -> 로그온 시간으로 변경하기

					ct_client.cc_tfDate.setText(date);
					String ids = st.nextToken(); // 대화방 대기자 리스트
					StringTokenizer users = new StringTokenizer(ids, DELIMETER); // ID1`ID2`ID3...
					while (users.hasMoreTokens()) {
						ct_client.cc_lstMember.add(users.nextToken()); // add to list object
					}

					ct_client.cc_tfLogon.setEditable(false);
					break;
				}

				// 로그온 실패 또는 로그온하고 대화방이 개설되지 않은 상태
				// PACKET : NO_LOGON|errCode
				case NO_LOGON: {
					int errcode = Integer.parseInt(st.nextToken());
					if (errcode == MSG_ALREADYUSER) {
						logonbox.dispose();
						msgBox = new MessageBox(ct_client, "로그온", "이미 다른 사용자가 있습니다.");
						msgBox.setVisible(true);
					} else if (errcode == MSG_SERVERFULL) {
						logonbox.dispose();
						msgBox = new MessageBox(ct_client, "로그온", "대화방이 만원입니다.");
						msgBox.setVisible(true);
					}
					break;
				}

				// 대결 수락 메세지
				case REQ_AGREE: {
					String id = st.nextToken(); // 로그인한 아이디
					String counter = st.nextToken(); // 상대방 아이디(클릭 한)
					String message = counter + "님,  " + id + "님과의 대결을 수락하시겠습니까?";
					int value = JOptionPane.showConfirmDialog(null, message, "대결 요청", JOptionPane.YES_NO_OPTION);

					if (value == 1) { // no 버튼
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
						 room = new PlayRoom(this, "가위바위보",id,counter);
						 room.pack(); 
						 room.setVisible(true);
						 
					}
					break;
				}

				// 게임 수락시
				case YES_GAME: {
					ct_client.dispose(); // 로그온 창을 지운다.
					String counter = st.nextToken();
					ct_client.dispose(); 
					 room = new PlayRoom(this, "가위바위보", MyId ,counter);
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
						String message = idTo + "님,  " + id + "님이 게임을 거부하였습니다.";
						JOptionPane.showConfirmDialog(null, message, "게임 요청", JOptionPane.ERROR_MESSAGE);
						break;
					} /*else if (code == ERR_NOUSER) {
						String message = idTo + "님,  " + id + "님은 이 방에 존재하지 않습니다.";
						JOptionPane.showConfirmDialog(null, message, "게임 요청", JOptionPane.ERROR_MESSAGE);
						break;
					} */
					else if (code == ERR_ALREADY) {
						String message = idTo + "님,  " + id + "님은 지금 게임에 참여중입니다.";
						JOptionPane.showConfirmDialog(null, message, "게임 요청", JOptionPane.ERROR_MESSAGE);
						break;
					}
				}catch(NoSuchElementException e) {}
				}
				
				case YES_RESULT:{
					try{  
	            	   String result = st.nextToken(); // 대화말 전송자의 ID를 구한다. 
	                     
	                      room.dr_taContents.setText(result);
	                      room.dr_taContents.append("\n한번 더 할려면 재경기 버튼클릭!");
	                   }catch(NullPointerException e){
	                	   
	                   }catch(NoSuchElementException e) {}
	                  
	                   break;
	            	   
	               }

				// 가위바위보방 개설 및 입장 성공 메시지 PACKET : YES_ENTERROOM
				/*case YES_ENTERROOM: {
					ct_client.dispose(); // 로그온 창을 지운다.
					//room = new DisplayRoom(this, "대화방");
					room.pack();
					room.setVisible(true); // 대화방 창을 출력한다.
					break;
				}

				// 대화방 개설 및 입장 실패 메시지 PACKET : NO_ENTERROOM|errCode
				case NO_ENTERROOM: {
					int roomerrcode = Integer.parseInt(st.nextToken());
					if (roomerrcode == MSG_CANNOTOPEN) {
						msgBox = new MessageBox(ct_client, "대화방입장", "로그온된 사용자가 아닙니다.");
						msgBox.setVisible(true);
					}
					break;
				} */

				// 대화방에 참여한 사용자 리스트를 update 한다.
				// PACKET : MDY_USERIDS|id1'id2'id3.....
				case MDY_USERIDS: {
					ct_client.cc_lstPlayer.removeAll(); // 모든 ID를 삭제한다.
					try {
					String ids = st.nextToken(); // 대화방 참여자 리스트
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
					ct_client.cc_lstMember.removeAll(); // 모든 ID를 삭제한다.
					
					String ids = st.nextToken(); // 로그온 리스트
					StringTokenizer logonusers = new StringTokenizer(ids, DELIMETER);
					while (logonusers.hasMoreTokens()) {
						ct_client.cc_lstMember.add(logonusers.nextToken());
					}
				
					break;
				}

			

				// LOGOUT 메시지 처리
				// PACKET : YES_LOGOUT|탈퇴자id|탈퇴자 제외 id1, id2,....
				case YES_LOGOUT: {
					ct_client.cc_tfStatus.setText("로그아웃 성공");
					ct_client.cc_lstMember.removeAll();
					ct_client.cc_tfLogon.setEditable(true);
					ct_client.cc_tfDate.setText("로그온 시간이 표시됩니다.");
					break;
				}

				// 퇴실 메시지(YES_QUITROOM) 처리 PACKET : YES_QUITROOM
				case YES_QUITROOM: { // 룸 화면을 닫고 다시 로그인창은 뜨게 해줘야 함
					String id = st.nextToken();

					room.dispose();
					ct_client.setVisible(true);
					break;
				}

				} // switch 종료

				Thread.sleep(200);

			} // while 종료(스레드 종료)

		} catch (InterruptedException e) {
			System.out.println(e);
			release();

		} catch (IOException e) {
			System.out.println(e);
			release();
		}
	}

	// 네트워크 자원을 해제한다.
	public void release() {
	};

	// Logon 패킷(REQ_LOGON|ID)을 생성하고 전송한다.
	public void requestLogon(String id) {
		try {
			MyId = id; // chatClient에서 id를 이 메소드로 받아들이니까 MYid에 저장해놓는다.
			logonbox = new MessageBox(ct_client, "로그온", "서버에 로그온 중입니다.");
			logonbox.setVisible(true);
			ct_buffer.setLength(0); // Logon 패킷을 생성한다.
			ct_buffer.append(REQ_LOGON);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString()); // Logon 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void requestLogout() {
		if (MyId != null) { // 로그인한 상태일때만 로그아웃을 할 수 있다.
			try {
				// MyId = id; //chatClient에서 id를 이 메소드로 받아들이니까 MYid에 저장해놓는다.
				ct_buffer.setLength(0); // Logout 패킷을 생성한다.
				ct_buffer.append(REQ_LOGOUT);
				ct_buffer.append(SEPARATOR);
				ct_buffer.append(MyId);
				send(ct_buffer.toString()); // Logout 패킷을 전송한다.
			} catch (IOException e) {
				System.out.println(e);
			}

		}
	}

	// EnterRoom 패킷(REQ_ENTERROOM|ID)을 생성하고 전송한다.
	public void requestEnterRoom(String id) {
		try {
			ct_buffer.setLength(0); // EnterRoom 패킷을 생성한다.
			ct_buffer.append(REQ_ENTERROOM);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(id);
			send(ct_buffer.toString()); // EnterRoom 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// QuitRoom 패킷(REQ_QUITROOM|MyID)을 생성하고 전송한다.
	public void requestQuitRoom() {
		try {
			ct_buffer.setLength(0); // EnterRoom 패킷을 생성한다.
			ct_buffer.append(REQ_QUITROOM);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(MyId); // 퇴실하는 아이디를 보냄
			send(ct_buffer.toString()); // EnterRoom 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// 게임을 승낙할지 안할지 메세지를 보낸다. (REQ_AGREE | ID | 상대방 )
	public void requestAgree(String counter) {
		try {
			// MyId = id; // chatClient에서 id를 이 메소드로 받아들이니까 MYid에 저장해놓는다.
			logonbox = new MessageBox(ct_client, "START", "상대방에게 게임을 요청하였습니다.");
			logonbox.setVisible(true);
			ct_buffer.setLength(0); // Logon 패킷을 생성한다.
			ct_buffer.append(REQ_AGREE);
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(MyId); // 현재 로그온한 아이디
			ct_buffer.append(SEPARATOR);
			ct_buffer.append(counter);
			send(ct_buffer.toString()); // Logon 패킷을 전송한다.
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	//가위 바위 보 입력을 받아 아이디와 입력결과 서버로 
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

	// 클라이언트에서 메시지를 전송한다.
	private void send(String sendData) throws IOException {
		ct_out.writeUTF(sendData);
		ct_out.flush();
	}
}
