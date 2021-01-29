package rock_paper_scissors;

import java.awt.*;
import java.awt.event.*;

public class Rock_paper_Client extends Frame implements ActionListener, KeyListener, MouseListener {
	protected TextField cc_tfLogon; // 로그온 입력 텍스트 필드
	private Button cc_btLogon; // 로그온 실행 버튼
	private Button cc_btSTART; // 가위바위보 방 개설 및 입장
	private Button cc_btLogout; // 로그아웃 버튼
	private boolean isselected; // 가위바위보 상대를 선택했는지 안했는지
	private String counter=null; // 선택한 상대 저장하기 위해

	public TextField cc_tfStatus; // 로그온 개설 안내
	public TextField cc_tfDate; // 개설시각
	public List cc_lstMember; // 로그인 한 유저들(초기 화면)
	public List cc_lstPlayer; // 게임수락하여 게임에 참가한 플레이어들

	public static ClientThread cc_thread;

	public String msg_logon = "";

	public Rock_paper_Client(String str) {
		super(str);
		setLayout(new BorderLayout());
		isselected = false;

		// 로그온, 대화방 개설 및 입장 버튼을 설정한다.
		Panel bt_panel = new Panel();
		bt_panel.setLayout(new FlowLayout());
		cc_btLogon = new Button("로그온실행");
		cc_btLogon.addActionListener(this);
		bt_panel.add(cc_btLogon);

		cc_tfLogon = new TextField(10);
		bt_panel.add(cc_tfLogon);

		cc_btSTART = new Button("START"); // 클릭하면 접속해있는 유저들에게 게임 수락 창 뜨게
		cc_btSTART.addActionListener(this);
		bt_panel.add(cc_btSTART);

		cc_btLogout = new Button("로그아웃");
		cc_btLogout.addActionListener(this);
		bt_panel.add(cc_btLogout);
		add("Center", bt_panel);

		// 4개의 Panel 객체를 사용하여 대화방 정보를 출력한다.
		Panel roompanel = new Panel(); // 3개의 패널을 담을 패널객체
		roompanel.setLayout(new BorderLayout());

		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());
		cc_tfStatus = new TextField("하단의 텍스트 필드에  ID를 입력하십시오,", 43);
		// 대화방의 개설상태 알림
		cc_tfStatus.setEditable(false);
		northpanel.add(cc_tfStatus);

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout());
		centerpanel.add(new Label("로그인 시각 : "));
		cc_tfDate = new TextField("사용자의 로그인 시각", 30);
		cc_tfDate.setEditable(false);
		centerpanel.add(cc_tfDate);

		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout());

		Panel subpanel1 = new Panel();
		subpanel1.setLayout(new FlowLayout());
		subpanel1.add(new Label("   로그온 사용자         "), BorderLayout.WEST);
		subpanel1.add(new Label("    가위바위보 참가자"), BorderLayout.EAST);
		southpanel.add(subpanel1, BorderLayout.NORTH);

		Panel subpanel = new Panel();
		subpanel.setLayout(new FlowLayout());
		cc_lstMember = new List(10);
		cc_lstPlayer = new List(10);
		subpanel.add(cc_lstMember, BorderLayout.WEST);
		// subpanel.add(new Label("가위바위보 유저"), BorderLayout.NORTH);
		subpanel.add(cc_lstPlayer, BorderLayout.EAST);
		southpanel.add(subpanel, BorderLayout.SOUTH);
		cc_lstMember.addMouseListener(this); // 로그인한 유저들 클릭할 수 있게

		roompanel.add("North", northpanel);
		roompanel.add("Center", centerpanel);
		roompanel.add("South", southpanel);
		add("North", roompanel);

		// 로그온 텍스트 필드에 포커스를 맞추는 메소드 추가

		addWindowListener(new WinListener());
	} 

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			cc_thread.requestLogout();
			System.exit(0); // 나중에 로그아웃루틴으로 변경
		}
	}
	
	public void mouseClicked(MouseEvent e) {  //마우스가 클릭되었을 때 
		   try {
			   isselected = true;
			   counter = ((List)e.getSource()).getSelectedItem();  //클릭한 객체를 WID에 저장 
		   }catch(Exception err) {}
	   }

	// 로그온, 대화방 개설 및 입장 버튼 눌림 이벤트를 처리한다.
	public void actionPerformed(ActionEvent ae){
      Button b = (Button)ae.getSource();
      if(b.getLabel().equals("로그온실행")){

         // 로그온 처리 루틴
         msg_logon = cc_tfLogon.getText(); // 로그온 ID를 읽는다.
         if(!msg_logon.equals("")){
            cc_thread.requestLogon(msg_logon); // ClientThread의 메소드를 호출
         }else{
            MessageBoxLess msgBox = new  MessageBoxLess(this, "로그온", "로그온 id를 입력하세요.");
            msgBox.setVisible(true);
         }
      }else if(b.getLabel().equals("START")){

         // 대화방 개설 및 입장 처리 루틴
         msg_logon = cc_tfLogon.getText(); // 로그온 ID를 읽는다.
         if(!msg_logon.equals("")){
        	 
        
        		if(msg_logon.equals(counter)) {  //내 자신을 클릭했을 때 
        			MessageBox msgbox = new MessageBox(this, "오류!", "자신하고는 게임 할 수 없습니다!");
          		  	msgbox.setVisible(true);
          		  	return;	  		
        		}
	      		else{
	      			cc_thread.requestAgree(counter); 
	      		}  	      		
	      			counter =null;     	
         }
         
            //cc_thread.requestEnterRoom(msg_logon); // ClientThread의 메소드를 호출
         else{
            MessageBox msgBox = new MessageBox(this, "로그온", "로그온을 먼저 하십시오.");
            msgBox.setVisible(true);
         }
      }else if(b.getLabel().equals("로그아웃")){

      // 로그아웃 처리 루틴
    	  cc_thread.requestLogout();
    	  cc_tfLogon.setText("");
      }
      }

	public static void main(String args[]) {
		Rock_paper_Client client = new Rock_paper_Client("대화방 개설 및 입장");
		client.setSize(350, 400);
		client.setVisible(true);

		// 소켓을 생성하고 서버와 통신할 스레드를 호출한다.

		// 서버와 클라이언트를  다른 시스템으로 사용하는 경우
		// 실행 : java ChatClient [호스트이름과 포트번호가 필요하다.]
		// To DO

		// 서버와 클라이언트를 같은 시스템으로 사용하는 경우
		// 실행 : java ChatClient [호스트이름과 포트번호가 필요없다.]

		cc_thread = new ClientThread(client); // 로컬 호스트용 생성자
		cc_thread.start(); // 클라이언트의 스레드를 시작한다.

	}

	//@Override
	public void mouseClicked1(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//isselected = true;
		//counter = cc_lstMember.getSelectedItem(); // 클릭한 객체를 counter 에 저장
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
