package rock_paper_scissors;
import java.io.*;
import java.net.*;
import java.util.*;

import RockPaperScissors.ServerThread;

public class ServerThread extends Thread {
   private Socket st_sock;
   private DataInputStream st_in;
   private DataOutputStream st_out;
   private StringBuffer st_buffer;
   /* 로그온된 사용자 저장 */
   private static Hashtable<String,ServerThread> logonHash; 
   private static Vector<String> logonVector;  //대화방 입장전에 대화방사용자 목록 관리
   /* 대화방 참여자 저장 */
   private static Hashtable<String,ServerThread> roomHash; 
   private static Vector<String> roomVector;  //대화방 입장 후 사용자 목록 관리 

   private static int isOpenRoom = 0; // 대화방이 개설안됨(초기값)

   private static final String SEPARATOR = "|"; // 메시지간 구분자
   private static final String DELIMETER = "`"; // 소메시지간 구분자
   private static Date starttime;  	// 로그온 시각

   public String st_ID; 			// ID 저장
   public static String first = null;
   public static String second = null;
   private static int first_num=0;
   private static int second_num=0;
   private static ServerThread client1 = null;
   private static ServerThread client2 = null;

   // 메시지 패킷 코드 및 데이터 정의

   // 클라이언트로부터 전달되는 메시지 코드
   private static final int REQ_LOGON = 1001;
   private static final int REQ_AGREE = 1004;
   private static final int RE_AGREE = 1005;
   private static final int REQ_ENTERROOM = 1011;   //대화방 입장버튼 누를때 
   private static final int REQ_SENDWORDS = 1021;
   private static final int REQ_WHISPER = 1022;
   private static final int REQ_LOGOUT = 1031;
   private static final int REQ_QUITROOM = 1041;
   private static final int NO_GAME = 1006; 
   private static final int YES_GAME = 1007;
   private static final int REQ_RESULT = 1050;

   // 클라이언트에 전송하는 메시지 코드
   private static final int YES_LOGON = 2001;
   private static final int NO_LOGON = 2002;
   private static final int YES_ENTERROOM = 2011;
   private static final int NO_ENTERROOM = 2012;
   private static final int MDY_USERIDS = 2013;  // 다른 사용자들한테도 로그인아이디 정보 수정 
   private static final int MDY_LOGONIDS = 2014;
   private static final int YES_SENDWORDS = 2021;
	private static final int YES_WHISPER = 2023;
   private static final int NO_SENDWORDS = 2022;
   private static final int YES_LOGOUT = 2031;
   private static final int NO_LOGOUT = 2032;
   private static final int YES_QUITROOM = 2041;
   private static final int YES_RESULT = 2047;

   // 에러 메시지 코드
   private static final int MSG_ALREADYUSER = 3001;  //기존에 이름이 있을때
   private static final int MSG_SERVERFULL = 3002;  //인원 초과할 경우에 
   private static final int MSG_CANNOTOPEN = 3011; // 
   private static final int ERR_ALREADY = 3004;
   private static final int ERR_REJECITON = 3007; //게임 요청 거절했을 때 
   private static final int ERR_NOUSER = 3008;
  


   static{	
      logonHash = new Hashtable<String,ServerThread>(Rock_paper_Server.cs_maxclient);   //처음의 로그인부분의 해쉬테이블
      logonVector = new Vector<String>(Rock_paper_Server.cs_maxclient); 
      roomHash = new Hashtable<String,ServerThread>(Rock_paper_Server.cs_maxclient);  //대화방에서의 해쉬테이블 
      roomVector = new Vector<String>(Rock_paper_Server.cs_maxclient); 
   }

   public ServerThread(Socket sock){
      try{
         st_sock = sock;
         st_in = new DataInputStream(sock.getInputStream()); 
         st_out = new DataOutputStream(sock.getOutputStream());
         st_buffer = new StringBuffer(2048);
      }catch(IOException e){
         System.out.println(e);
      }
   }

