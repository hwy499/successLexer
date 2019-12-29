package parser;

import java.util.ArrayList;
import java.util.Iterator;


//构建分析表
public class ForecastAnalysisTable {
	
	public static String error = "X";  //错误标记
	public static String acc = "acc";  //acc标记
	
	public LR1ProjectCluster lr1ProjectCluster;   //dfa
	
	private String[] actionCol; //action
	private String[] gotoCol;  //goto
	public int actionLength;  
	public int gotoLength;
	public int stateNum; 
	
	private int[][] gotoTable;
	private String[][] actionTable;
	
	public ForecastAnalysisTable(){
		createTableHeader();//建表
		this.actionLength = actionCol.length;
		this.gotoLength = gotoCol.length;
		createDFA();//创建DFA
		this.stateNum = lr1ProjectCluster.size();
		this.gotoTable = new int[stateNum][gotoLength+actionLength-1];
		this.actionTable = new String[stateNum][actionLength];
		createAnalyzeTable();//填充语法分析表的相关内容
	}
	
	/**
	 * 利用这个方法建立一个LR(1)语法分析表的表头
	 */
	private void createTableHeader(){
		//初始化action的列宽    加1为的是终结符$
		this.actionCol = new String[Grammar.VT.size()+1];
		//初始化goto的列宽
		this.gotoCol = new String[Grammar.VN.size()+Grammar.VT.size()];
		
		Iterator<String> iter1 = Grammar.VT.iterator();
		Iterator<String> iter2 = Grammar.VN.iterator();
		int i = 0;
		//初始化action和goto表头
		while(iter1.hasNext()){
			String vt = iter1.next();
			if(!vt.equals(Grammar.emp)){
				actionCol[i] = vt;
				gotoCol[i] = vt;
				i++;
			}
		}
		//追加一个$
		actionCol[i] = "$";
		//初始化goto表头
		while(iter2.hasNext()){
			String vn = iter2.next();
			gotoCol[i] = vn;
			i++;
		}
	}
	
	//private ArrayList<DFAState> stateList = new ArrayList<DFAState>();//用于下列递归方法的一个公共的容器
	private ArrayList<Integer> gotoStart = new ArrayList<Integer>();
	private ArrayList<Integer> gotoEnd = new ArrayList<Integer>();
	private ArrayList<String> gotoPath = new ArrayList<String>();
	
	
	
	//利用这个递归方法建立一个用于语法分析的LR1项目集簇
	private void createDFA(){
		
		this.lr1ProjectCluster = new LR1ProjectCluster();
		LR1ProjectSet state0 = new LR1ProjectSet(0);
		state0.addNewDerivation(new LR1Project(getDerivation("S'").get(0),"$",0));
		for(int i = 0;i < state0.set.size();i++){
			
			LR1Project lrd = state0.set.get(i);			
			//判断当前产生式是否可以进行  
			if(lrd.position < lrd.production.list.size()){
				String A = lrd.production.list.get(lrd.position);//获取·后面的文法符号
				String b = null;//紧跟A的一项+a
				if(lrd.position==lrd.production.list.size()-1){
					b = lrd.terminator;
				} else {
					b = lrd.production.list.get(lrd.position+1);
				}
				//判断A是否为非终结符，是则展开，否则结束
				if(Grammar.VN.contains(A)){
					ArrayList<String> firstB = first(b);
					ArrayList<Production> dA = getDerivation(A);
					for(int j=0,length1=dA.size();j<length1;j++){
						for(int k=0,length2=firstB.size();k<length2;k++){
							LR1Project lrd1 = new LR1Project(dA.get(j),firstB.get(k),0);
							state0.addNewDerivation(lrd1);
						}
					}
				}
			}
		}
	
		lr1ProjectCluster.lr1ProjectCluster.add(state0);
		//state0建立成功后开始递归建立其他的状态
		ArrayList<String> gotoPath = state0.getGotoPath();
		for(String path:gotoPath){
			ArrayList<LR1Project> list = state0.getLRDs(path);//直接通过路径传到下一个状态的情况
			addState(0,path,list);  //开始进行递归，建立用于分析的DFA
		}
	}
	
	//递归查询
	private void addState(int lastState,String path,ArrayList<LR1Project> list){
		
		LR1ProjectSet lr1ProjectSet = new LR1ProjectSet(0);
		for(int i = 0;i < list.size();i++){
			list.get(i).position++;
			lr1ProjectSet.addNewDerivation(list.get(i));
		}
		
		for(int i = 0;i < lr1ProjectSet.set.size();i++){
			if(lr1ProjectSet.set.get(i).production.list.size() != lr1ProjectSet.set.get(i).position){
				String A = lr1ProjectSet.set.get(i).production.list.get(lr1ProjectSet.set.get(i).position);
				String B = null;
				if(lr1ProjectSet.set.get(i).position+1 == lr1ProjectSet.set.get(i).production.list.size()){
					B = lr1ProjectSet.set.get(i).terminator;
				} else {
					B = lr1ProjectSet.set.get(i).production.list.get(lr1ProjectSet.set.get(i).position+1);
				}
				ArrayList<Production> dA = getDerivation(A);
				ArrayList<String> firstB = first(B);
				for(int j = 0;j < dA.size();j++){
					for(int k = 0;k < firstB.size();k++){
						LR1Project lrd = new LR1Project(dA.get(j),firstB.get(k),0);
						if(!lr1ProjectSet.contains(lrd)){
							lr1ProjectSet.addNewDerivation(lrd);
						}
					}
				}
			}
		}
		
		for(int i = 0;i < lr1ProjectCluster.lr1ProjectCluster.size();i++){
			if(lr1ProjectCluster.lr1ProjectCluster.get(i).equalTo(lr1ProjectSet)){
				gotoStart.add(lastState);
				gotoEnd.add(i);
				gotoPath.add(path);
				return;
			}
		}
		
		lr1ProjectSet.id = lr1ProjectCluster.lr1ProjectCluster.size();
		lr1ProjectCluster.lr1ProjectCluster.add(lr1ProjectSet);
		gotoStart.add(lastState);
		gotoEnd.add(lr1ProjectSet.id);
		gotoPath.add(path);
		ArrayList<String> gotoPath = lr1ProjectSet.getGotoPath();
		for(String p:gotoPath){
			ArrayList<LR1Project> l = lr1ProjectSet.getLRDs(p);//直接通过路径传到下一个状态的情况
			addState(lr1ProjectSet.id,p,l);
		}
	}
	
