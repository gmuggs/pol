package edu.gmu.mason.vanilla.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.FieldPortrayal2D;

/**
 * General description_________________________________________________________
 * A class used for displaying text on the GUI
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class HUD extends FieldPortrayal2D {
	public static final int GUTTER = 32;
	public static final int BORDER = 8;
	public static final int DEFAULT_FONTSIZE = 15;

	protected int fontSize = DEFAULT_FONTSIZE;
	protected Font font = new Font("SansSerif", Font.BOLD, fontSize);
	protected Color color = new Color(33, 33, 222);
	protected Map<String, Object> properties;

	public HUD() {
		properties = new HashMap<String, Object>();
	}

	public void setFontSize(int size) {
		if (size < 1)
			size = 1;
		fontSize = size;
		font = new Font("SansSerif", Font.BOLD, fontSize);
	}

	public int getFontSize() {
		return fontSize;
	}

	public void addProperty(String key, Object value) {
		properties.put(key, value);
	}

	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		graphics.setFont(font);
		graphics.setColor(color);
		int y = 0;
		for (Entry<String, Object> entry : properties.entrySet()) {
			String text = entry.getKey() + ": " + entry.getValue().toString();
			Rectangle2D bounds = new TextLayout(text, font,
					graphics.getFontRenderContext()).getBounds();
			if (y == 0)
				y = (int) ((GUTTER + bounds.getHeight()) / 2);
			graphics.drawString(text, BORDER, y);
			y += fontSize;
		}
	}
}