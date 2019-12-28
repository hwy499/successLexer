package parser;

import java.util.ArrayList;



//LR1项目集簇
public class LR1ProjectCluster {
	
	public ArrayList<LR1ProjectSet> lr1ProjectCluster = new ArrayList<LR1ProjectSet>();
	
	public LR1ProjectSet getLR1ProjectSet(int i){
		return lr1ProjectCluster.get(i);
	}
	
	public int size(){
		return lr1ProjectCluster.size();
	}
	
	public boolean contains(LR1ProjectSet state){
		return lr1ProjectCluster.contains(state);
	}
	
	public String getAllStatesToString(){
		int size = lr1ProjectCluster.size();
		String str = "";
		for(int i = 0;i < size;i++){
			System.out.println("I"+i+":");
			str += "I"+i+":\n"+lr1ProjectCluster.get(i).print();
		}
		return str;
	}
}
