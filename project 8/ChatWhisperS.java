package com.java.Chapter09;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class ChatWhisperS extends Frame {
	  TextArea display;
	   Label info;
	   List<ServerThread_W> list;
	   //Hashtable hash;
	   //List <HashMap<String,ServerThread_W>> list = new ArrayList<HashMap<String, ServerThread_W>>(); //리스트에 해쉬맵 담기 
	   HashMap<String, ServerThread_W> hash_m = new HashMap<String, ServerThread_W>();
	   public ServerThread_W SThread;
		
	   public ChatWhisperS() {
	      super("서버");
	      info = new Label();
	      add(info, BorderLayout.CENTER);
	      display = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
	      display.setEditable(false);
	      add(display, BorderLayout.SOUTH);
	      addWindowListener(new WinListener());
	      setSize(300,250);
	      setVisible(true);
	   }
		
	   public void runServer() {
	      ServerSocket server;
	      Socket sock;
	      ServerThread_W SThread;
	      try {
	         server = new ServerSocket(5000, 100);
	         //hash = new Hashtable();
	         hash_m = new HashMap<String, ServerThread_W>();
	         //list = new ArrayList<HashMap<String, ServerThread_W>>();
	         list = new ArrayList<ServerThread_W>();
	         try {
	            while(true) {
	               sock = server.accept();
	               SThread = new ServerThread_W(this, sock, display, info);
	               SThread.start();
	               info.setText(sock.getInetAddress().getHostName() + " 서버는 클라이언트와 연결됨");
	            }
	         } catch(IOException ioe) {
	            server.close();
	            ioe.printStackTrace();
	         }
	      } catch(IOException ioe) {
	         ioe.printStackTrace();
	      }
				
	   }

	   public static void main(String args[]) {
	      ChatWhisperS s = new ChatWhisperS();
	      s.runServer();
	   }
			
	   class WinListener extends WindowAdapter {
	      public void windowClosing(WindowEvent e) {
	         System.exit(0);
	      }
	   }
	}
       
	class ServerThread_W extends Thread {
	   Socket sock;
	   BufferedWriter output;
	   BufferedReader input;
	   TextArea display;
	   Label info;
	   TextField text;
	   String clientdata;
	   String serverdata = "";
	   ChatWhisperS cs;
		
	   private static final String SEPARATOR = "|";
	   private static final int REQ_LOGON = 1001;
	   private static final int REQ_LOGOUT = 1002;  //로그아웃 메시지 
	   private static final int REQ_SENDWORDS = 1021;
	   private static final int REQ_WISPERSEND = 1022;

	   public ServerThread_W(ChatWhisperS c, Socket s, TextArea ta, Label l) {
	      sock = s;
	      display = ta;
	      info = l;
	      cs = c;
	      try {
	         input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	         output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	      } catch(IOException ioe) {
	         ioe.printStackTrace();
	      }
	   }
	   public void run() {
	      try {
	         cs.list.add(this); 
	         while((clientdata = input.readLine()) != null) {
	            StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
	            int command = Integer.parseInt(st.nextToken());
	            int Lcnt = cs.list.size();
	            switch(command) {
	               case REQ_LOGON : {
	                  String ID = st.nextToken();
	                  if (cs.hash_m.containsKey(ID)==true) {  //해쉬테이블에 id가 있으면 중복 
	                		int index = cs.list.indexOf(ID);
	                		ServerThread_W SThread = (ServerThread_W) cs.hash_m.get(index);
	                		SThread.output.write("ID 중복\r\n");
	                		SThread.output.flush();
	                		break; 
	                  }
	                  else {  //해쉬 맵에 ID가 없으면 
	                	  display.append("클라이언트가 " + ID + "(으)로 로그인 하였습니다.\r\n");
		                  cs.hash_m.put(ID, this); // 해쉬 테이블에 아이디와 스레드를 저장한다
		                  //cs.list.add(this);
		                  //cs.list.add(cs.hash_m);
		                  ServerThread_W SThread = (ServerThread_W) cs.hash_m.get(ID);
	                		SThread.output.write("로그인 성공\r\n");
	                		SThread.output.flush();
		                  break;
	                  }
	                 
	               }
	               case REQ_LOGOUT : {
	            	   String ID = st.nextToken();
	            	   cs.hash_m.remove(ID, this); //해쉬맵에서 ID 제거 
	            	   cs.list.remove(this);
	            	   display.append("클라이언트 " + ID + " 가 로그아웃 하였습니다.\r\n");
	            	   break;
	               }
	               case REQ_SENDWORDS : {
	                  String ID = st.nextToken();
	                  String message = st.nextToken();
	                  display.append(ID + " : " + message + "\r\n");
	                  for(int i=0; i<Lcnt; i++) {
	                     ServerThread_W SThread = (ServerThread_W)cs.list.get(i);  //(ServerThread_W)cs.list.get(i).get(ID);
	                     SThread.output.write(ID + " : " + message + "\r\n");
	                     SThread.output.flush();
	                  }
	                  break;
	               }
	               case REQ_WISPERSEND : {
	                  String ID = st.nextToken();
	                  String WID = st.nextToken();
	                  String message = st.nextToken();
	                  display.append(ID + " -> " + WID + " : " + message + "\r\n");
	                  ServerThread_W SThread = (ServerThread_W)cs.hash_m.get(ID);
	        // 해쉬테이블에서 귓속말 메시지를 전송한 클라이언트의 스레드를 구함
	                  SThread.output.write(ID + " -> " + WID + " : " + message + "\r\n");
	        // 귓속말 메시지를 전송한 클라이언트에 전송함
	                  SThread.output.flush();
	                  SThread = (ServerThread_W)cs.hash_m.get(WID);
	        // 해쉬테이블에서 귓속말 메시지를 수신할 클라이언트의 스레드를 구함
	                  SThread.output.write(ID + " : " + message + "\r\n");
	        // 귓속말 메시지를 수신할 클라이언트에 전송함
	                  SThread.output.flush();
	                  break;
	               }
	            }
	         }
	      } catch(IOException e) {
	         e.printStackTrace();
	      }
	      cs.list.remove(this);
	      try{
	         sock.close();
	      }catch(IOException ea){
	         ea.printStackTrace();
	      }
	   }
}
