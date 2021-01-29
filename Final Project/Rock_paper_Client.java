package rock_paper_scissors;

import java.awt.*;
import java.awt.event.*;

public class Rock_paper_Client extends Frame implements ActionListener, KeyListener, MouseListener {
	protected TextField cc_tfLogon; // �α׿� �Է� �ؽ�Ʈ �ʵ�
	private Button cc_btLogon; // �α׿� ���� ��ư
	private Button cc_btSTART; // ���������� �� ���� �� ����
	private Button cc_btLogout; // �α׾ƿ� ��ư
	private boolean isselected; // ���������� ��븦 �����ߴ��� ���ߴ���
	private String counter=null; // ������ ��� �����ϱ� ����

	public TextField cc_tfStatus; // �α׿� ���� �ȳ�
	public TextField cc_tfDate; // �����ð�
	public List cc_lstMember; // �α��� �� ������(�ʱ� ȭ��)
	public List cc_lstPlayer; // ���Ӽ����Ͽ� ���ӿ� ������ �÷��̾��

	public static ClientThread cc_thread;

	public String msg_logon = "";

	public Rock_paper_Client(String str) {
		super(str);
		setLayout(new BorderLayout());
		isselected = false;

		// �α׿�, ��ȭ�� ���� �� ���� ��ư�� �����Ѵ�.
		Panel bt_panel = new Panel();
		bt_panel.setLayout(new FlowLayout());
		cc_btLogon = new Button("�α׿½���");
		cc_btLogon.addActionListener(this);
		bt_panel.add(cc_btLogon);

		cc_tfLogon = new TextField(10);
		bt_panel.add(cc_tfLogon);

		cc_btSTART = new Button("START"); // Ŭ���ϸ� �������ִ� �����鿡�� ���� ���� â �߰�
		cc_btSTART.addActionListener(this);
		bt_panel.add(cc_btSTART);

		cc_btLogout = new Button("�α׾ƿ�");
		cc_btLogout.addActionListener(this);
		bt_panel.add(cc_btLogout);
		add("Center", bt_panel);

		// 4���� Panel ��ü�� ����Ͽ� ��ȭ�� ������ ����Ѵ�.
		Panel roompanel = new Panel(); // 3���� �г��� ���� �гΰ�ü
		roompanel.setLayout(new BorderLayout());

		Panel northpanel = new Panel();
		northpanel.setLayout(new FlowLayout());
		cc_tfStatus = new TextField("�ϴ��� �ؽ�Ʈ �ʵ忡  ID�� �Է��Ͻʽÿ�,", 43);
		// ��ȭ���� �������� �˸�
		cc_tfStatus.setEditable(false);
		northpanel.add(cc_tfStatus);

		Panel centerpanel = new Panel();
		centerpanel.setLayout(new FlowLayout());
		centerpanel.add(new Label("�α��� �ð� : "));
		cc_tfDate = new TextField("������� �α��� �ð�", 30);
		cc_tfDate.setEditable(false);
		centerpanel.add(cc_tfDate);

		Panel southpanel = new Panel();
		southpanel.setLayout(new FlowLayout());

		Panel subpanel1 = new Panel();
		subpanel1.setLayout(new FlowLayout());
		subpanel1.add(new Label("   �α׿� �����         "), BorderLayout.WEST);
		subpanel1.add(new Label("    ���������� ������"), BorderLayout.EAST);
		southpanel.add(subpanel1, BorderLayout.NORTH);

		Panel subpanel = new Panel();
		subpanel.setLayout(new FlowLayout());
		cc_lstMember = new List(10);
		cc_lstPlayer = new List(10);
		subpanel.add(cc_lstMember, BorderLayout.WEST);
		// subpanel.add(new Label("���������� ����"), BorderLayout.NORTH);
		subpanel.add(cc_lstPlayer, BorderLayout.EAST);
		southpanel.add(subpanel, BorderLayout.SOUTH);
		cc_lstMember.addMouseListener(this); // �α����� ������ Ŭ���� �� �ְ�

		roompanel.add("North", northpanel);
		roompanel.add("Center", centerpanel);
		roompanel.add("South", southpanel);
		add("North", roompanel);

		// �α׿� �ؽ�Ʈ �ʵ忡 ��Ŀ���� ���ߴ� �޼ҵ� �߰�

		addWindowListener(new WinListener());
	} 

