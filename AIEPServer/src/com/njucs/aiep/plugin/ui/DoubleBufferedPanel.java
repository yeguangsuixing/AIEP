package com.njucs.aiep.plugin.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public abstract class DoubleBufferedPanel extends JPanel {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4378679551072396001L;
	
	private Dimension offDimension = null;
	private Graphics offGraphics = null;
	private Image offImage = null;
	
	protected abstract void paint0(Graphics g);

	public void paint(Graphics g){
		Dimension d = getSize(); 
		if( d.width == 0 || d.height == 0) return;
		if ((offGraphics == null) || (d.width != offDimension.width) 
	               || (d.height != offDimension.height)) { 
			offDimension = d; 
			offImage = createImage(d.width, d.height); 
			offGraphics = offImage.getGraphics();
		}
		offGraphics.setColor(getBackground()); 
		offGraphics.fillRect(0, 0, d.width, d.height);
		offGraphics.setColor(Color.black);
		paint0(offGraphics);
		g.drawImage(offImage, 0, 0, this);
	}
}
