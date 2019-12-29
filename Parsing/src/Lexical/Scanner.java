package Lexical;

import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;

//通过扫描工具类过滤注释，空格
public class Scanner {

	private static String inputPath = "in/";
	public String input;
	public int pointer;
	private FileInputStream in;
	
	public Scanner(String filename){
		File sourceFile = new File(Scanner.inputPath+filename);
		ArrayList<Character> trans = new ArrayList<Character>();
		try {
			in = new FileInputStream(sourceFile);
			char ch1 = ' ';
			char ch2 = ' ';//用于在验证是否为引号内结尾或者注释结尾
			while(in.available()>0){
				if(ch2 != ' '){
					ch1 = ch2;
				} else {
					ch1 = (char) in.read();
				}
				if(ch1 == '\''){//避免删除空白时将‘’包含的空白字符剔除
					trans.add(ch1);
					trans.add((char)in.read());
					trans.add((char)in.read());
				} else if (ch1 == '\"'){//避免将字符串中的空白剔除
					trans.add(ch1);
					while(in.available()>0){
						ch1 = (char)in.read();
						trans.add(ch1);
						if(ch1 == '\"'){
							break;
						}
					}
				} else if (ch1 == '/'){//剔除字符串
					ch2 = (char)in.read();
					if(ch2 == '/'){
						while(in.available() > 0){
							ch2 = (char)in.read();
							if(ch2 == '\n'){
								break;
							}
						}
						ch2 = ' ';
					} else if (ch2 == '*') {
						while(in.available() > 0){
							ch1 = (char)in.read();
							if(ch1 == '*'){
								ch2 = (char)in.read();
								if(ch2 == '/'){
									break;
								}
							}
						}
					} else {
						if(ch2 == ' '){
							while(ch2 == ' '){
								ch2 = (char)in.read();
							}
						}
						trans.add(ch1);
						trans.add(ch2);
						ch2 = ' ';
					}
				} else if(ch1 == ' '){
					if(trans.get(trans.size()-1) == ' '){
						continue;
					} 
				} else {
					if(!((int)ch1 == 13 ||(int)ch1 == 10 ||(int)ch1 == 32)){//去除换行
						trans.add(ch1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		char[] chStr = new char[trans.size()];
		for(int i = 0;i < trans.size();i++){
			chStr[i] = trans.get(i);
		}
		String result = new String(chStr);
		this.input = result;
		this.pointer = 0;
	}
	
	public char getNextChar(){
		if(pointer==input.length()){
			return (char)0;
		} else {
			return input.charAt(pointer++);
		}
	}
	
	//回退n个字符
	public void retract(int n){
		for(int i = 0;i < n;i++){
			pointer--;
		}
	}
	
	public int getIndex(){
		return pointer;
	}
	
	public int getLength(){
		return this.input.length();
	}
	
	public String getSubStr(int index,int length){
		if((index+length-1)>=this.input.length()){
			return null;
		} else {
			String result = this.input.substring(index,index+length);
			return result;
		}
	}
	
	public String getTestString(int index){
		int temp = index;
		int len = 1;
		while(isLetterOrDigit(input.charAt(temp))&&(temp<=(input.length()-1))){
			temp++;
			len++;
		}
		String result = input.substring(index-1,index-1+len);
		return result;
	}
	
	private boolean isLetterOrDigit(char c){
		return (c=='_'||(c>='a'&&c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9'));	
	}
	
	public String getLeftStr(int index){
		if(index == input.length()-1){
			return null;
		} else {
			return input.substring(index);
		}
	}
	
	public void move(int n){
		for(int i = 0;i < n;i++){
			pointer++;
		}
	}
	
	public String getStringInQuotation(int index){
		int temp = index;
		while(input.charAt(temp-1)!='\"'){
			temp--;
		}
		StringBuilder sb = new StringBuilder();
		while(input.charAt(temp) != '\"'){
			sb.append(input.charAt(temp));
			temp++;
		}
		return sb.toString();
	}
	
}
