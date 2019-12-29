package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeSet;


//文法类
public class Grammar {

	public static String emp = "ε";
	public static String end = "$";
	
	public static TreeSet<String> VN = new TreeSet<String>();// 非终结符集
	public static TreeSet<String> VT = new TreeSet<String>();// 终结符集
	public static ArrayList<Production> listDerivation = new ArrayList<Production>();// 产生式集
	public static HashMap<String, TreeSet<String>> MapOfFirst = new HashMap<String, TreeSet<String>>();// first

	static {
		try {
			readFile("Grammar.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		calculateFirstSet();
	}
	private static void readFile(String filename) throws FileNotFoundException {
		File file = new File("in/" + filename);
		Scanner scanner = new Scanner(file);
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			String[] div = line.split("->");
			String[] right = div[1].split("\\|");// 将合并书写的多个表达式解析成多个;
			for (String item : right) {
				Production derivation = new Production(div[0] + "->" + item);
				listDerivation.add(derivation);
				VN.add(div[0]);
				if(VT.contains(div[0])) {
					VT.remove(div[0]);
				}
			}
			for (String item : right) {
				String [] it = item.split(" ");
				for (String str : it) {
					if(!VN.contains(str) && !Objects.equals(" ", str)) {
						VT.add(str);
					}
				}
			}	
		}
		scanner.close();
	}

	
	

	// 计算所有符号的first集合 
	private static void calculateFirstSet() {
		// 计算所有终结符的first集合
		Iterator<String> iter1 = VT.iterator();
		while (iter1.hasNext()) {
			String vt = iter1.next();
			MapOfFirst.put(vt, new TreeSet<String>());
			MapOfFirst.get(vt).add(vt);
		}
		// 计算所有非终结符的first集合
		Iterator<String> iter2 = VN.iterator();
		while (iter2.hasNext()) {
			//获取vn
			String vn = iter2.next();
			//将终结符vn放在firstMap中并为其申请一个set集合
			MapOfFirst.put(vn, new TreeSet<String>());
			int dSize = listDerivation.size();
			for (int i = 0; i < dSize; i++) {
				Production production = listDerivation.get(i);
				
				if (production.left.equals(vn)) {
					// 如果是产生式右端第一个文法符号是一个终结符，则直接添加
					if (VT.contains(production.list.get(0))) {
						MapOfFirst.get(vn).add(production.list.get(0));
					} else {
						// 如果产生式右端第一个文法符号是个非终结符，则需要进行递归查找
						MapOfFirst.get(vn).addAll(findFirstSet(production.list.get(0)));
					}
				}
			}
		}
		
		
	}

	//查找非终结符vn的first集合
	private static TreeSet<String> findFirstSet(String vn) {
		TreeSet<String> set = new TreeSet<String>();
		for (Production d : listDerivation) {
			if (d.left.equals(vn)) {
				if (VT.contains(d.list.get(0))) {// 如果是个终结符，则直接加入
					set.add(d.list.get(0));
				} else {
					if (!vn.equals(d.list.get(0))) {// 去除类似于E->E*E这样的左递归，从而有效避免栈溢出
						set.addAll(findFirstSet(d.list.get(0)));// 再次递归
					}
				}
			}
		}
		return set;
	}
	
	
}
