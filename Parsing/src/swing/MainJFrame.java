package swing;

import javax.swing.JFrame;
import javax.swing.JLabel;


public class MainJFrame extends JFrame{
	private static final long serialVersionUID = 1L;

	public MainJFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 100, 100);
		JLabel label = new JLabel("运行完成");
		this.add(label);
		this.setVisible(true);
	}
	public static void main(String[] args) {
		new MainJFrame();
	}
}
