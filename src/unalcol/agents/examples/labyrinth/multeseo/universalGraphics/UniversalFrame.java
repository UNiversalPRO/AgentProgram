package unalcol.agents.examples.labyrinth.multeseo.universalGraphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class UniversalFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Map map;
	/**
	 * Create the frame.
	 */
	public UniversalFrame( Map map ) {
		this.map = map;
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setBounds(500, 0, 1000, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	public void drawWall( int posX, int posY ){
		
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
	}
}
