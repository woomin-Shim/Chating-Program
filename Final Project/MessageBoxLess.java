package rock_paper_scissors;
import java.awt.*;
import java.awt.event.*;

class MessageBoxLess extends Dialog implements ActionListener{
	   Frame client;
	   public MessageBoxLess(Frame parent, String title, String message){
	      super(parent, title, false); // modeless Dialog 생성  , 박스를 누르지 않아도 조작이 가능 
	      /* modal dialog는 반드시 이 modal을 제거해야지 다른 작업 가능,
	       * modeless는 이 박스가 떳다하더라도 조작이 가능 
	       */
	      setLayout(new BorderLayout());
	      Label lbl = new Label(message);
	      add("Center", lbl);
	      Button bt = new Button("OK");
	      bt.addActionListener(this);
	      add("South", bt);
	      pack();
	      client = parent;
	   }
	   public void actionPerformed(ActionEvent ae){
	      client.dispose();
	   }
	}