   public void run(){
      try{
         while(true){
            String recvData = st_in.readUTF();
            StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
            int command = Integer.parseInt(st.nextToken());
            switch(command){

               // 로그온 시도 메시지 PACKET : REQ_LOGON|ID
               case REQ_LOGON:{
                  int result;
                  String id = st.nextToken(); // 클라이언트의 ID를 얻는다.
                  result = addUser(id, this); //아이디 중복검사 및 아이디 해쉬테이블에 추가 
                  st_buffer.setLength(0);
                  if(result == 0){  // 접속을 허용한 상태
                	  starttime = new Date(); // 로그인 시간으로 바꿔줌 
                     st_buffer.append(YES_LOGON); 
                     					// YES_LOGON|개설시각|ID1`ID2`..
                     st_buffer.append(SEPARATOR);
                     st_buffer.append(starttime);
                     st_buffer.append(SEPARATOR);
                     String userIDs = getUsers(); //대화방 참여 사용자ID를 구한다
                     st_buffer.append(userIDs);
                     send(st_buffer.toString());
                     
                     modifyLogonUser(userIDs);
                     /*st_buffer.setLength(0);
                     st_buffer.append(MDY_LOGONIDS);
                     st_buffer.append(SEPARATOR);
                     st_buffer.append(userIDs);
                     broadcast(st_buffer.toString()); // MDY_USERIDS 패킷을 전송한다.  */
                     
                     break;
                  }else{  // 접속불가 상태
                     st_buffer.append(NO_LOGON);  // NO_LOGON|errCode
                     st_buffer.append(SEPARATOR);
                     st_buffer.append(result); // 접속불가 원인코드 전송,  MSG_ALREADYUSER...
                     send(st_buffer.toString());
                     break;
                  }
               }
               
               // 클릭한 유저에게 접속 요청하기 위한 메세지 REQ_AGREE | id | counter
               case REQ_AGREE:{
                   //st_buffer.setLength(0);
                   String id = st.nextToken(); // 클라이언트의 ID를 얻는다.
                   String counter = st.nextToken(); //상대방 아이디를 얻는다
                  ServerThread client = null;
                   ServerThread cc = (ServerThread) roomHash.get(counter); 
                   
      
                   if((client = (ServerThread) logonHash.get(counter))!=null){ //가위바위보방에는 없고 로그인목록에는 존재             		   
            		   	if(cc != null){ //이미 가위바위보방에 참가 하였다면 
            			   st_buffer.setLength(0);
	            		   st_buffer.append(NO_GAME);
	            		   st_buffer.append(SEPARATOR);
	            		   st_buffer.append(ERR_ALREADY);
	            		   st_buffer.append(SEPARATOR);
	            		   st_buffer.append(counter);
	            		   send(st_buffer.toString()); 
	            		   break;     
            		   }
                   
                  /* if(checkUserID(id) == null){  //로그인 사용자가 있는지 없는지 check 

                       // NO_ENTERROOM PACKET : NO_ENTERROOM|errCode
                          st_buffer.append(NO_ENTERROOM);
                          st_buffer.append(SEPARATOR);
                          st_buffer.append(MSG_CANNOTOPEN);
                          send(st_buffer.toString());  // NO_ENTERROOM 패킷을 전송한다.
                          break;
                       } */
                   //다시 클라이언트로쓰레드로 -> 해당 유저에게 수락메세지 
                   ServerThread counter_thread = (ServerThread)logonHash.get(counter); //요청받은 상대방에게 수락 메세지를 보내기 위한 쓰레드 
                   st_buffer.setLength(0); //요거 안해줘서 오류 계속 난거 
                   st_buffer.append(REQ_AGREE);
                   st_buffer.append(SEPARATOR);
                   st_buffer.append(id);
                   st_buffer.append(SEPARATOR);
                   st_buffer.append(counter);
                   client.send(st_buffer.toString()); 
                   break;
               }
               }
  
               case YES_GAME:{
            	   String id = st.nextToken();
            	   String idTo = st.nextToken();
            	   
            	   //수락을 하면 두명의 유저가 모두 대결방에 들어가야 함 
            	   roomVector.addElement(id);  
                   roomHash.put(id, this); 
                   roomVector.addElement(idTo);  
                   roomHash.put(idTo, this); 
                   	                 
            	   ServerThread client = null;
            	   client = (ServerThread)logonHash.get(id);
            	   st_buffer.setLength(0);
        		   st_buffer.append(YES_GAME);
        		   st_buffer.append(SEPARATOR);
        		   st_buffer.append(idTo);	        		   
        		   client.send(st_buffer.toString()); 
        		   
        		   String player = getRoomUsers(); //가위바위보방 참가자들 목록 구해오기
        		   st_buffer.setLength(0);
        		   st_buffer.append(MDY_USERIDS);
        		   st_buffer.append(SEPARATOR);
        		   st_buffer.append(player);		
        		   broadcastLogon(st_buffer.toString());  //로그인한 모든 사용자에게 브로드캐스트해준다 
        		    
        		   break;
                }
               //요청한 상대방에게 다시 거절 메세지 
               case NO_GAME:{
                   st_buffer.setLength(0);
                   String idTo = st.nextToken(); 
                   String id = st.nextToken();
                   
                   ServerThread client = null;
                   client = (ServerThread)logonHash.get(idTo);
                   
                   st_buffer.setLength(0);
        		   st_buffer.append(NO_GAME);
        		   st_buffer.append(SEPARATOR);
        		   st_buffer.append(ERR_REJECITON);
        		   st_buffer.append(SEPARATOR);
        		   st_buffer.append(idTo);
        		   st_buffer.append(SEPARATOR);
        		   st_buffer.append(id);
        		   client.send(st_buffer.toString()); 
        		   break;
                }
               

               // 가위바위보방 개설 시도 메시지  PACKET : REQ_ENTERROOM|ID
               case REQ_ENTERROOM:{
                  st_buffer.setLength(0);
                  String id = st.nextToken(); // 클라이언트의 ID를 얻는다.
                  if(checkUserID(id) == null){  //로그인 사용자가 있는지 없는지 check 

                  // NO_ENTERROOM PACKET : NO_ENTERROOM|errCode
                     st_buffer.append(NO_ENTERROOM);
                     st_buffer.append(SEPARATOR);
                     st_buffer.append(MSG_CANNOTOPEN);
                     send(st_buffer.toString());  // NO_ENTERROOM 패킷을 전송한다.
                     break;
                  }

                  roomVector.addElement(id);  // 사용자 ID 추가
                  roomHash.put(id, this); //사용자 ID 및 클라이언트와 통신할  스레드 저장

                  /*if(isOpenRoom == 0){  // 대화방 개설시간 설정
                     isOpenRoom = 1;
                     starttime = new Date();
                  }*/
                  

                  // YES_ENTERROOM PACKET : YES_ENTERROOM
                  st_buffer.append(YES_ENTERROOM); 
                  send(st_buffer.toString()); // YES_ENTERROOM 패킷을 전송한다.

                  //MDY_USERIDS PACKET : MDY_USERIDS|id1'id2' ....
                  st_buffer.setLength(0);
                  st_buffer.append(MDY_USERIDS);
                  st_buffer.append(SEPARATOR);
                  String userIDs = getRoomUsers(); // 대화방 참여 사용자 ID를 구한다
                  st_buffer.append(userIDs);
                  broadcastRoom(st_buffer.toString()); // MDY_USERIDS 패킷을 전송한다.
                  break;
               }


               // LOGOUT 전송 시도 메시지  
               // PACKET : YES_LOGOUT|탈퇴자ID|탈퇴자 이외의 ids
               case REQ_LOGOUT:{
            	   String id = st.nextToken();
            	  
            	   st_buffer.setLength(0);
                   st_buffer.append(YES_LOGOUT);
                   st_buffer.append(SEPARATOR);
                   st_buffer.append(id);
                   send(st_buffer.toString()); //해당 클라이언트에 YES QUITROOM 
                   
                   removeUser(id); //대화창과 로그인창 둘 모두에서 유저를 제거함 
                   String ids = getUsers();
                   String idss = getRoomUsers();
                   modifyLogonUser(ids);
                   modifyRoomUser(ids);
                   break;  //*************** 이거 때문에 고생 
                   
                   /*st_buffer.setLength(0);
                   st_buffer.append(MDY_USERIDS);
                   st_buffer.append(SEPARATOR);
                   String userIDs = getRoomUsers(); // 대화방 참여 사용자 ID를 구한다
                   st_buffer.append(userIDs);
                   broadcastRoom(st_buffer.toString()); // MDY_USERIDS 패킷을 전송한다.
                   break; */
                   																					
               }

               // 방 입장하고 퇴실처리 하였을 때 
               case REQ_QUITROOM:{ 	   
            	   st_buffer.setLength(0);
            	   String id = st.nextToken();
                   st_buffer.append(YES_QUITROOM);
                   st_buffer.append(SEPARATOR);
                   st_buffer.append(id);
                   send(st_buffer.toString()); //해당 클라이언트에 YES QUITROOM 
                   roomVector.removeElement(id);  // 대화방에서도 유저를 제거 
                   roomHash.remove(id, this);
                   String roomUser = getRoomUsers();
                   
                   st_buffer.setLength(0);
                   st_buffer.append(MDY_USERIDS);
                   st_buffer.append(SEPARATOR);
                   st_buffer.append(roomUser);
                   broadcastLogon(st_buffer.toString()); // MDY_USERIDS 패킷을 전송한다.
                   break; 
                   
                   /*try{
                      String data = st.nextToken(); // 대화말을 구한다.
                      st_buffer.append(data);
                   }catch(NoSuchElementException e){}
                   broadcastRoom(st_buffer.toString()); // YES_SENDWORDS 패킷  전송 */
                   
               }
               
               case REQ_RESULT: {
            	   String id = st.nextToken();
            	   String counter = st.nextToken();
            	   //String word = st.nextToken(); // 가위 바위 보 
            	   st_buffer.setLength(0);
            	   st_buffer.append(YES_RESULT);
                   st_buffer.append(SEPARATOR);
            	   String result;
            	   
            	   //첫번째 유저와 두번째 유저 구분하여 값 가져오기 
            	   if(first == null) {  
            		   first = st.nextToken(); 
            		   first_num = Integer.parseInt(first);
            	   }
            	   else {
            		   second = st.nextToken(); 
            		   second_num = Integer.parseInt(second);
            	   }
            	   
            	   //가위1 바위2 보3
            	   if(first!=null&&second!=null) {
            		   if((first_num==1 && second_num==1) || (first_num==2 && second_num==2) || (first_num==3 && second_num==3)) {
            			   result = "무승부입니다!";
            			   st_buffer.append(result);
            			   broadcastLogon(st_buffer.toString());
            			   first=second=null;
            			   break; 
            		   }
            		   else if((first_num==1 && second_num==3) || (first_num==2 && second_num==1) ||  (first_num==3 && second_num==2)) {
            			   result = counter + " : 승리    " + id + " : 패배";
            			   st_buffer.append(result);
            			   broadcastLogon(st_buffer.toString());
            			   first=second=null;
            			   break; 
            		   }
            		   else if((first_num==1 && second_num==2) || (first_num==2 && second_num==3) ||  (first_num==3 && second_num==1)) {
            			   result = id + " : 승리   " + counter + " : 패배    ";
            			   st_buffer.append(result);
            			   broadcastLogon(st_buffer.toString());
            			   first=second=null;
            			   break; 
            		   }
            	   }

            	   } // switch 종료
               }
            Thread.sleep(100);
         } //while 종료
               
      }catch(NullPointerException e){ // 로그아웃시 st_in이 이 예외를 발생하므로
      }catch(InterruptedException e){
      }catch(IOException e){
      }catch(NoSuchElementException e){}
   }


