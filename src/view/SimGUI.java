package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import controller.CommandCenter;
import model.events.WorldListener;
import simulation.Address;

@SuppressWarnings("serial")
public class SimGUI extends JFrame{
	private JPanel west;
	private JPanel center;
	private JPanel east;
	private JPanel grid;
	private JPanel nxtcyc;	
	private JTextArea general;
	private JScrollPane info;
	private DefaultListModel<String> info1;
	private JScrollPane log;
	private DefaultListModel<String> log1;
	private JPanel avunits;
	private JPanel respunits;
	private JScrollPane unitinfo;
	private DefaultListModel<String> unitinfo1;
	private JScrollPane avunitsscroller;
	private JScrollPane respspunitsscroller;

	
	public SimGUI() {
		setTitle("Rescue Simulation");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1200, 700);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2);
	// CENTER
		center=new JPanel();
		center.setPreferredSize(new Dimension(600, 700));
		center.setLayout(new BorderLayout());
		add(center, BorderLayout.CENTER);
		
		grid= new JPanel();
	    grid.setPreferredSize(new Dimension(600, 600));
		grid.setLayout(new GridLayout(0, 10));
		center.add(grid, BorderLayout.CENTER);
		
		nxtcyc= new JPanel();
		nxtcyc.setPreferredSize(new Dimension(600, 100));
		center.add(nxtcyc, BorderLayout.SOUTH);
		
	//WEST
		west= new JPanel();
		west.setPreferredSize(new Dimension(300, 700));
		west.setLayout(new BorderLayout());
		add(west, BorderLayout.WEST);
		
		general= new JTextArea();
		general.setPreferredSize(new Dimension(300, 100));
		general.setEditable(false);
		general.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		west.add(general, BorderLayout.NORTH);
		
		info1=new DefaultListModel<>();
		JList<String> info2 = new JList<>(info1);
		info= new JScrollPane(info2,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		info.setPreferredSize(new Dimension(300, 350));
		JLabel infoTitle = new JLabel("                                    Rescuable Info");
		info.setColumnHeaderView(infoTitle);
		west.add(info, BorderLayout.WEST); 
		
		log1=new DefaultListModel<>();
		JList<String> log2=new JList<>(log1);
		log= new JScrollPane(log2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		log.setPreferredSize(new Dimension(300, 250));
		JLabel logtitle=new JLabel("                                          Log");
		log.setColumnHeaderView(logtitle);
		west.add(log, BorderLayout.SOUTH);  
		
	//EAST
		east = new JPanel();
		east.setPreferredSize(new Dimension(300, 700));
		east.setLayout(new BorderLayout());
		add(east,BorderLayout.EAST);
		
		avunits=new JPanel();
		Border avunittitle=BorderFactory.createTitledBorder("Available Units");
		avunits.setBorder(avunittitle);
		avunits.setLayout(new GridLayout(0, 3));
		avunitsscroller=new JScrollPane(avunits);
		avunitsscroller.setPreferredSize(new Dimension(300, 300));
		east.add(avunitsscroller, BorderLayout.NORTH);
		

		respunits=new JPanel();
		Border respunitstitle=BorderFactory.createTitledBorder("Responding/Treating Units");
		respunits.setBorder(respunitstitle);
		respunits.setLayout(new GridLayout(0, 3));
		respspunitsscroller= new JScrollPane(respunits);
		respspunitsscroller.setPreferredSize(new Dimension(300, 300));
		east.add(respspunitsscroller, BorderLayout.CENTER);
		
		
		unitinfo1=new DefaultListModel<>();
		JList<String> unitinfo2= new JList<>(unitinfo1);
		unitinfo=new JScrollPane(unitinfo2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		unitinfo.setPreferredSize(new Dimension(300, 200));
		JLabel unitinfotitle= new JLabel("Unit Info");
		unitinfo.setColumnHeaderView(unitinfotitle);
		east.add(unitinfo, BorderLayout.SOUTH);
		
		
		
		
		
		
	}
	public void addavunitbutton(JButton button) {
		avunits.add(button);
	}
	
	public void addrespunitbutton(JButton button) {
		respunits.add(button);
	}
	
	public void addunitinfo(String s) {
		unitinfo1.addElement(s);
	}
	
	public void addnxtcyc(JButton button) {
		nxtcyc.add(button);
	}

	public void addgrid(JButton button) {
		grid.add(button);
	}
	public void addgeneral(String x) {
		
		general.setText("                           General Info"+"\n"+x);
	}
	public void addlog(String x) {
	log1.addElement(x);
	}
	
	public void addrescuableinfo(String x) {
	info1.addElement(x);
	}
	
	public DefaultListModel<String> getInfo1() {
		return info1;
	}
	
	public DefaultListModel<String> getLog1() {
		return log1;
	}
	
	public DefaultListModel<String> getUnitinfo1() {
		return unitinfo1;
	}
	
	public JPanel getAvunits() {
		return avunits;
	}
	
	public JPanel getRespunits() {
		return respunits;
	}  
	
	
	
	
	
	
	
	
	
	
	
	
	
}
