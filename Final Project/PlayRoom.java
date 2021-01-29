package rock_paper_scissors;
import java.awt.*;
import java.awt.event.*;
import rock_paper_scissors.ClientThread;

public class PlayRoom extends Frame implements ActionListener, KeyListener, MouseListener {
	
	private Button enter; //���������� �Է� ��ư
	private Button dr_btClear;
	private Button dr_btLogout;
	private Button rock, scissors, paper;
	private Panel southpanel;
	
	public TextArea dr_taContents;
	public TextField input; //���������� �Է� 
	public String IdTo;
	public static ClientThread dr_thread;
	
	 public PlayRoom(ClientThread client, String title, String id, String counter){
		 super(title);
		 
		 setLayout(new BorderLayout());
		 
		 dr_thread = client;
		 Panel northpanel = new Panel();
	     northpanel.setLayout(new FlowLayout());
	     dr_btClear = new Button("����"); 
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
	     dr_taContents.setText("���� ���� �� �� �ϳ��� Ŭ���ϼ���!");
	     centerpanel.add(dr_taContents);
	     
	     Panel southpanel = new Panel();
	     southpanel.setLayout(new FlowLayout());
	     Label me = new Label(dr_thread.MyId);
	     southpanel.add(me);
	  
		 scissors = new Button("����");
		 southpanel.add(scissors,BorderLayout.EAST);
		 scissors.addActionListener(this);
			
		 paper = new Button("����");
		 southpanel.add(paper,BorderLayout.CENTER);
		 paper.addActionListener(this);

			
		 rock = new Button("��");
		 southpanel.add(rock,BorderLayout.WEST);
		 rock.addActionListener(this);
	     
	     add("North", northpanel);
	     add("Center", centerpanel);
	     add("South", southpanel);
	     
	     IdTo = id;

	 }
	
	 public void actionPerformed(ActionEvent ae){
	      Button b = (Button)ae.getSource();
	      if(b.getLabel().equals("����")){
	    	  dr_taContents.setText("");
	      }else if(b.getLabel().equals("EXIT")){
	    	  dr_thread.requestQuitRoom();
	      }
	 
	      		else if(b.getLabel().equals("����")) {
					 dr_thread.showResult(IdTo, "1");
					 //southpanel.setVisible(false);
				 }
				 else if(b.getLabel().equals("����")) {
					 dr_thread.showResult(IdTo, "2");
				 }
				 else if(b.getLabel().equals("��")) {
					 dr_thread.showResult(IdTo, "3");
				 }
				 //else {
					 //dr_taContents.setText("�ٽ� �Է��ϼ���!");
					 //return;
				 //}
	      
	 }
	 
	 class WinListener extends WindowAdapter
	   {
	      public void windowClosing(WindowEvent we){
	    	 dr_thread.requestLogout();
	         //System.exit(0); // �α׾ƿ� ��ƾ���� �ٲ۴�.
	         //dr_thread.release();
	      }
	   }
	 
	 
	/*public void KeyPressed(KeyEvent ke) {
		 if(ke.getKeyChar() == KeyEvent.VK_ENTER) {
			 String words = input.getText();
			 
			 if(words.equals("����")==true) {
				 dr_thread.showResult(IdTo, "����");
			 }
			 else if(words.equals("����")==true) {
				 dr_thread.showResult(IdTo, "����");
			 }
			 else if(words.equals("��")==true) {
				 dr_thread.showResult(IdTo, "��");
			 }
			 else {
				 dr_taContents.setText("�ٽ� �Է��ϼ���!");
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
