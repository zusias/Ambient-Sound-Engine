package ase.views.frames;

import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.google.common.eventbus.Subscribe;

import ase.views.GuiSettings;
import ase.views.events.SettingsEvent;

public class AboutFrame extends SubFrame {
	private static final long serialVersionUID = 8864837387600530238L;
	
	private final GridBagLayout layout = new GridBagLayout();
	
	private final JLabel title = new JLabel("Ambient Sound Engine v5.0");
	private final GridBagConstraints titleGbc = new GridBagConstraints();
	private final JLabel createdBy = new JLabel("Created By:");
	private final GridBagConstraints createdByGbc = new GridBagConstraints();
	private final JLabel gary = new JLabel("Eric \"Lance\" Gary (elancegary@hotmail.com)");
	private final GridBagConstraints garyGbc = new GridBagConstraints();
	private final JLabel shaw = new JLabel("David Shaw (david.mi.shaw@gmail.com)");
	private final GridBagConstraints shawGbc = new GridBagConstraints();
	private final JLabel kidwell = new JLabel("Chris Kidwell (kidwell.chris.l@mail.com)");
	private final GridBagConstraints kidwellGbc = new GridBagConstraints();
	private final JLabel gall = new JLabel("Kevin C. Gall (kcg245@gmail.com)");
	private final GridBagConstraints gallGbc = new GridBagConstraints();
	private final JLabel thanks = new JLabel("Special Thanks To:");
	private final GridBagConstraints thanksGbc = new GridBagConstraints();
	private final JLabel wirth = new JLabel("Jeff Wirth");
	private final GridBagConstraints wirthGbc = new GridBagConstraints();
	private final JLabel windyga = new JLabel("Dr. Piotr Windyga");
	private final GridBagConstraints windygaGbc = new GridBagConstraints();
	private final JLabel llewellyn = new JLabel("Dr. Mark Llewellyn");
	private final GridBagConstraints llewellynGbc = new GridBagConstraints();
	
	public AboutFrame(GuiSettings settings) {
		super("ASE About", settings);
		
		this.setSize(settings.aboutFrameDefaultSize);
		
		this.setLayout(layout);
		setupGridbagConstraints();
		
		title.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.add(title, titleGbc);
		this.add(createdBy, createdByGbc);
		this.add(gary, garyGbc);
		this.add(shaw, shawGbc);
		this.add(kidwell, kidwellGbc);
		this.add(gall, gallGbc);
		this.add(thanks, thanksGbc);
		this.add(wirth,wirthGbc);
		this.add(windyga, windygaGbc);
		this.add(llewellyn, llewellynGbc);
	}
	
	private void setupGridbagConstraints() {
		int gridy = 0;
		
		titleGbc.fill = HORIZONTAL;
		titleGbc.anchor = EAST;
		titleGbc.gridx = 0;
		titleGbc.gridy = gridy++;
		titleGbc.insets = new Insets(0, 0, 10, 0);
		
		Insets subtitleInsets = new Insets(5, 0, 3, 0);
		Insets nameInsets = new Insets(0, 10, 0, 0);
		
		createdByGbc.fill = HORIZONTAL;
		createdByGbc.anchor = EAST;
		createdByGbc.gridx = 0;
		createdByGbc.gridy = gridy++;
		createdByGbc.insets = subtitleInsets;
		
		garyGbc.fill = HORIZONTAL;
		garyGbc.anchor = EAST;
		garyGbc.gridx = 0;
		garyGbc.gridy = gridy++;
		garyGbc.insets = nameInsets;
		
		shawGbc.fill = HORIZONTAL;
		shawGbc.anchor = EAST;
		shawGbc.gridx = 0;
		shawGbc.gridy = gridy++;
		shawGbc.insets = nameInsets;
		
		kidwellGbc.fill = HORIZONTAL;
		kidwellGbc.anchor = EAST;
		kidwellGbc.gridx = 0;
		kidwellGbc.gridy = gridy++;
		kidwellGbc.insets = nameInsets;
		
		gallGbc.fill = HORIZONTAL;
		gallGbc.anchor = EAST;
		gallGbc.gridx = 0;
		gallGbc.gridy = gridy++;
		gallGbc.insets = nameInsets;
		
		//Special Thanks
		thanksGbc.fill = HORIZONTAL;
		thanksGbc.anchor = EAST;
		thanksGbc.gridx = 0;
		thanksGbc.gridy = gridy++;
		thanksGbc.insets = subtitleInsets;
		
		wirthGbc.fill = HORIZONTAL;
		wirthGbc.anchor = EAST;
		wirthGbc.gridx = 0;
		wirthGbc.gridy = gridy++;
		wirthGbc.insets = nameInsets;
		
		windygaGbc.fill = HORIZONTAL;
		windygaGbc.anchor = EAST;
		windygaGbc.gridx = 0;
		windygaGbc.gridy = gridy++;
		windygaGbc.insets = nameInsets;
		
		llewellynGbc.fill = HORIZONTAL;
		llewellynGbc.anchor = EAST;
		llewellynGbc.gridx = 0;
		llewellynGbc.gridy = gridy++;
		llewellynGbc.insets = nameInsets;
	}
	
	@Subscribe public void applySettings(SettingsEvent e) {
		super.applySettings(e);
		
		this.setSize(settings.aboutFrameDefaultSize);
		title.setFont(settings.largeFont);
		
		Font subtitleFont = new Font(settings.smallFont.getFontName(), Font.BOLD | Font.ITALIC, settings.smallFont.getSize());
		createdBy.setFont(subtitleFont);
		thanks.setFont(subtitleFont);
		
		gary.setFont(settings.smallFont);
		shaw.setFont(settings.smallFont);
		kidwell.setFont(settings.smallFont);
		gall.setFont(settings.smallFont);
		wirth.setFont(settings.smallFont);
		windyga.setFont(settings.smallFont);
		llewellyn.setFont(settings.smallFont);
	}
}