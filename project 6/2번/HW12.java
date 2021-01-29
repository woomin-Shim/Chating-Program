package com.java.Chapter12;

import java.io.*;
import java.net.*;
import java.util.*;

public class HW12 {
	public static void main(String args[]) {
		URL u;
		URLConnection uc;
		InputStream is;
		String line;
		if (args.length == 0) {
			System.out.println("URL�� �Է��ϼ���!");
			return;
		}
		for (int i = 0; i < args.length; i++) {
			try {
				u = new URL(args[i]); // URL ��ü�� �����Ѵ�.
				uc = u.openConnection(); // uc URLConnection ��ü ����
				int cl = uc.getContentLength();
				StringBuffer buffer = new StringBuffer();

				System.out.println("������ ���� : " + uc.getContentType());
				System.out.println("������ ���ڵ� : " + uc.getContentEncoding());
				System.out.println("���� ���� ��¥ : " + new Date(uc.getDate()));
				System.out.println("���� ���� ��¥ : " + new Date(uc.getLastModified()));
				System.out.println("���� ���� ��¥ : " + new Date(uc.getExpiration()));
				System.out.println("���� ���� : " + uc.getContentLength());

		
							
				is = uc.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
					buffer.append(line).append('\n');
					
				}
				String filename = u.getFile(); //URL���� �����̸��� �д´�.
				filename = filename.substring(filename.lastIndexOf('/')+1);
				FileOutputStream fout = new FileOutputStream(filename);
				
				
			} catch (MalformedURLException e) {
				System.out.println("�Էµ� URL�� �߸��� URL�Դϴ�.");
			} catch (IOException e) {
				System.out.println(e);
			}
		}

	}
}
