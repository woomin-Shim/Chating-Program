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
			System.out.println("URL을 입력하세요!");
			return;
		}
		for (int i = 0; i < args.length; i++) {
			try {
				u = new URL(args[i]); // URL 객체를 생성한다.
				uc = u.openConnection(); // uc URLConnection 객체 생성
				int cl = uc.getContentLength();
				StringBuffer buffer = new StringBuffer();

				System.out.println("컨텐츠 유형 : " + uc.getContentType());
				System.out.println("컨텐츠 인코딩 : " + uc.getContentEncoding());
				System.out.println("문서 전송 날짜 : " + new Date(uc.getDate()));
				System.out.println("최종 수정 날짜 : " + new Date(uc.getLastModified()));
				System.out.println("문서 만기 날짜 : " + new Date(uc.getExpiration()));
				System.out.println("문서 길이 : " + uc.getContentLength());

		
							
				is = uc.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
					buffer.append(line).append('\n');
					
				}
				String filename = u.getFile(); //URL에서 파일이름을 읽는다.
				filename = filename.substring(filename.lastIndexOf('/')+1);
				FileOutputStream fout = new FileOutputStream(filename);
				
				
			} catch (MalformedURLException e) {
				System.out.println("입력된 URL은 잘못된 URL입니다.");
			} catch (IOException e) {
				System.out.println(e);
			}
		}

	}
}
