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
   /* �α׿µ� ����� ���� */
   private static Hashtable<String,ServerThread> logonHash; 
   private static Vector<String> logonVector;  //��ȭ�� �������� ��ȭ������ ��� ����
   /* ��ȭ�� ������ ���� */
   private static Hashtable<String,ServerThread> roomHash; 
   private static Vector<String> roomVector;  //��ȭ�� ���� �� ����� ��� ���� 

   private static int isOpenRoom = 0; // ��ȭ���� �����ȵ�(�ʱⰪ)

   private static final String SEPARATOR = "|"; // �޽����� ������
   private static final String DELIMETER = "`"; // �Ҹ޽����� ������
   private static Date starttime;  	// �α׿� �ð�

   public String st_ID; 			// ID ����
   public static String first = null;
   public static String second = null;
   private static int first_num=0;
   private static int second_num=0;
   private static ServerThread client1 = null;
   private static ServerThread client2 = null;

   // �޽��� ��Ŷ �ڵ� �� ������ ����

   // Ŭ���̾�Ʈ�κ��� ���޵Ǵ� �޽��� �ڵ�
   private static final int REQ_LOGON = 1001;
   private static final int REQ_AGREE = 1004;
   private static final int RE_AGREE = 1005;
   private static final int REQ_ENTERROOM = 1011;   //��ȭ�� �����ư ������ 
   private static final int REQ_SENDWORDS = 1021;
   private static final int REQ_WHISPER = 1022;
   private static final int REQ_LOGOUT = 1031;
   private static final int REQ_QUITROOM = 1041;
   private static final int NO_GAME = 1006; 
   private static final int YES_GAME = 1007;
   private static final int REQ_RESULT = 1050;

   // Ŭ���̾�Ʈ�� �����ϴ� �޽��� �ڵ�
   private static final int YES_LOGON = 2001;
   private static final int NO_LOGON = 2002;
   private static final int YES_ENTERROOM = 2011;
   private static final int NO_ENTERROOM = 2012;
   private static final int MDY_USERIDS = 2013;  // �ٸ� ����ڵ����׵� �α��ξ��̵� ���� ���� 
   private static final int MDY_LOGONIDS = 2014;
   private static final int YES_SENDWORDS = 2021;
	private static final int YES_WHISPER = 2023;
   private static final int NO_SENDWORDS = 2022;
   private static final int YES_LOGOUT = 2031;
   private static final int NO_LOGOUT = 2032;
   private static final int YES_QUITROOM = 2041;
   private static final int YES_RESULT = 2047;

   // ���� �޽��� �ڵ�
   private static final int MSG_ALREADYUSER = 3001;  //������ �̸��� ������
   private static final int MSG_SERVERFULL = 3002;  //�ο� �ʰ��� ��쿡 
   private static final int MSG_CANNOTOPEN = 3011; // 
   private static final int ERR_ALREADY = 3004;
   private static final int ERR_REJECITON = 3007; //���� ��û �������� �� 
   private static final int ERR_NOUSER = 3008;
  


   static{	
      logonHash = new Hashtable<String,ServerThread>(Rock_paper_Server.cs_maxclient);   //ó���� �α��κκ��� �ؽ����̺�
      logonVector = new Vector<String>(Rock_paper_Server.cs_maxclient); 
      roomHash = new Hashtable<String,ServerThread>(Rock_paper_Server.cs_maxclient);  //��ȭ�濡���� �ؽ����̺� 
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

               // �α׿� �õ� �޽��� PACKET : REQ_LOGON|ID
               case REQ_LOGON:{
                  int result;
                  String id = st.nextToken(); // Ŭ���̾�Ʈ�� ID�� ��´�.
                  result = addUser(id, this); //���̵� �ߺ��˻� �� ���̵� �ؽ����̺� �߰� 
                  st_buffer.setLength(0);
                  if(result == 0){  // ������ ����� ����
                	  starttime = new Date(); // �α��� �ð����� �ٲ��� 
                     st_buffer.append(YES_LOGON); 
                     					// YES_LOGON|�����ð�|ID1`ID2`..
                     st_buffer.append(SEPARATOR);
                     st_buffer.append(starttime);
                     st_buffer.append(SEPARATOR);
                     String userIDs = getUsers(); //��ȭ�� ���� �����ID�� ���Ѵ�
                     st_buffer.append(userIDs);
                     send(st_buffer.toString());
                     
                     modifyLogonUser(userIDs);
                     /*st_buffer.setLength(0);
                     st_buffer.append(MDY_LOGONIDS);
                     st_buffer.append(SEPARATOR);
                     st_buffer.append(userIDs);
                     broadcast(st_buffer.toString()); // MDY_USERIDS ��Ŷ�� �����Ѵ�.  */
                     
                     break;
                  }else{  // ���ӺҰ� ����
                     st_buffer.append(NO_LOGON);  // NO_LOGON|errCode
                     st_buffer.append(SEPARATOR);
                     st_buffer.append(result); // ���ӺҰ� �����ڵ� ����,  MSG_ALREADYUSER...
                     send(st_buffer.toString());
                     break;
                  }
               }
               
               // Ŭ���� �������� ���� ��û�ϱ� ���� �޼��� REQ_AGREE | id | counter
               case REQ_AGREE:{
                   //st_buffer.setLength(0);
                   String id = st.nextToken(); // Ŭ���̾�Ʈ�� ID�� ��´�.
                   String counter = st.nextToken(); //���� ���̵� ��´�
                  ServerThread client = null;
                   ServerThread cc = (ServerThread) roomHash.get(counter); 
                   
      
                   if((client = (ServerThread) logonHash.get(counter))!=null){ //�����������濡�� ���� �α��θ�Ͽ��� ����             		   
            		   	if(cc != null){ //�̹� �����������濡 ���� �Ͽ��ٸ� 
            			   st_buffer.setLength(0);
	            		   st_buffer.append(NO_GAME);
	            		   st_buffer.append(SEPARATOR);
	            		   st_buffer.append(ERR_ALREADY);
	            		   st_buffer.append(SEPARATOR);
	            		   st_buffer.append(counter);
	            		   send(st_buffer.toString()); 
	            		   break;     
            		   }
                   
                  /* if(checkUserID(id) == null){  //�α��� ����ڰ� �ִ��� ������ check 

                       // NO_ENTERROOM PACKET : NO_ENTERROOM|errCode
                          st_buffer.append(NO_ENTERROOM);
                          st_buffer.append(SEPARATOR);
                          st_buffer.append(MSG_CANNOTOPEN);
                          send(st_buffer.toString());  // NO_ENTERROOM ��Ŷ�� �����Ѵ�.
                          break;
                       } */
                   //�ٽ� Ŭ���̾�Ʈ�ξ������ -> �ش� �������� �����޼��� 
                   ServerThread counter_thread = (ServerThread)logonHash.get(counter); //��û���� ���濡�� ���� �޼����� ������ ���� ������ 
                   st_buffer.setLength(0); //��� �����༭ ���� ��� ���� 
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
            	   
            	   //������ �ϸ� �θ��� ������ ��� ���濡 ���� �� 
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
        		   
        		   String player = getRoomUsers(); //������������ �����ڵ� ��� ���ؿ���
        		   st_buffer.setLength(0);
        		   st_buffer.append(MDY_USERIDS);
        		   st_buffer.append(SEPARATOR);
        		   st_buffer.append(player);		
        		   broadcastLogon(st_buffer.toString());  //�α����� ��� ����ڿ��� ��ε�ĳ��Ʈ���ش� 
        		    
        		   break;
                }
               //��û�� ���濡�� �ٽ� ���� �޼��� 
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
               

               // ������������ ���� �õ� �޽���  PACKET : REQ_ENTERROOM|ID
               case REQ_ENTERROOM:{
                  st_buffer.setLength(0);
                  String id = st.nextToken(); // Ŭ���̾�Ʈ�� ID�� ��´�.
                  if(checkUserID(id) == null){  //�α��� ����ڰ� �ִ��� ������ check 

                  // NO_ENTERROOM PACKET : NO_ENTERROOM|errCode
                     st_buffer.append(NO_ENTERROOM);
                     st_buffer.append(SEPARATOR);
                     st_buffer.append(MSG_CANNOTOPEN);
                     send(st_buffer.toString());  // NO_ENTERROOM ��Ŷ�� �����Ѵ�.
                     break;
                  }

                  roomVector.addElement(id);  // ����� ID �߰�
                  roomHash.put(id, this); //����� ID �� Ŭ���̾�Ʈ�� �����  ������ ����

                  /*if(isOpenRoom == 0){  // ��ȭ�� �����ð� ����
                     isOpenRoom = 1;
                     starttime = new Date();
                  }*/
                  

                  // YES_ENTERROOM PACKET : YES_ENTERROOM
                  st_buffer.append(YES_ENTERROOM); 
                  send(st_buffer.toString()); // YES_ENTERROOM ��Ŷ�� �����Ѵ�.

                  //MDY_USERIDS PACKET : MDY_USERIDS|id1'id2' ....
                  st_buffer.setLength(0);
                  st_buffer.append(MDY_USERIDS);
                  st_buffer.append(SEPARATOR);
                  String userIDs = getRoomUsers(); // ��ȭ�� ���� ����� ID�� ���Ѵ�
                  st_buffer.append(userIDs);
                  broadcastRoom(st_buffer.toString()); // MDY_USERIDS ��Ŷ�� �����Ѵ�.
                  break;
               }


               // LOGOUT ���� �õ� �޽���  
               // PACKET : YES_LOGOUT|Ż����ID|Ż���� �̿��� ids
               case REQ_LOGOUT:{
            	   String id = st.nextToken();
            	  
            	   st_buffer.setLength(0);
                   st_buffer.append(YES_LOGOUT);
                   st_buffer.append(SEPARATOR);
                   st_buffer.append(id);
                   send(st_buffer.toString()); //�ش� Ŭ���̾�Ʈ�� YES QUITROOM 
                   
                   removeUser(id); //��ȭâ�� �α���â �� ��ο��� ������ ������ 
                   String ids = getUsers();
                   String idss = getRoomUsers();
                   modifyLogonUser(ids);
                   modifyRoomUser(ids);
                   break;  //*************** �̰� ������ ��� 
                   
                   /*st_buffer.setLength(0);
                   st_buffer.append(MDY_USERIDS);
                   st_buffer.append(SEPARATOR);
                   String userIDs = getRoomUsers(); // ��ȭ�� ���� ����� ID�� ���Ѵ�
                   st_buffer.append(userIDs);
                   broadcastRoom(st_buffer.toString()); // MDY_USERIDS ��Ŷ�� �����Ѵ�.
                   break; */
                   																					
               }

               // �� �����ϰ� ���ó�� �Ͽ��� �� 
               case REQ_QUITROOM:{ 	   
            	   st_buffer.setLength(0);
            	   String id = st.nextToken();
                   st_buffer.append(YES_QUITROOM);
                   st_buffer.append(SEPARATOR);
                   st_buffer.append(id);
                   send(st_buffer.toString()); //�ش� Ŭ���̾�Ʈ�� YES QUITROOM 
                   roomVector.removeElement(id);  // ��ȭ�濡���� ������ ���� 
                   roomHash.remove(id, this);
                   String roomUser = getRoomUsers();
                   
                   st_buffer.setLength(0);
                   st_buffer.append(MDY_USERIDS);
                   st_buffer.append(SEPARATOR);
                   st_buffer.append(roomUser);
                   broadcastLogon(st_buffer.toString()); // MDY_USERIDS ��Ŷ�� �����Ѵ�.
                   break; 
                   
                   /*try{
                      String data = st.nextToken(); // ��ȭ���� ���Ѵ�.
                      st_buffer.append(data);
                   }catch(NoSuchElementException e){}
                   broadcastRoom(st_buffer.toString()); // YES_SENDWORDS ��Ŷ  ���� */
                   
               }
               
               case REQ_RESULT: {
            	   String id = st.nextToken();
            	   String counter = st.nextToken();
            	   //String word = st.nextToken(); // ���� ���� �� 
            	   st_buffer.setLength(0);
            	   st_buffer.append(YES_RESULT);
                   st_buffer.append(SEPARATOR);
            	   String result;
            	   
            	   //ù��° ������ �ι�° ���� �����Ͽ� �� �������� 
            	   if(first == null) {  
            		   first = st.nextToken(); 
            		   first_num = Integer.parseInt(first);
            	   }
            	   else {
            		   second = st.nextToken(); 
            		   second_num = Integer.parseInt(second);
            	   }
            	   
            	   //����1 ����2 ��3
            	   if(first!=null&&second!=null) {
            		   if((first_num==1 && second_num==1) || (first_num==2 && second_num==2) || (first_num==3 && second_num==3)) {
            			   result = "���º��Դϴ�!";
            			   st_buffer.append(result);
            			   broadcastLogon(st_buffer.toString());
            			   first=second=null;
            			   break; 
            		   }
            		   else if((first_num==1 && second_num==3) || (first_num==2 && second_num==1) ||  (first_num==3 && second_num==2)) {
            			   result = counter + " : �¸�    " + id + " : �й�";
            			   st_buffer.append(result);
            			   broadcastLogon(st_buffer.toString());
            			   first=second=null;
            			   break; 
            		   }
            		   else if((first_num==1 && second_num==2) || (first_num==2 && second_num==3) ||  (first_num==3 && second_num==1)) {
            			   result = id + " : �¸�   " + counter + " : �й�    ";
            			   st_buffer.append(result);
            			   broadcastLogon(st_buffer.toString());
            			   first=second=null;
            			   break; 
            		   }
            	   }

            	   } // switch ����
               }
            Thread.sleep(100);
         } //while ����
               
      }catch(NullPointerException e){ // �α׾ƿ��� st_in�� �� ���ܸ� �߻��ϹǷ�
      }catch(InterruptedException e){
      }catch(IOException e){
      }catch(NoSuchElementException e){}
   }


   // �ڿ��� �����Ѵ�.

   public void release(){}

   /* �ؽ� ���̺� ������ ��û�� Ŭ���̾�Ʈ�� ID �� ������ ����ϴ� �����带 ���.
          ��, �ؽ� ���̺��� ��ȭ�� �ϴ� Ŭ���̾�Ʈ�� ����Ʈ�� ����. */
    private static synchronized int addUser(String id, ServerThread client){
      if(checkUserID(id) != null){ //���̵� �̹� �����ϸ� 
         return MSG_ALREADYUSER;
      }  
      if(logonHash.size() >= Rock_paper_Server.cs_maxclient){
         return MSG_SERVERFULL;
      }
      logonVector.addElement(id);  // ����� ID �߰�
      logonHash.put(id, client); // ����� ID �� Ŭ���̾�Ʈ�� ����� �����带 �����Ѵ�.
      client.st_ID = id;
      return 0; // Ŭ���̾�Ʈ�� ���������� �����ϰ�, ��ȭ���� �̹� ������ ����.
   }
    
    //�α׾ƿ��� �ϸ� ���Ϳ��� ������ ���� 
    private static synchronized void removeUser(String id){
        if(checkUserID(id) != null){ //���̵� �̹� �����ϸ� 
           
        	logonVector.removeElement(id);  // �α���â���� ������ ���� 
        	logonHash.remove(id);
        	roomVector.removeElement(id);  // ��ȭ�濡���� ������ ���� 
        	roomHash.remove(id);
        	
        }
    }

   /* ������ ��û�� ������� ID�� ��ġ�ϴ� ID�� �̹� ���Ǵ� ���� �����Ѵ�.
           ��ȯ���� null�̶�� �䱸�� ID�� ��ȭ�� ������ ������. */
   private static ServerThread checkUserID(String id){
      ServerThread alreadyClient = null;
      alreadyClient = (ServerThread) logonHash.get(id);
      return alreadyClient;
   }

   // �α׿¿� ������ ����� ID�� ���Ѵ�.
   private String getUsers(){
      StringBuffer id = new StringBuffer();
      String ids;
      Enumeration<String> enu = logonVector.elements();//��ü���� ����(Vector)���� ������ ��ü���� �Ѽ����� �ϳ��� ó���� �� �ִ� �޼ҵ带 ����
      while(enu.hasMoreElements()){       // MSG_TYPE | ID | id1' id2' id3' ...
         id.append(enu.nextElement());
         id.append(DELIMETER); 
      }
      try{
         ids = new String(id);  // ���ڿ��� ��ȯ�Ѵ�.
         ids = ids.substring(0, ids.length()-1); // ������ "`"�� �����Ѵ�.
      }catch(StringIndexOutOfBoundsException e){
         return "";
      }
      return ids;
   }

   // ��ȭ�濡 ������ ����� ID�� ���Ѵ�.

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
         ids = ids.substring(0, ids.length()-1); // ������ "`"�� �����Ѵ�.
      }catch(StringIndexOutOfBoundsException e){
         return "";
      }
      return ids;
   }
   
   //�α���â(�ʱ�ȭ��)�� ����ڸ���� ������Ʈ
   private void modifyLogonUser(String userIDs) throws IOException {
	     st_buffer.setLength(0);
         st_buffer.append(MDY_LOGONIDS);
         st_buffer.append(SEPARATOR);
         st_buffer.append(userIDs);
         broadcastLogon(st_buffer.toString()); // MDY_USERIDS ��Ŷ�� �����Ѵ�.
   }
   
   //��ȭ���� ��������� ������Ʈ����
   private void modifyRoomUser(String userIDs) throws IOException {
	   st_buffer.setLength(0);
       st_buffer.append(MDY_USERIDS);
       st_buffer.append(SEPARATOR);
       st_buffer.append(userIDs);
       broadcastRoom(st_buffer.toString()); // MDY_USERIDS ��Ŷ�� �����Ѵ�.
 }

   // ��ȭ�濡 ������ ��� �����(��ε��ɽ���)���� �����͸� �����Ѵ�, �α���â�� ��ȭ���� �����Ͽ� ��ε�ĳ���� ���־�� �� 
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


   // �����͸� �����Ѵ�.
   public void send(String sendData) throws IOException{
      synchronized(st_out){
         st_out.writeUTF(sendData);
         st_out.flush();
      }
   }
}   

