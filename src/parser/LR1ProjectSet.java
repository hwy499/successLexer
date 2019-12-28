package parser;

import java.util.ArrayList;
import java.util.Iterator;

public class LR1ProjectSet {
	
	public int id ;
	public ArrayList<LR1Project> set = new ArrayList<LR1Project>();
	
	public LR1ProjectSet(int id){
		this.id = id;
	}
	
	public boolean addNewDerivation(LR1Project d){
		if(contains(d)){
			return false;
		} else {
			set.add(d);
			return true;
		}
	}
	
	public String print(){
		String str = "";
		Iterator<LR1Project> iter = set.iterator();
		while(iter.hasNext()){
			str += iter.next().print();
		}
		return str;
	}
	
	public boolean contains(LR1Project lrd){
		for(LR1Project l:set){
			if(l.equalTo(lrd)){
				return true;
			}
		}
		return false;
	}
	
	public boolean equalTo(LR1ProjectSet state){
		if(this.toString().hashCode()==state.toString().hashCode()){
			return true;
		} else {
			return false;
		}
	}
	
	public String toString(){
		String result = "";
		for(int i = 0;i < set.size();i++){
			result += set.get(i);
			if(i < set.size()-1){
				result += "|";
			}
		}
		return result;
	}
	

    public ArrayList<String> getGotoPath(){
		
		ArrayList<String> result = new ArrayList<String>();
		for(LR1Project lrd:set){
			if(lrd.production.list.size()==lrd.position){
				continue;
			}
			String s = lrd.production.list.get(lrd.position);
			if(!result.contains(s)){
				result.add(s);
			}
		}
		return result;
	}
	
	public ArrayList<LR1Project> getLRDs(String s){
		ArrayList<LR1Project> result = new ArrayList<LR1Project>();
		for(LR1Project lrd:set){
			if(lrd.production.list.size() != lrd.position){
				String s1 = lrd.production.list.get(lrd.position);
				if(s1.equals(s)){
					result.add((LR1Project)lrd.clone());
				}
			}
		}
		return result;
	}

}