   // 자원을 해제한다.

   public void release(){}

   /* 해쉬 테이블에 접속을 요청한 클라이언트의 ID 및 전송을 담당하는 스레드를 등록.
          즉, 해쉬 테이블은 대화를 하는 클라이언트의 리스트를 포함. */
    private static synchronized int addUser(String id, ServerThread client){
      if(checkUserID(id) != null){ //아이디가 이미 존재하면 
         return MSG_ALREADYUSER;
      }  
      if(logonHash.size() >= Rock_paper_Server.cs_maxclient){
         return MSG_SERVERFULL;
      }
      logonVector.addElement(id);  // 사용자 ID 추가
      logonHash.put(id, client); // 사용자 ID 및 클라이언트와 통신할 스레드를 저장한다.
      client.st_ID = id;
      return 0; // 클라이언트와 성공적으로 접속하고, 대화방이 이미 개설된 상태.
   }
    
    //로그아웃을 하면 벡터에서 유저를 제거 
    private static synchronized void removeUser(String id){
        if(checkUserID(id) != null){ //아이디가 이미 존재하면 
           
        	logonVector.removeElement(id);  // 로그인창에서 유저를 제거 
        	logonHash.remove(id);
        	roomVector.removeElement(id);  // 대화방에서도 유저를 제거 
        	roomHash.remove(id);
        	
        }
    }

