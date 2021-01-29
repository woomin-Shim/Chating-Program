package rock_paper_scissors;
import java.io.*;
import java.net.*;

public class Rock_paper_Server {
   public static final int cs_port = 2777;
   public static final int cs_maxclient=10;

// Ŭ���̾�Ʈ�κ��� ���ӿ�û�� ��ٸ���, ������ �����Ѵ�.
   public static void main(String args[]){
      try{
         ServerSocket ss_socket = new ServerSocket(cs_port);
         while(true){
            Socket sock = null;
            ServerThread client = null; //Ŭ���̾�Ʈ�� ����� ��������
            try{
               sock = ss_socket.accept(); // Ŭ���̾�Ʈ�� ������ ��ٸ���.
               client = new ServerThread(sock); 
               client.start();
            }catch(IOException e){
               System.out.println(e);
               try{
                  if(sock != null)
                     sock.close();
               }catch(IOException e1){
                  System.out.println(e);
               }finally{
                  sock = null;
               }
            }
         }
      }catch(IOException e){
         // ������ ���������� �������� �ʴ� ��츦 ó���Ѵ�.
      }
   }
}

