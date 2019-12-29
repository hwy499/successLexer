package Lexical;

import java.util.ArrayList;

public class lexicalAnalysis {
	
	//扫描类
	private Scanner scan;
	
	public lexicalAnalysis(String filename){
		this.scan = new Scanner(filename);
	}
	
	//通过语法分析获得Token序列
	public ArrayList<Token> getTokenList(){
		ArrayList<Token> result = new ArrayList<Token>();
		int index = 0;
		while(index < scan.getLength()){
			Token token = analyze(index);
			result.add(token);
			index = scan.getIndex();
		}
		this.scan.retract(scan.getLength()-1);
		return result;
	}
	
	private String[] keyword ={
		"auto","double","int","struct","break","else","long","switch",
		"case","enum","register","typedef","char","return","union","const",
		"extern","float","short","unsigned","continue","for","signed","void",
		"default","goto","sizeof","volatile","do","if","static","while"
	};


	
	private boolean flag = false;       
	//获得单个单词
	public Token analyze(int index){
		int length = scan.getLength();
		int type = -1;
		String value = "";
		while(index < length){
			char ch = scan.getNextChar();
			index++;
			if(isDigit(ch)){
				if(WordType.isCalc(type)){
					scan.retract(1);
					break;
				}
				if(value == ""){
					value = new Character(ch).toString();
					type = WordType.NUM;
				} else {
					value += new Character(ch).toString();
				}
				
			} else if (isLetter(ch)){
				if(WordType.isCalc(type)){
					scan.retract(1);
					break;
				}
				if(flag){
					value = scan.getStringInQuotation(index);
					type = WordType.ID;
					scan.move(value.length()-1);
					return new Token(type,value);
				}
				if(type == WordType.ID){
					value += new Character(ch).toString();
					continue;
				}
				String str = scan.getTestString(index);
				String val = null;
				if(str.startsWith("include")){
					val = "include";
					type = WordType.INCLUDE;
				} else {
					for(int i = 0;i < keyword.length;i++){
						if(str.startsWith(keyword[i])){
							val = keyword[i];
							type = i;
							break;
						}
					}
				}
				if(val == null){
					type = WordType.ID;
					if(value == ""){
						value = new Character(ch).toString();
					} else {
						value += new Character(ch).toString();
					}
				} else {
					value = val;
					scan.move(value.length()-1);
					return new Token(type,value);
				}
				
			} else {
				if(type == WordType.NUM || type == WordType.ID){
					scan.retract(1);
					return new Token(type,value);
				}
				switch(ch){
				case '='://==,=
					if(type == -1){
						type = WordType.ASSIGN;
						value = "=";
					} else if(type == WordType.LT){//<=
						type = WordType.LE;
						value = "<=";
						return new Token(type,value);
					} else if(type == WordType.GT){//>=
						type = WordType.GE;
						value = ">=";
						return new Token(type,value);
					} else if(type == WordType.ASSIGN){//==
						type = WordType.EQUAL;
						value = "==";
						return new Token(type,value);
					} else if(type == WordType.NOT){//!=
						type = WordType.NE;
						value = "!=";
						return new Token(type,value);
					} else if(type == WordType.ADD){//+=
						type = WordType.INCREASEBY;
						value = "+=";
						return new Token(type,value);
					} else if(type == WordType.SUB){//-=
						type = WordType.DECREASEBY;
						value = "-=";
						return new Token(type,value);
					} else if(type == WordType.DIV){///=
						type = WordType.DIVBY;
						value = "/=";
						return new Token(type,value);
					} else if(type == WordType.MUL){//*=
						type = WordType.MULBY;
						value = "*=";
						return new Token(type,value);
					}
					break;
				case '+':
					if(type == -1){
						type = WordType.ADD;
						value = "+";
					} else if(type == WordType.ADD){//++
						type = WordType.INCREASE;
						value = "++";
						return new Token(type,value);
					} 
					break;
				case '-':
					if(type == -1){
						type = WordType.SUB;
						value = "-";
					} else if(type == WordType.SUB){//--
						type = WordType.DECREASEBY;
						value = "--";
						return new Token(type,value);
					}
					break;
				case '*':
					if(type == -1){
						type = WordType.MUL;
						value = "*";
					} 
					break;
				case '/':
					if(type == -1){
						type = WordType.DIV;
						value = "/";
					}
					break;
				case '<':
					if(type == -1){
						type = WordType.LT;
						value = "<";
					}
					break;
				case '>':
					if(type == -1){
						type = WordType.GT;
						value = ">";
					}
					break;
				case '!':
					if(type == -1){
						type = WordType.NOT;
						value = "!";
					}
					break;
				case '|':
					if(type == -1){
						type = WordType.OR_1;
						value = "|";
					} else if(type == WordType.OR_1){
						type = WordType.OR_2;
						value = "||";
						return new Token(type,value);
					}
					break;
				case '&':
					if(type == -1){
						type = WordType.AND_1;
						value = "&";
					} else if(type == WordType.AND_1){
						type = WordType.AND_2;
						value = "&&";
						return new Token(type,value);
					}
					break;
				case ';':
					if(type == -1){
						type = WordType.SEMICOLON;
						value = ";";
					}
					break;
				case '{':
					if(type == -1){
						type = WordType.BRACE_L;
						value = "{";
					} else if(WordType.isCalc(type)){
						scan.retract(1);
						return new Token(type,value);
					}
					break;
				case '}':
					if(type == -1){
						type = WordType.BRACE_R;
						value = "}";
					} else if(WordType.isCalc(type)){
						scan.retract(1);
						return new Token(type,value);
					}
					break;
				case '[':
					if(type == -1){
						type = WordType.BRACKET_L;
						value = "[";
					} else if(WordType.isCalc(type)){
						scan.retract(1);
						return new Token(type,value);
					}
					break;
				case ']':
					if(type == -1){
						type = WordType.BRACKET_R;
						value = "]";
					} else if(WordType.isCalc(type)){
						scan.retract(1);
						return new Token(type,value);
					}
					break;
				case '(':
					if(type == -1){
						type = WordType.PARENTHESIS_L;
						value = "(";
					} else if(WordType.isCalc(type)){
						scan.retract(1);
						return new Token(type,value);
					} 
					break;
				case ')':
					if(type == -1){
						type = WordType.PARENTHESIS_R;
						value = ")";
					} else if(WordType.isCalc(type)){
						scan.retract(1);
						return new Token(type,value);
					}
					break;
				case '#':
					if(type == -1){
						type = WordType.POUND;
						value = "#";
					}
					break;
				case ',':
					if(type == -1){
						type = WordType.COMMA;
						value = ",";
					}
					break;
				case '\'':
					if(type == -1){
						type = WordType.SINGLE_QUOTAOTION;
						value = "\'";
					}
					break;
				case '"':
					if(flag == false){
						flag = true;//表明这是配对的双引号中的第一个
					} else {
						flag = false;
					}
					if(type == -1){
						type = WordType.DOUBLE_QUOTATION;
						value = "\"";
					}
					break;
				default:
					break;
				}
				if(!WordType.isCalc(type)){
					break;
				}
			}
		}
		if(value.length()>1){
			scan.move(value.length()-1);
		}
		Token token = new Token(type,value);
		return token;
	}
	
	private boolean isDigit(char c){
		return ((c<='9'&&c>='0')||c=='.');
	}
	
	private boolean isLetter(char c){
		return ((c>='a'&&c<='z')||c=='_'||(c>='A'&&c<='Z'));
	}
}
