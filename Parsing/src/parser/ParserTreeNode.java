package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//语法树--数据结构
public class ParserTreeNode {
	private String data;
	private List<ParserTreeNode> childs;
	
	public ParserTreeNode(String data) {
		this.data = data;
		childs = new ArrayList<ParserTreeNode>();
		childs.clear();
	}

	public void addNode(ParserTreeNode tree) {
		childs.add(tree);
	}

	public void clearParserTreeNode() {
		data = null;
		childs.clear();
	}

	public int dept() {
		return dept(this);
	}

	public int dept(ParserTreeNode tree) {
		if (tree.isEmpty()) {
			return 0;
		} else if (tree.isLeaf()) {
			return 1;
		} else {
			int n = tree.childs.size();
			int[] a = new int[n];
			for (int i = 0; i < n; i++) {
				if (tree.childs.get(i).isEmpty()) {
					a[i] = 0 + 1;
				} else {
					a[i] = dept(tree.childs.get(i)) + 1;
				}
			}
			Arrays.sort(a);
			return a[n - 1];
		}
	}

	public ParserTreeNode getChild(int i) {
		return childs.get(i);
	}

	public ParserTreeNode getFirstChild() {
		return childs.get(0);

	}

	public ParserTreeNode getLastChild() {
		return childs.get(childs.size() - 1);
	}

	public List<ParserTreeNode> getChilds() {
		return childs;
	}

	public Object getRootData() {
		return data;
	}

	public boolean isEmpty() {
		if (childs.isEmpty() && data == null) {
			return true;
		}
		return false;
	}

	public boolean isLeaf() {
		if (childs.isEmpty())
			return true;
		return false;
	}

	public ParserTreeNode root() {
		return this;
	}

	public void setRootData(String data) {
		this.data = data;
	}

	public int size() {
		return size(this);
	}
	
	private int size(ParserTreeNode tree) {
		if (tree.isEmpty()) {
			return 0;
		} else if (tree.isLeaf()) {
			return 1;
		} else {
			int count = 1;
			int n = tree.childs.size();
			for (int i = 0; i < n; i++) {
				if (!tree.childs.get(i).isEmpty()) {
					count += size(tree.childs.get(i));
				}
			}
			return count;
		}
	}
}
