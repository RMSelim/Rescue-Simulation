package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class GameOver extends JFrame{
	private JTextArea endgame;
	
	
	
	public GameOver(){
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1200, 700);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2);
		endgame=new JTextArea();
		endgame.setEditable(false);
		endgame.setFont(new Font(Font.SERIF, Font.BOLD, 50));
		add(endgame, BorderLayout.CENTER);
	}
	
	public void addcasualties(int x) {
		String s="Game Over"+"\n"+"Number of Casualties: ";
		endgame.setText(s+x);
	}

}
