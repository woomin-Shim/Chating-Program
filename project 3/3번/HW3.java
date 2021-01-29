import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class HW3 extends Frame implements ActionListener {
	private TextField accountField, nameField, balanceField;
	private Button enter, done, out;
	private RandomAccessFile output;
	private Record data;

	public HW3() {
		super("���Ͼ���");
		data = new Record();
		try {
			output = new RandomAccessFile("customer.txt", "rw");
		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}
		setSize(300, 150);
		setLayout(new GridLayout(4, 3));
		add(new Label("���¹�ȣ"));
		accountField = new TextField();
		add(accountField);
		add(new Label());
		add(new Label("�̸�"));
		nameField = new TextField(20);
		add(nameField);
		add(new Label());
		add(new Label("�ܰ�"));
		balanceField = new TextField(20);
		add(balanceField);
		add(new Label());
		enter = new Button("�Է�");
		enter.addActionListener(this);
		add(enter);
		out = new Button("���");
		out.addActionListener(this);
		add(out);
		done = new Button("����");
		done.addActionListener(this);
		add(done);
		addWindowListener(new WinListener());  // ������ â �ݱ����� �ʿ� 
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
				// �ؽ�Ʈ �ʵ��� ������ ����.
				accountField.setText("");
				nameField.setText("");
				balanceField.setText("");
			} catch (NumberFormatException nfe) {
				System.err.println("���ڸ� �Է��ϼ���");
			} catch (IOException io) {
				System.err.println("���Ͼ��� ����\n" + io.toString());
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
				System.err.println("������� ����\n");
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
				System.err.println("���� �ݱ� ����\n" + io.toString());
			}
			System.exit(0);
		}
	}

	public static void main(String args[]) {
		new HW3();
	}
	
	class WinListener extends WindowAdapter {  //������ â X��ư �ݱ� 
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}

class Record {
	private int account;
	private String name;
	private double balance;

	// RandomAccessFile�� ���� �� ���ڵ� �б�
	public void read(RandomAccessFile file) throws IOException {
		account = file.readInt();
		char namearray[] = new char[15];
		for (int i = 0; i < namearray.length; i++)
			namearray[i] = file.readChar();
		name = new String(namearray);
		balance = file.readDouble();
	}

	// RandomAccessFile�� �� ���ڵ� ����
	public void write(RandomAccessFile file) throws IOException {
		StringBuffer buf;
		file.writeInt(account);
		if (name != null)
			buf = new StringBuffer(name);
		else
			buf = new StringBuffer(15);
		buf.setLength(15); // �̸��� �����ϴ� �޸� ũ�⸦ 15��
		file.writeChars(buf.toString());
		file.writeDouble(balance);
	}

	public void setAccount(int a) {
		account = a;
	} // ���¹�ȣ ����

	public int getAccount() {
		return account;
	} // ���¹�ȣ ��ȯ

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
	} // �� ���ڵ� ����
}
