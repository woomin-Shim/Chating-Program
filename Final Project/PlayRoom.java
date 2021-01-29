package rock_paper_scissors;
import java.awt.*;
import java.awt.event.*;
import rock_paper_scissors.ClientThread;

public class PlayRoom extends Frame implements ActionListener, KeyListener, MouseListener {
	
	private Button enter; //가위바위보 입력 버튼
	private Button dr_btClear;
	private Button dr_btLogout;
	private Button rock, scissors, paper;
	private Panel southpanel;
	
	public TextArea dr_taContents;
	public TextField input; //가위바위보 입력 
	public String IdTo;
	public static ClientThread dr_thread;
	
	 public PlayRoom(ClientThread client, String title, String id, String counter){
		 super(title);
		 
		 setLayout(new BorderLayout());
		 
		 dr_thread = client;
		 Panel northpanel = new Panel();
	     northpanel.setLayout(new FlowLayout());
	     dr_btClear = new Button("재경기"); 
	     dr_btClear.addActionListener(this);
	     northpanel.add(dr_btClear);
	   
	     dr_btLogout = new Button("EXIT");
	     dr_btLogout.addActionListener(this);
	     northpanel.add(dr_btLogout);
		 
		 Panel centerpanel = new Panel();
	     centerpanel.setLayout(new FlowLayout());
	     Label roomname = new Label(id + "   VS   "  + counter);
	     centerpanel.add(roomname);
	     dr_taContents = new TextArea(3, 30);
	     dr_taContents.setEditable(false);
	     dr_taContents.setText("가위 바위 보 중 하나를 클릭하세요!");
	     centerpanel.add(dr_taContents);
	     
	     Panel southpanel = new Panel();
	     southpanel.setLayout(new FlowLayout());
	     Label me = new Label(dr_thread.MyId);
	     southpanel.add(me);
	  
		 scissors = new Button("가위");
		 southpanel.add(scissors,BorderLayout.EAST);
		 scissors.addActionListener(this);
			
		 paper = new Button("바위");
		 southpanel.add(paper,BorderLayout.CENTER);
		 paper.addActionListener(this);

			
		 rock = new Button("보");
		 southpanel.add(rock,BorderLayout.WEST);
		 rock.addActionListener(this);
	     
	     add("North", northpanel);
	     add("Center", centerpanel);
	     add("South", southpanel);
	     
	     IdTo = id;

	 }
	
	 public void actionPerformed(ActionEvent ae){
	      Button b = (Button)ae.getSource();
	      if(b.getLabel().equals("재경기")){
	    	  dr_taContents.setText("");
	      }else if(b.getLabel().equals("EXIT")){
	    	  dr_thread.requestQuitRoom();
	      }
	 
	      		else if(b.getLabel().equals("가위")) {
					 dr_thread.showResult(IdTo, "1");
					 //southpanel.setVisible(false);
				 }
				 else if(b.getLabel().equals("바위")) {
					 dr_thread.showResult(IdTo, "2");
				 }
				 else if(b.getLabel().equals("보")) {
					 dr_thread.showResult(IdTo, "3");
				 }
				 //else {
					 //dr_taContents.setText("다시 입력하세요!");
					 //return;
				 //}
	      
	 }
	 
	 class WinListener extends WindowAdapter
	   {
	      public void windowClosing(WindowEvent we){
	    	 dr_thread.requestLogout();
	         //System.exit(0); // 로그아웃 루틴으로 바꾼다.
	         //dr_thread.release();
	      }
	   }
	 
	 
	/*public void KeyPressed(KeyEvent ke) {
		 if(ke.getKeyChar() == KeyEvent.VK_ENTER) {
			 String words = input.getText();
			 
			 if(words.equals("가위")==true) {
				 dr_thread.showResult(IdTo, "가위");
			 }
			 else if(words.equals("바위")==true) {
				 dr_thread.showResult(IdTo, "바위");
			 }
			 else if(words.equals("보")==true) {
				 dr_thread.showResult(IdTo, "보");
			 }
			 else {
				 dr_taContents.setText("다시 입력하세요!");
				 //return;
			 }
		 }
	 }*/
	
	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