	//用于获取与一个文法符号相关的产生式集合
	public ArrayList<Production> getDerivation(String v){
		ArrayList<Production> result = new ArrayList<Production>();
		Iterator<Production> iter = Grammar.listDerivation.iterator();
		while(iter.hasNext()){
			Production d = iter.next();
			if(d.left.equals(v)){
				result.add(d);
			}
		}
		return result;
	}
	
	//用于获取一个文法符号的first集合
	private ArrayList<String> first(String v){
		ArrayList<String> result = new ArrayList<String>();
		if(v.equals("$")){
			result.add("$");
		} else {
			Iterator<String> iter = Grammar.MapOfFirst.get(v).iterator();
			while(iter.hasNext()){
				result.add(iter.next());
			}
		}
		return result;
	}
	

	//利用这个方法填充语法分析表的相关内容
	private void createAnalyzeTable(){
		for(int i = 0;i < gotoTable.length;i++){
			for(int j = 0;j < gotoTable[0].length;j++){
				gotoTable[i][j] = -1;
			}
		}
		for(int i = 0;i < actionTable.length;i++){
			for(int j = 0;j < actionTable[0].length;j++){
				actionTable[i][j] = ForecastAnalysisTable.error;
			}
		}
		//完善语法分析表的goto部分
		int gotoCount = this.gotoStart.size();
		for(int i = 0;i < gotoCount;i++){
			int start = gotoStart.get(i);
			int end = gotoEnd.get(i);
			String path = gotoPath.get(i);
			int pathIndex = gotoIndex(path);
			this.gotoTable[start][pathIndex] = end;
		}
		
		//完善语法分析表的action部分
		int stateCount = lr1ProjectCluster.lr1ProjectCluster.size();
		for(int i = 0;i < stateCount;i++){
			LR1ProjectSet state = lr1ProjectCluster.getLR1ProjectSet(i);//获取dfa的单个状态
			for(LR1Project lrd:state.set){//对每一个进行分析
				if(lrd.position == lrd.production.list.size()){
					if(!lrd.production.left.equals("S'")){
						int derivationIndex = derivationIndex(lrd.production);
						String value = "r"+derivationIndex;
						actionTable[i][actionIndex(lrd.terminator)] = value;//设为规约
					} else {
						actionTable[i][actionIndex("$")] = ForecastAnalysisTable.acc;//设为接受
					}
				} else {
					String next = lrd.production.list.get(lrd.position);//获取·后面的文法符号
					if(Grammar.VT.contains(next)){//必须是一个终结符号
						if(gotoTable[i][gotoIndex(next)] != -1){
							actionTable[i][actionIndex(next)] = "s"+gotoTable[i][gotoIndex(next)];
						}
					}
				}
			}
		}
	}
	
	
	//返回goto中的列数
	private int gotoIndex(String s){
		for(int i = 0;i < gotoLength;i++){
			if(gotoCol[i].equals(s)){
				return i;
			}
		}
		return -1;
	}
	
	
	//返回action中的列数
	private int actionIndex(String s){
		for(int i = 0;i < actionLength;i++){
			if(actionCol[i].equals(s)){
				return i;
			}
		}
		return -1;
	}
	
	
	//返回是第几个表达式
	private int derivationIndex(Production d){
		int size = Grammar.listDerivation.size();
		for(int i = 0;i < size;i++){
			if(Grammar.listDerivation.get(i).equals(d)){
				return i;
			}
		}
		return -1;
	}
	//stateIndex 行,String vt 列
	public String ACTION(int stateIndex,String vt){
		int index = actionIndex(vt);
		return actionTable[stateIndex][index];
	}
	//stateIndex 行,String vt 列
	public int GOTO(int stateIndex,String vn){
		int index = gotoIndex(vn);
		return gotoTable[stateIndex][index];
	}
	
	
	
	/**
	 * 打印语法分析表
	 */
	public String print(){
		StringBuilder sb = new StringBuilder();
		String colLine = "";
		sb.append(colLine);
		for(int i = 0;i < actionCol.length;i++){
			colLine += "\t";
			colLine += actionCol[i];
		}
		for(int j = 0;j < gotoCol.length;j++){
			colLine += "\t";
			colLine += gotoCol[j];
		}
		sb.append(colLine+"\n");
		int index = 0;
		for(int i = 0;i < lr1ProjectCluster.lr1ProjectCluster.size();i++){
			String line = String.valueOf(i);
			while(index < actionCol.length){
				line += "\t";
				line += actionTable[i][index];
				index++;
			}
			index = 0;
			while(index < gotoCol.length){
				line += "\t";
				if(gotoTable[i][index] == -1){
					line += "X";
				} else {
					line += gotoTable[i][index];
				}
				index++;
			}
			index = 0;
			line += "\t";
			sb.append(line+"\n");
		}
		return sb.toString();
	}
	//获取所有的状态数量
	public int getStateNum(){
		return lr1ProjectCluster.lr1ProjectCluster.size();
	}

}
