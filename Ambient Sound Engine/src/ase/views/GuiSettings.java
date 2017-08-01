package ase.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Completely mutable Settings class. Meant to be a
 * single source of truth for all gui elements for
 * style settings
 * @author Kevin C. Gall
 *
 */
public class GuiSettings {
	public Font smallFont = new Font("Arial", 0, 12);
	public Color backgroundColor = new Color(255,255,255);
	public Dimension minimumWindowSize = new Dimension(815, 781);
}
