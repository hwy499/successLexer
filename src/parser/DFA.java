package parser;

import java.util.ArrayList;

public class DFA {
	
	public ArrayList<DFAState> states = new ArrayList<DFAState>();
	
	public DFAState get(int i){
		return states.get(i);
	}
	
	public int size(){
		return states.size();
	}
	
	public int contains(DFAState state){
		for(int i = 0;i <states.size();i++){
			if(states.get(i).equals(state)){
				return i;
			}
		}
		return -1;
	}
	
	public String printAllStates(){
		int size = states.size();
		String str = "";
		for(int i = 0;i < size;i++){
			System.out.println("I"+i+":");
			str += "I"+i+":\n"+states.get(i).print();
		}
		return str;
	}
	
	public DFA(){
		
	}

}