   /* 접속을 요청한 사용자의 ID와 일치하는 ID가 이미 사용되는 지를 조사한다.
           반환값이 null이라면 요구한 ID로 대화방 입장이 가능함. */
   private static ServerThread checkUserID(String id){
      ServerThread alreadyClient = null;
      alreadyClient = (ServerThread) logonHash.get(id);
      return alreadyClient;
   }

   // 로그온에 참여한 사용자 ID를 구한다.
   private String getUsers(){
      StringBuffer id = new StringBuffer();
      String ids;
      Enumeration<String> enu = logonVector.elements();//객체들의 집합(Vector)에서 각각의 객체들을 한순간에 하나씩 처리할 수 있는 메소드를 제공
      while(enu.hasMoreElements()){       // MSG_TYPE | ID | id1' id2' id3' ...
         id.append(enu.nextElement());
         id.append(DELIMETER); 
      }
      try{
         ids = new String(id);  // 문자열로 변환한다.
         ids = ids.substring(0, ids.length()-1); // 마지막 "`"를 삭제한다.
      }catch(StringIndexOutOfBoundsException e){
         return "";
      }
      return ids;
   }

   // 대화방에 참여한 사용자 ID를 구한다.

   private String getRoomUsers(){
      StringBuffer id = new StringBuffer();
      String ids;
      Enumeration<String> enu = roomVector.elements();
      while(enu.hasMoreElements()){
         id.append(enu.nextElement());
         id.append(DELIMETER);   //id'id'id ...
      }
      try{
         ids = new String(id);
         ids = ids.substring(0, ids.length()-1); // 마지막 "`"를 삭제한다.
      }catch(StringIndexOutOfBoundsException e){
         return "";
      }
      return ids;
   }
   
