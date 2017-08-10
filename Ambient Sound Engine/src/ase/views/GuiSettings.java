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
	//General Settings
	public final Font smallFont;
	public final Font mediumFont;
	public final Color backgroundColor;
	public final Color foregroundColor;
	
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
	
	private GuiSettings(
		Font smallFont,
		Font mediumFont,
		Color backgroundColor,
		Color foregroundColor,
		Dimension minimumWindowSize,
		Dimension addSoundFrameDefaultSize,
		Dimension manageFrameDefaultSize,
		Dimension metadataFrameDefaultSize,
		Dimension preferencesFrameDefaultSize,
		Dimension helpFrameDefaultSize,
		Dimension aboutFrameDefaultSize
	) {
		this.smallFont = smallFont;
		this.mediumFont = mediumFont;
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
		this.minimumWindowSize = minimumWindowSize;
		this.addSoundFrameDefaultSize = addSoundFrameDefaultSize;
		this.manageFrameDefaultSize = manageFrameDefaultSize;
		this.metadataFrameDefaultSize = metadataFrameDefaultSize;
		this.preferencesFrameDefaultSize = preferencesFrameDefaultSize;
		this.helpFrameDefaultSize = helpFrameDefaultSize;
		this.aboutFrameDefaultSize = aboutFrameDefaultSize;
	}
	
	public SettingsBuilder modifySettings() {
		return new SettingsBuilder()
				.setSmallFont(smallFont)
				.setMediumFont(mediumFont)
				.setBackgroundColor(backgroundColor)
				.setForegroundColor(foregroundColor)
				.setMinimumWindowSize(minimumWindowSize)
				.setAddSoundFrameDefaultSize(addSoundFrameDefaultSize)
				.setManageFrameDefaultSize(manageFrameDefaultSize)
				.setMetadataFrameDefaultSize(metadataFrameDefaultSize)
				.setPreferencesFrameDefaultSize(preferencesFrameDefaultSize)
				.setHelpFrameDefaultSize(helpFrameDefaultSize)
				.setAboutFrameDefaultSize(aboutFrameDefaultSize);
	}
	
	public static class SettingsBuilder {
		private Map<String, Font> fonts = new HashMap<>();
		private Map<String, Color> colors = new HashMap<>();
		private Map<String, Dimension> dimensions = new HashMap<>();
		
		public SettingsBuilder() {
			fonts.put("smallFont", new Font("Arial", 0, 12));
			fonts.put("mediumFont", new Font("Tahoma", 0, 16));
			colors.put("backgroundColor", new Color(100,100,100));
			colors.put("foregroundColor", new Color(240,240,255));
			dimensions.put("minimumWindowSize", new Dimension(815, 781));
			dimensions.put("addSoundFrameDefaultSize", new Dimension(800, 900));
			dimensions.put("manageFrameDefaultSize", new Dimension(1000, 600));
			dimensions.put("metadataFrameDefaultSize", new Dimension(500, 600));
			dimensions.put("preferencesFrameDefaultSize", new Dimension(550, 300));
			dimensions.put("helpFrameDefaultSize", new Dimension(200, 200));
			dimensions.put("aboutFrameDefaultSize", new Dimension(200, 400));
		}
		
		public SettingsBuilder setSmallFont (Font f) {
			fonts.put("smallFont", f);
			return this;
		}
		
		public SettingsBuilder setMediumFont (Font f) {
			fonts.put("mediumFont", f);
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
		
		public GuiSettings build() {
			return new GuiSettings(
				fonts.get("smallFont"),
				fonts.get("mediumFont"),
				colors.get("backgroundColor"),
				colors.get("foregroundColor"),
				dimensions.get("minimumWindowSize"),
				dimensions.get("addSoundFrameDefaultSize"),
				dimensions.get("manageFrameDefaultSize"),
				dimensions.get("metadataFrameDefaultSize"),
				dimensions.get("preferencesFrameDefaultSize"),
				dimensions.get("helpFrameDefaultSize"),
				dimensions.get("aboutFrameDefaultSize")
			);
		}
	}
}

