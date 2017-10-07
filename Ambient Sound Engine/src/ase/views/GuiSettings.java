package ase.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * Completely mutable Settings class. Meant to be a
 * single source of truth for all gui elements for
 * style settings
 * @author Kevin C. Gall
 *
 */
public class GuiSettings {
	/* General Settings */
	//Fonts / Text
	public final Font smallFont;
	public final Font mediumFont;
	public final Font largeFont;
	public final Color lightText;
	
	public final Color backgroundColor;
	public final Color foregroundColor;
	public final Color white;

	/* Window Sizing */
	
	public final Dimension minimumWindowSize;
	
	//AddSoundFrame Settings
	public final Dimension addSoundFrameDefaultSize;
	
	//ManageFrame settings
	public final Dimension manageFrameDefaultSize;
	
	//MetadataFrame settings
	public final Dimension metadataFrameDefaultSize;
	
	//PreferencesFrame settings
	public final Dimension preferencesFrameDefaultSize;
	
	//HelpFrame settings
	public final Dimension helpFrameDefaultSize;
	
	//AboutFrame settings
	public final Dimension aboutFrameDefaultSize;
	
	/* Buttons */
	public final Dimension buttonSize;
	
	private GuiSettings(
		Font smallFont,
		Font mediumFont,
		Font largeFont,
		Color lightText,
		Color backgroundColor,
		Color foregroundColor,
		Color white,
		Dimension minimumWindowSize,
		Dimension addSoundFrameDefaultSize,
		Dimension manageFrameDefaultSize,
		Dimension metadataFrameDefaultSize,
		Dimension preferencesFrameDefaultSize,
		Dimension helpFrameDefaultSize,
		Dimension aboutFrameDefaultSize,
		Dimension buttonSize
	) {
		this.smallFont = smallFont;
		this.mediumFont = mediumFont;
		this.largeFont = largeFont;
		this.lightText = lightText;
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
		this.white = white;
		this.minimumWindowSize = minimumWindowSize;
		this.addSoundFrameDefaultSize = addSoundFrameDefaultSize;
		this.manageFrameDefaultSize = manageFrameDefaultSize;
		this.metadataFrameDefaultSize = metadataFrameDefaultSize;
		this.preferencesFrameDefaultSize = preferencesFrameDefaultSize;
		this.helpFrameDefaultSize = helpFrameDefaultSize;
		this.aboutFrameDefaultSize = aboutFrameDefaultSize;
		this.buttonSize = buttonSize;
	}
	
	public SettingsBuilder modifySettings() {
		return new SettingsBuilder()
				.setSmallFont(smallFont)
				.setMediumFont(mediumFont)
				.setLargeFont(largeFont)
				.setLightText(lightText)
				.setBackgroundColor(backgroundColor)
				.setForegroundColor(foregroundColor)
				.setWhite(white)
				.setMinimumWindowSize(minimumWindowSize)
				.setAddSoundFrameDefaultSize(addSoundFrameDefaultSize)
				.setManageFrameDefaultSize(manageFrameDefaultSize)
				.setMetadataFrameDefaultSize(metadataFrameDefaultSize)
				.setPreferencesFrameDefaultSize(preferencesFrameDefaultSize)
				.setHelpFrameDefaultSize(helpFrameDefaultSize)
				.setAboutFrameDefaultSize(aboutFrameDefaultSize)
				.setButtonSize(buttonSize);
	}
	
	public static class SettingsBuilder {
		private Map<String, Font> fonts = new HashMap<>();
		private Map<String, Color> colors = new HashMap<>();
		private Map<String, Dimension> dimensions = new HashMap<>();
		
		public SettingsBuilder() {
			fonts.put("smallFont", new Font("Arial", 0, 12));
			fonts.put("mediumFont", new Font("Tahoma", 0, 16));
			fonts.put("largeFont", new Font("Tahoma", 0, 22));
			colors.put("lightText", new Color(220,220,220));
			colors.put("backgroundColor", new Color(100,100,100));
			colors.put("foregroundColor", new Color(240,240,255));
			colors.put("white", new Color(255,255,255));
			dimensions.put("minimumWindowSize", new Dimension(815, 781));
			dimensions.put("addSoundFrameDefaultSize", new Dimension(800, 900));
			dimensions.put("manageFrameDefaultSize", new Dimension(1000, 600));
			dimensions.put("metadataFrameDefaultSize", new Dimension(500, 600));
			dimensions.put("preferencesFrameDefaultSize", new Dimension(550, 300));
			dimensions.put("helpFrameDefaultSize", new Dimension(200, 200));
			dimensions.put("aboutFrameDefaultSize", new Dimension(200, 400));
			dimensions.put("buttonSize", new Dimension(25, 25));
		}
		
		public SettingsBuilder setSmallFont (Font f) {
			fonts.put("smallFont", f);
			return this;
		}
		
		public SettingsBuilder setMediumFont (Font f) {
			fonts.put("mediumFont", f);
			return this;
		}
		
		public SettingsBuilder setLargeFont (Font f) {
			fonts.put("largeFont", f);
			return this;
		}
		
		public SettingsBuilder setLightText (Color c) {
			colors.put("lightText", c);
			return this;
		}
		
		public SettingsBuilder setBackgroundColor(Color c) {
			colors.put("backgroundColor", c);
			return this;
		}
		
		public SettingsBuilder setForegroundColor(Color c) {
			colors.put("foregroundColor", c);
			return this;
		}
		
		public SettingsBuilder setWhite(Color c) {
			colors.put("white", c);
			return this;
		}
		
		public SettingsBuilder setMinimumWindowSize(Dimension d) {
			dimensions.put("minimumWindowSize", d);
			return this;
		}
		
		public SettingsBuilder setAddSoundFrameDefaultSize(Dimension d) {
			dimensions.put("addSoundFrameDefaultSize", d);
			return this;
		}
		
		public SettingsBuilder setManageFrameDefaultSize(Dimension d) {
			dimensions.put("manageFrameDefaultSize", d);
			return this;
		}
		
		public SettingsBuilder setMetadataFrameDefaultSize(Dimension d) {
			dimensions.put("metadataFrameDefaultSize", d);
			return this;
		}
		
		public SettingsBuilder setPreferencesFrameDefaultSize(Dimension d) {
			dimensions.put("preferencesFrameDefaultSize", d);
			return this;
		}
		
		public SettingsBuilder setHelpFrameDefaultSize(Dimension d) {
			dimensions.put("helpFrameDefaultSize", d);
			return this;
		}
		
		public SettingsBuilder setAboutFrameDefaultSize(Dimension d) {
			dimensions.put("aboutFrameDefaultSize", d);
			return this;
		}
		
		public SettingsBuilder setButtonSize(Dimension d) {
			dimensions.put("buttonSize", d);
			return this;
		}
		
		public GuiSettings build() {
			return new GuiSettings(
				fonts.get("smallFont"),
				fonts.get("mediumFont"),
				fonts.get("largeFont"),
				colors.get("lightText"),
				colors.get("backgroundColor"),
				colors.get("foregroundColor"),
				colors.get("white"),
				dimensions.get("minimumWindowSize"),
				dimensions.get("addSoundFrameDefaultSize"),
				dimensions.get("manageFrameDefaultSize"),
				dimensions.get("metadataFrameDefaultSize"),
				dimensions.get("preferencesFrameDefaultSize"),
				dimensions.get("helpFrameDefaultSize"),
				dimensions.get("aboutFrameDefaultSize"),
				dimensions.get("buttonSize")
			);
		}
	}
}

