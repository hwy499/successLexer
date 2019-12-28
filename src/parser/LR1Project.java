package parser;


//LR1项目LR1Project 

public class LR1Project implements Cloneable {

	public Production production;
	public String terminator; 
	public int position;

	public LR1Project(Production d, String lr, int index) {
		this.production = d;
		this.terminator = lr;
		this.position = index;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(production.left + "->");
		int length = production.list.size();
		for (int i = 0; i < length; i++) {
			sb.append(" ");
			if (i == position) {
				sb.append("·");
			}
			sb.append(production.list.get(i));
		}
		if (position == length) {
			sb.append("·");
		}
		sb.append(","+terminator);
		return sb.toString();
	}

	public boolean equalTo(LR1Project lrd) {
		if (production.equalTo(lrd.production) && terminator.hashCode() == lrd.terminator.hashCode() && position == lrd.position) {
			return true;
		} else {
			return false;
		}
	}

	public String print() {
		System.out.println(this.toString());
		return this.toString()+"\n";
	}

	public Object clone() {
		return new LR1Project(production, terminator, position);
	}

}
