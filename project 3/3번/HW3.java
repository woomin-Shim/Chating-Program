import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class HW3 extends Frame implements ActionListener {
	private TextField accountField, nameField, balanceField;
	private Button enter, done, out;
	private RandomAccessFile output;
	private Record data;

	public HW3() {
		super("파일쓰기");
		data = new Record();
		try {
			output = new RandomAccessFile("customer.txt", "rw");
		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}
		setSize(300, 150);
		setLayout(new GridLayout(4, 3));
		add(new Label("구좌번호"));
		accountField = new TextField();
		add(accountField);
		add(new Label());
		add(new Label("이름"));
		nameField = new TextField(20);
		add(nameField);
		add(new Label());
		add(new Label("잔고"));
		balanceField = new TextField(20);
		add(balanceField);
		add(new Label());
		enter = new Button("입력");
		enter.addActionListener(this);
		add(enter);
		out = new Button("출력");
		out.addActionListener(this);
		add(out);
		done = new Button("종료");
		done.addActionListener(this);
		add(done);
		addWindowListener(new WinListener());  // 윈도우 창 닫기위해 필요 
		setVisible(true);
	}

	public void addRecord() {
		int accountNo = 0;
		Double d;
		if (!accountField.getText().equals("")) {
			try {
				accountNo = Integer.parseInt(accountField.getText());
				if (accountNo > 0 && accountNo <= 100) {
					data.setAccount(accountNo);
					data.setName(nameField.getText());
					d = new Double(balanceField.getText());
					data.setBalance(d.doubleValue());
					output.seek((long) (accountNo - 1) * Record.size());
					data.write(output);
				}
				// 텍스트 필드의 내용을 지움.
				accountField.setText("");
				nameField.setText("");
				balanceField.setText("");
			} catch (NumberFormatException nfe) {
				System.err.println("숫자를 입력하세요");
			} catch (IOException io) {
				System.err.println("파일쓰기 에러\n" + io.toString());
				System.exit(1);
			}
		}
	}

	public void getRecord() {
		int accountNo = 0;
		if (!accountField.getText().equals("")) {
			try {
				accountNo = Integer.parseInt(accountField.getText());
				if (accountNo > 0 && accountNo <= 100) {
					output.seek((long)(accountNo-1) * Record.size());
					data.read(output);
					accountField.setText("" + data.getAccount());
					nameField.setText("" + data.getName());
					balanceField.setText("" + data.getBalance());
					}
			} catch (IOException e) {
				System.err.println("파일출력 에러\n");
			}

		}

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == enter) {
			addRecord();
		} else if (e.getSource() == out) {
			getRecord();
		} else if (e.getSource() == done) {
			try {
				output.close();
			} catch (IOException io) {
				System.err.println("파일 닫기 에러\n" + io.toString());
			}
			System.exit(0);
		}
	}

	public static void main(String args[]) {
		new HW3();
	}
	
	class WinListener extends WindowAdapter {  //윈도우 창 X버튼 닫기 
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}

class Record {
	private int account;
	private String name;
	private double balance;

	// RandomAccessFile로 부터 한 레코드 읽기
	public void read(RandomAccessFile file) throws IOException {
		account = file.readInt();
		char namearray[] = new char[15];
		for (int i = 0; i < namearray.length; i++)
			namearray[i] = file.readChar();
		name = new String(namearray);
		balance = file.readDouble();
	}

	// RandomAccessFile에 한 레코드 저장
	public void write(RandomAccessFile file) throws IOException {
		StringBuffer buf;
		file.writeInt(account);
		if (name != null)
			buf = new StringBuffer(name);
		else
			buf = new StringBuffer(15);
		buf.setLength(15); // 이름을 저장하는 메모리 크기를 15로
		file.writeChars(buf.toString());
		file.writeDouble(balance);
	}

	public void setAccount(int a) {
		account = a;
	} // 구좌번호 설정

	public int getAccount() {
		return account;
	} // 구좌번호 반환

	public void setName(String f) {
		name = f;
	}

	public String getName() {
		return name;
	}

	public void setBalance(double b) {
		balance = b;
	}

	public double getBalance() {
		return balance;
	}

	public static int size() {
		return 42;
	} // 한 레코드 길이
}