   //로그인창(초기화면)의 사용자목록을 업데이트
   private void modifyLogonUser(String userIDs) throws IOException {
	     st_buffer.setLength(0);
         st_buffer.append(MDY_LOGONIDS);
         st_buffer.append(SEPARATOR);
         st_buffer.append(userIDs);
         broadcastLogon(st_buffer.toString()); // MDY_USERIDS 패킷을 전송한다.
   }
   
   //대화방의 유저목록을 업데이트해줌
   private void modifyRoomUser(String userIDs) throws IOException {
	   st_buffer.setLength(0);
       st_buffer.append(MDY_USERIDS);
       st_buffer.append(SEPARATOR);
       st_buffer.append(userIDs);
       broadcastRoom(st_buffer.toString()); // MDY_USERIDS 패킷을 전송한다.
 }

   // 대화방에 참여한 모든 사용자(브로드케스팅)에게 데이터를 전송한다, 로그인창과 대화방을 구분하여 브로드캐스팅 해주어야 함 
   public synchronized void broadcastRoom(String sendData) throws IOException{
      ServerThread client;
		Enumeration<String> enu = roomVector.elements();
      while(enu.hasMoreElements()){
         client = (ServerThread) roomHash.get(enu.nextElement());
         client.send(sendData);
      }
   }
   
   public synchronized void broadcastLogon(String sendData) throws IOException{
	      ServerThread client;
	      Enumeration<String> enu = logonVector.elements();
	      while(enu.hasMoreElements()){
	         client = (ServerThread) logonHash.get(enu.nextElement());
	         client.send(sendData);
	      }
	   }


   // 데이터를 전송한다.
   public void send(String sendData) throws IOException{
      synchronized(st_out){
         st_out.writeUTF(sendData);
         st_out.flush();
      }
   }
}   

