package Lexical;

public class Token {
	
	public int type;//类型
	public String value;//种别码
	
	public Token(int type,String value){
		this.type = type;
		this.value = value;
	}
	
	@Override
	public String toString(){
		return "("+this.type+","+this.value+")";
	}
}
