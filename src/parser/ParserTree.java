package parser;

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ParserTree {
	private String data;
	private List<ParserTree> childs;
	
	public ParserTree(String data) {
		this.data = data;
		childs = new ArrayList();
		childs.clear();
	}

	public void addNode(ParserTree tree) {
		childs.add(tree);
	}

	public void clearParserTreeNode() {
		data = null;
		childs.clear();
	}

	public int dept() {
		return dept(this);
	}

	public int dept(ParserTree tree) {
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

	public ParserTree getChild(int i) {
		return childs.get(i);
	}

	public ParserTree getFirstChild() {
		return childs.get(0);

	}

	public ParserTree getLastChild() {
		return childs.get(childs.size() - 1);
	}

	public List<ParserTree> getChilds() {
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

	public ParserTree root() {
		return this;
	}

	public void setRootData(String data) {
		this.data = data;
	}

	public int size() {
		return size(this);
	}

	private int size(ParserTree tree) {
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

	/**
	 * 先根遍历
	 * 
	 * @param root 要的根结点
	 */
	public void preOrder(ParserTree root) {
		if (!root.isEmpty()) {
			visit(root);
			for (ParserTree child : root.getChilds()) {
				if (child != null) {
					preOrder(child);
				}
			}
		}
	}

	public void leftToRight(ParserTree root) {
		if (!root.isEmpty()) {
			if (root.isLeaf() && Grammar.VT.contains(root.getRootData())) {
				visit(root);
			}
			for (ParserTree child : root.getChilds()) {
				if (child != null) {
					leftToRight(child);
				}
			}
		}
	}

	/**
	 * 后根遍历
	 * 
	 * @param root 树的根结点
	 */
	public void postOrder(ParserTree root) {
		if (!root.isEmpty()) {
			for (ParserTree child : root.getChilds()) {
				if (child != null) {
					preOrder(child);
				}
			}
			visit(root);
		}
	}

	public void visit(ParserTree tree) {
		System.out.print("" + tree.getRootData());
	}

	public StringBuilder printTree(int level, ParserTree root,StringBuilder sb) {
		if(level == 0) {
			System.out.println(levelSign(level) + root.getRootData());
			sb.append(levelSign(level) + root.getRootData()+"\n");
		}
		level++;
		if (root.isEmpty()) {
			return new StringBuilder("");
		}
		for (ParserTree child : root.getChilds()) {
			if (child != null) {
				if (child.isLeaf()) {
					sb.append(levelSign(level) + child.getRootData()+"\n");
					System.out.println(levelSign(level) + child.getRootData());
				} else {
					System.out.println(levelSign(level) + child.getRootData());
					sb.append(levelSign(level) + child.getRootData()+"\n");
					printTree(level, child, sb);
				}
			}
		}
		return sb;
	}

	private static String levelSign(int level) {
		StringBuilder sb = new StringBuilder();
		sb.append(" |——");
		for (int x = 0; x < level; x++) {
			sb.insert(0, " |   ");
		}
		return sb.toString();
	}
}
