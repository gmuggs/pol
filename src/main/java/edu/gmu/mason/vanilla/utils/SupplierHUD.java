package edu.gmu.mason.vanilla.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.Map.Entry;
import java.util.function.Supplier;

import edu.gmu.mason.vanilla.gui.HUD;
import sim.portrayal.DrawInfo2D;
/**
 * General description_________________________________________________________
 * Assists with MASON
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */

@SuppressWarnings("rawtypes") 
public class SupplierHUD extends HUD{
	public void addProperty(String key, Supplier value) {
		properties.put(key, value);
	}

	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		graphics.setFont(font);
		graphics.setColor(Color.white);
		int y = 0;
		for(Entry<String, Object> entry: properties.entrySet()) {
			String text = entry.getKey() + ": ";
			if(entry.getValue() instanceof Supplier) {
				Supplier s = (Supplier)entry.getValue();
				text += s.get().toString();
			}
			else 
				text += entry.getValue().toString();
			
			Rectangle2D bounds = new TextLayout(text, font, graphics.getFontRenderContext()).getBounds();
			if(y == 0)
				y = (int)((GUTTER + bounds.getHeight()) / 2);
			graphics.drawString(text, BORDER, y);
			y += fontSize;
		}
	}
}
