package chapter4;
import java.io.*;
import java.util.Scanner;

public class HW4 {
	public static void main(String args[]) throws IOException{
		Scanner scan = new Scanner(System.in);
		String text = scan.nextLine();
		FileWriter fw = new FileWriter("HomeWork4.txt");
		fw.write(text, 0, text.length());
		fw.flush();
		fw.close();
		
		int numRead;
		String data;
		char[] buffer = new char[40];
		FileReader fr = new FileReader("HomeWork4.txt");
		while((numRead = fr.read(buffer)) > -1) {
			System.out.println(buffer);
		}
		fr.close();
		}
	}