	class WinListener extends WindowAdapter {
		public void windowClosing(WindowEvent we) {
			cc_thread.requestLogout();
			System.exit(0); // ���߿� �α׾ƿ���ƾ���� ����
		}
	}
	
	public void mouseClicked(MouseEvent e) {  //���콺�� Ŭ���Ǿ��� �� 
		   try {
			   isselected = true;
			   counter = ((List)e.getSource()).getSelectedItem();  //Ŭ���� ��ü�� WID�� ���� 
		   }catch(Exception err) {}
	   }

	// �α׿�, ��ȭ�� ���� �� ���� ��ư ���� �̺�Ʈ�� ó���Ѵ�.
	public void actionPerformed(ActionEvent ae){
      Button b = (Button)ae.getSource();
      if(b.getLabel().equals("�α׿½���")){

         // �α׿� ó�� ��ƾ
         msg_logon = cc_tfLogon.getText(); // �α׿� ID�� �д´�.
         if(!msg_logon.equals("")){
            cc_thread.requestLogon(msg_logon); // ClientThread�� �޼ҵ带 ȣ��
         }else{
            MessageBoxLess msgBox = new  MessageBoxLess(this, "�α׿�", "�α׿� id�� �Է��ϼ���.");
            msgBox.setVisible(true);
         }
      }else if(b.getLabel().equals("START")){

         // ��ȭ�� ���� �� ���� ó�� ��ƾ
         msg_logon = cc_tfLogon.getText(); // �α׿� ID�� �д´�.
         if(!msg_logon.equals("")){
        	 
        
        		if(msg_logon.equals(counter)) {  //�� �ڽ��� Ŭ������ �� 
        			MessageBox msgbox = new MessageBox(this, "����!", "�ڽ��ϰ�� ���� �� �� �����ϴ�!");
          		  	msgbox.setVisible(true);
          		  	return;	  		
        		}
	      		else{
	      			cc_thread.requestAgree(counter); 
	      		}  	      		
	      			counter =null;     	
         }
         
            //cc_thread.requestEnterRoom(msg_logon); // ClientThread�� �޼ҵ带 ȣ��
         else{
            MessageBox msgBox = new MessageBox(this, "�α׿�", "�α׿��� ���� �Ͻʽÿ�.");
            msgBox.setVisible(true);
         }
      }else if(b.getLabel().equals("�α׾ƿ�")){

      // �α׾ƿ� ó�� ��ƾ
    	  cc_thread.requestLogout();
    	  cc_tfLogon.setText("");
      }
      }

	public static void main(String args[]) {
		Rock_paper_Client client = new Rock_paper_Client("��ȭ�� ���� �� ����");
		client.setSize(350, 400);
		client.setVisible(true);

		// ������ �����ϰ� ������ ����� �����带 ȣ���Ѵ�.

		// ������ Ŭ���̾�Ʈ��  �ٸ� �ý������� ����ϴ� ���
		// ���� : java ChatClient [ȣ��Ʈ�̸��� ��Ʈ��ȣ�� �ʿ��ϴ�.]
		// To DO

		// ������ Ŭ���̾�Ʈ�� ���� �ý������� ����ϴ� ���
		// ���� : java ChatClient [ȣ��Ʈ�̸��� ��Ʈ��ȣ�� �ʿ����.]

		cc_thread = new ClientThread(client); // ���� ȣ��Ʈ�� ������
		cc_thread.start(); // Ŭ���̾�Ʈ�� �����带 �����Ѵ�.

	}

	//@Override
	public void mouseClicked1(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//isselected = true;
		//counter = cc_lstMember.getSelectedItem(); // Ŭ���� ��ü�� counter �� ����
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
