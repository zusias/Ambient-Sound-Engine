package ase_source_bak;

import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The operations manager class handles much of the routing of commands
 * from the GUI to the code that will manages functions.
 * 
 * Created on April 6, 2006, 11:16 AM
 * 
 * <br><br>
 * History -
 * <br>10/Oct/2015 - CKidwell - Fixed a bug where the lower console was
 * not updating its play icons correctly.
 * 
 * <br><br>
 * NOTES
 * <br>6/14/2016 - Kevin Gall -
 * <b>Trying to understand the flow of the start/stop commands and threads.</b><br>
 * It appears that all sounds are started and stopped by
 * the instance of FmodExEngine, which will have a console for each logical unit
 * that plays sounds (SoundControlPanels). The "Play Once" sounds start a thread
 * when those sounds begin playing that sleeps for the length of the sound, then
 * wakes up to turn the sound off before it starts again.
 * The single-play thread scheme is buggy as of this date, as I've witnessed
 * single-play sounds repeat endlessly, and separately, I've seen them stop in the middle of their play,
 * only to start back up again minutes later...
 * 
 * 2017 Code Reorganization:
 * Several methods from the SoundEngine and Soundcard classes were made private, screwing up
 * a lot of access the OperationsManager depended on. The big job of this refactor is to
 * implement a reasonable member access model. Previously, there were very few private members,
 * meaning any class could access any member of a member of a member of a nested class. It was
 * a mess...
 * 
 * @author CKidwell 
 */
public class OperationsManager {
//Static final variables for ops manager
   static final int LEFTPANEL = 0;
   static final int TOP = 1;
   static final int BOTTOM = 2;
   
   static final int PREVIEWBUTTON = 0;
   static final int DELETEBUTTON = 1;
   static final int ROWSELECTTOGGLE = 2;
   static final int PLAYMODEBUTTON = 3;
   static final int PLAYPAUSEBUTTON = 4;
   
   static final int PREVIEW=2;
   static final int STAGE=1;
   
   static final int INPUTFIELDSIZE = 15;
   
   static final int SOUND = 1;
   static final int SOUNDSCAPE = 2;
   
   static final boolean UNPAUSED = true;
   static final boolean PAUSED = false;
   
   static final boolean ON = true;
   static final boolean OFF = false;
   
   static final int MASTERCONTROL = -1;
   
   static final int STATEMAPROWCOUNT = 25;
   static final int STATEMAPCOLUMNCOUNT = 2;
   
//Static Variables
   static FmodExEngine soundEngine;
   static Database db;
   static Gui app;
   
//Private Variables
   private int panelID;
   private Soundscape soundscape;
   private Vector<ResultObject> resultObject;
   private FadeThread currentFade; //the current fader thread, if any
   
//Package Variables
   Vector<PlayOnceThread> playingThreads; 

//Constructors
   /** Creates a new instance of OperationsManager */
   public OperationsManager(){
	   // No argument instance - for master GUI
	   playingThreads = new Vector<PlayOnceThread>();
   }
   public OperationsManager(int ID) {
	   // Single argument instance - refers to SoundControlPanel
	   playingThreads = new Vector<PlayOnceThread>();
	   panelID = ID;
   }
    
   /**
    * Loads a soundscape into the phantom console to hear it
    * @param ssID
    */
   public void previewSoundscapeFromMainGuiPanel(int ssID){
	   Soundscape temp = db.loadSoundscape(ssID,LEFTPANEL);
       preloadSoundscape(temp, LEFTPANEL);
       soundEngine.getPreview().phantom.changeVolume(50);
       soundEngine.getPreview().phantom.startPlayback();
       
   }
    
    /**
     * Loads a sound into the phantom console to preview it
     * @param soundID
     */
    public void previewSoundFromMainGuiPanel(int soundID){
       SoundObject temp = db.getSoundObject(soundID);
       preloadSound(temp, LEFTPANEL);
       soundEngine.preview.phantom.changeVolume(0, 50);
       soundEngine.preview.phantom.startPlayback(0);
   }
    public void stopPreviewingFromMainGuiPanel(){
       try{
          soundEngine.preview.phantom.purgeConsole();
       }
       catch (NullPointerException ex) {}
       
   }
    
//Private Methods
    private void preloadSoundscape(Soundscape newSoundscape,int console){
        
        switch(console){
            case LEFTPANEL:
                try{soundEngine.preview.phantom.preload(newSoundscape);}
                catch(NullPointerException ex){System.out.println("No Previewing available");}
                break;
            case 1:
                soundEngine.stage.consoleOne.preload(newSoundscape);
                try {soundEngine.preview.consoleOne.preload(newSoundscape);}
                catch (NullPointerException ex){System.out.println("No Previewing available");}
                break;
            case 2:
                soundEngine.stage.consoleTwo.preload(newSoundscape);
                try{soundEngine.preview.consoleTwo.preload(newSoundscape);}
                catch (NullPointerException ex){System.out.println("No Previewing available");}
                break;
                
            default:
                System.out.println("Error 406");
        }
    }
    private void preloadSound(SoundObject newSound,int console){
        switch(console){
            case LEFTPANEL:
                try{soundEngine.preview.phantom.preload(newSound);}
                catch(NullPointerException ex){System.out.println("No Previewing available");}
                break;
            case 1:
                soundEngine.stage.consoleOne.preload(newSound);
                try{soundEngine.preview.consoleOne.preload(newSound);}
                catch(NullPointerException ex){System.out.println("No Previewing available");}
                break;
            case 2:
                soundEngine.stage.consoleTwo.preload(newSound);
                try{soundEngine.preview.consoleTwo.preload(newSound);}
                catch(NullPointerException ex){System.out.println("No Previewing available");}
                break;
            default:
                System.out.println("Error 391");
        }
        
    }      
    private void changeVolumeConsole(int newVolume){
            switch(panelID){
                case 1:
                    soundEngine.stage.consoleOne.changeVolume(newVolume);
                    try{soundEngine.preview.consoleOne.changeVolume(newVolume);}
                    catch(NullPointerException ex){System.out.println("No Previewing available");}
                    break;
                case 2:
                    soundEngine.stage.consoleTwo.changeVolume(newVolume);
                    try{soundEngine.preview.consoleTwo.changeVolume(newVolume);}
                    catch(NullPointerException ex){System.out.println("No Previewing available");}
                    break;
            }
        }
    private void changeVolumeSingleSound(int newVolume, int row){
        switch(panelID){
            case 1:
                soundEngine.stage.consoleOne.changeVolume(row,newVolume);
                try{soundEngine.preview.consoleOne.changeVolume(row,newVolume);}
                catch(NullPointerException ex){}
                break;
            case 2:
                soundEngine.stage.consoleTwo.changeVolume(row,newVolume);
                try{soundEngine.preview.consoleTwo.changeVolume(row,newVolume);}
                catch(NullPointerException ex){};
        }
    }
    
    /*todo: still needs preview extension
     *
     */
    private void changeModeSingleSound(int mode, int row){
        switch(panelID){
            case 1:
                soundEngine.stage.consoleOne.changePlaybackMode(row, mode);
                break;
            case 2:
                soundEngine.stage.consoleTwo.changePlaybackMode(row,mode);
                break;
        }
    }
    
    private void startorStopSoundscapeStagePlayback(boolean status){
        switch(panelID){
            case 1:
                if (status==ON)
                    soundEngine.stage.consoleOne.startPlayback();
                else
                    soundEngine.stage.consoleOne.stopPlayback();
            break;
            case 2:
                if (status==ON)
                    soundEngine.stage.consoleTwo.startPlayback();
                else
                    soundEngine.stage.consoleTwo.stopPlayback();
                break;
            default:
                System.out.println("Error 554");
        }
    }
    private void startorStopSoundscapePreviewPlayback(boolean status){
         switch(panelID){
            case 1:
                if (status==ON){
                    try{soundEngine.preview.consoleOne.startPlayback();}
                    catch(NullPointerException ex){}
                }
                else{
                    try{soundEngine.preview.consoleOne.stopPlayback();}
                    catch(NullPointerException ex){}
                }
            break;
            case 2:
                if (status==ON){
                    try{soundEngine.preview.consoleTwo.startPlayback();}
                    catch(NullPointerException ex){}
                }
                else{
                    try{soundEngine.preview.consoleTwo.stopPlayback();}
                    catch(NullPointerException ex){}
                }
                break;
            default:
                System.out.println("Error 654");
        }
        
    }    
//    private void startorStopSoundStagePlayback(int row, boolean status){
//        switch(panelID){
//            case 1:
//                if (status==ON)
//                    soundEngine.stage.consoleOne.startPlayback(row);
//                else
//                    soundEngine.stage.consoleOne.stopPlayback(row);
//                break;
//            case 2:
//                if (status==ON)
//                    soundEngine.stage.consoleTwo.startPlayback(row);
//                else
//                    soundEngine.stage.consoleTwo.stopPlayback(row);
//                break;
//            default:
//                System.out.println("Error 382");
//        }
//    }
//    private void startorStopSoundPreviewPlayback(int row, boolean status){
//        switch(panelID){
//            case 1:
//                if (status==ON){
//                    try{soundEngine.preview.consoleOne.startPlayback(row);}
//                    catch(NullPointerException ex){}
//                }
//                else{
//                    try{soundEngine.preview.consoleOne.stopPlayback(row);}
//                    catch(NullPointerException ex){}
//                }
//                break;
//            case 2:
//                if (status==ON){
//                    try{soundEngine.preview.consoleTwo.startPlayback(row);}
//                    catch(NullPointerException ex){}
//                }    
//                else{
//                    try{soundEngine.preview.consoleTwo.stopPlayback(row);}
//                    catch(NullPointerException ex){}
//                }
//                break;
//            default:
//                System.out.println("Error 382");
//        }
//    }    
    private void stopSoundscapePlayback(){
        startorStopSoundscapeStagePlayback(OFF);
        startorStopSoundscapePreviewPlayback(OFF);
    }    
    
    private void startorStopSingleSoundPlayback(int row, int device, boolean status){
        
        switch(panelID){
            case TOP:
                switch (device){
                    case STAGE:
                        if (status==ON)
                            soundEngine.stage.consoleOne.startPlayback(row);
                        else
                            soundEngine.stage.consoleOne.stopPlayback(row);
                        break;
                    case PREVIEW:
                        if (status==ON){
                            try{soundEngine.preview.consoleOne.startPlayback(row);}
                            catch(NullPointerException ex){}
                        }
                        else{
                            try{soundEngine.preview.consoleOne.stopPlayback(row);}
                            catch(NullPointerException ex){}
                        }
                        break;
                    default:
                        System.out.println("Error 181");
                }
                return;
            case BOTTOM:
                switch (device){
                    case STAGE:
                        if (status==ON)
                            soundEngine.stage.consoleTwo.startPlayback(row);
                        else
                            soundEngine.stage.consoleTwo.stopPlayback(row);
                        break;
                    case PREVIEW:
                        if (status==ON){
                            try{soundEngine.preview.consoleTwo.startPlayback(row);}
                            catch(NullPointerException ex){}
                        }
                        else{
                            try{soundEngine.preview.consoleTwo.stopPlayback(row);}
                            catch(NullPointerException ex){}
                        }
                        break;
                    default:
                        System.out.println("Error 181");
                }
                
        }
    }
    
    private void endSinglePlayThreads() {
    	for (PlayOnceThread thrd: playingThreads){
    		thrd.enabled = false;
    	}
    	playingThreads.clear();
    }
    
    //PUBLIC METHODS
    
    public void sendSoundscapeToPanel(SoundControlPanel scp, int ssID, String Dummy){
        
    	endSinglePlayThreads();
    	
    	if(!scp.evaluateAndSave()){
			return;
    	}
    	
        int currentPanel= scp.getPanelID();
        
        soundscape = db.loadSoundscape(ssID,currentPanel);
        preloadSoundscape(soundscape,currentPanel);
        scp.setPanelName(soundscape.getSSName());
        scp.pushSoundscapeToPanel(soundscape);
        
        scp.masterControl.requestFocusInWindow();
        scp.masterControl.selectMasterRow();
     }    
    
    /** 
     * This handles the delivery of a sound to either the top or bottom console when the 
     * "to console 1" or "to console 2" button is pushed in the main GUI
     */ 
    public void sendSoundToPanel(SoundControlPanel scp, int soundID){
        /*if(scp.getSoundscape().getSoundscapeSoundsCount()>= soundEngine.stage.getLoadLimit()){
            String message = "Ambient sound load limit of " + soundEngine.stage.getLoadLimit() + " exceeded.";
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, message);
            return;
        }*/if(scp.getSoundscape().getSoundscapeSoundsCount()>= 25){
            String message = "Ambient sound load limit of 25 exceeded.";
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, message);
            return;
        }
        
         if (scp.getSoundscape()==null){
            System.out.println("Can't add without opening soundscape first!");}
         else{
            SoundObject newSound=db.getSoundObject(soundID);
            int currentPanel= scp.getPanelID();
            scp.getSoundscape().addSound(newSound);
            preloadSound(newSound, currentPanel);
            scp.addRow(newSound.getName(),newSound.getVolume(),0);
            }
        return; 
    }
    
    public void saveSoundscapeToDatabase(Soundscape soundscape){
        JFrame frame = new JFrame();
        String message;
        if(soundscape.getSoundscapeID()<1){
	        message = "Trying to save a new soundscape";
	        int newSSID = db.saveNewSoundScape(soundscape);
	        soundscape.changeSSID(newSSID);
        }
        else{
            message = "Saving an existing soundscape";
            db.saveExistingSoundscape(soundscape);
            
            JOptionPane.showMessageDialog(frame, message);
        }
    }   
   
    public boolean [][] evaluateListenerResult(int row, int rowCount, int controlPressed, Soundscape soundscape, boolean [][] stateMap){
        System.out.println("evaluateListener row = " + row);
        if (row==MASTERCONTROL){
            switch(controlPressed){
            case PREVIEWBUTTON:
                if(stateMap[STATEMAPROWCOUNT][0]==OFF){
                    for(int stRow=0;stRow<rowCount;stRow++){
                        stateMap[stRow][0]=OFF;
                    }
                }
                stateMap[STATEMAPROWCOUNT][0]=!(stateMap[STATEMAPROWCOUNT][0]);
                startorStopSoundscapePreviewPlayback(stateMap[STATEMAPROWCOUNT][0]);
                
                break;
            case DELETEBUTTON:
                break;
            case ROWSELECTTOGGLE:
                break;
            case PLAYMODEBUTTON:
                
                break;
            case PLAYPAUSEBUTTON:
                stateMap[STATEMAPROWCOUNT][1]=!(stateMap[STATEMAPROWCOUNT][1]);
                startorStopSoundscapeStagePlayback(stateMap[STATEMAPROWCOUNT][1]);
                
                switch(panelID){
                case TOP:
                	//Loop through all sounds playing, for ones in play once mode, start threads
                	//that handle turning the play button off at the end of the sound
                	for(int i=0;i<app.getSoundPanelOne().rowCount;i++){
                		if (stateMap[i][1] == ON && soundscape.getSound(i).getPlaybackType() == SoundObject.SINGLEPLAY) {
                			int length=-1;
                			length = soundEngine.stage.consoleOne.getPlaybackObject(i).soundLength;
                			PlayOnceThread newThread = new PlayOnceThread(i, length);
                        	playingThreads.addElement(newThread);
                        	newThread.start();
                		}
                	}
                	break;
                case BOTTOM:
                	//Loop through all sounds playing, for ones in play once mode, start threads
                	//that handle turning the play button off at the end of the sound
                	for(int i=0;i<app.getSoundPanelTwo().rowCount;i++){
                		if (stateMap[i][1] == ON && soundscape.getSound(i).getPlaybackType() == SoundObject.SINGLEPLAY) {
                			int length=-1;
                			length = soundEngine.stage.consoleTwo.getPlaybackObject(i).soundLength;
                			PlayOnceThread newThread = new PlayOnceThread(i, length);
                        	playingThreads.addElement(newThread);
                        	newThread.start();
                		}
                	}
                	break;
                }
                
                break;
            default:
                break;
            }
        }
        else
        {
            switch(controlPressed){
                case PREVIEWBUTTON:
                    if(stateMap[STATEMAPROWCOUNT][0]==ON){
                        stateMap[STATEMAPROWCOUNT][0]=OFF;
                        startorStopSoundscapePreviewPlayback(OFF);
                    }
                    
                    stateMap[row][0]=!(stateMap[row][0]);
                    startorStopSingleSoundPlayback(row, PREVIEW, stateMap[row][0]);
                    
                    break;
                case DELETEBUTTON:
                    break;
                case ROWSELECTTOGGLE:
                    break;
                case PLAYPAUSEBUTTON:
                    stateMap[row][1]=!(stateMap[row][1]);
                    soundscape.getSound(row).setPlaybackEnabled(stateMap[row][1]);
                    
                    // Only play sounds if the master panel is playing
                    if(stateMap[STATEMAPROWCOUNT][1]==ON){

                        startorStopSingleSoundPlayback(row, STAGE, stateMap[row][1]);
                        
                    	//Start a new thread for sounds in single play mode, it sleeps for the 
                    	//length of the sound and then flips the sound to off. Start the thread
                        //after the sound has started playing.
                    	if(stateMap[row][1] && soundscape.getSound(row).getPlaybackType() == SoundObject.SINGLEPLAY) {
                        	int length=-1;
                        	switch(panelID){
                        	case TOP:
                        		length = soundEngine.stage.consoleOne.getPlaybackObject(row).soundLength;
                        		break;
                        	case BOTTOM:
                        		length = soundEngine.stage.consoleTwo.getPlaybackObject(row).soundLength;
                        		break;
                        	}
                        	PlayOnceThread newThread = new PlayOnceThread(row, length);
                        	playingThreads.addElement(newThread);
                        	newThread.start();
                        }
                    	
                    }
                    
                    if(stateMap[STATEMAPROWCOUNT][0]==ON){
                        startorStopSingleSoundPlayback(row, PREVIEW, stateMap[row][1]);
                    }
                    break;
            default:
                break;
            }
        }
        return stateMap;
    }
    public void loadBlankSoundscape(Soundscape newSoundscape){
        preloadSoundscape(newSoundscape,panelID);
        int presetA = EffectsPanel.NOCHANGE;
        int presetB = EffectsPanel.NOCHANGE;
        if (panelID == 1){
        	presetA = EffectsPanel.FADEIN;
        } else if (panelID == 2){
        	presetB = EffectsPanel.FADEIN;
        }
        app.routeEffectsPanelStates(presetA, presetB);
    }    
    public void purgeSoundscapeInMemory(Soundscape soundscape, int panelID){
        stopSoundscapePlayback();   
    }    
    public void masterVolumeChanged(Soundscape soundscape, int newVolume){
        soundscape.setMasterVolume(newVolume);
        changeVolumeConsole(newVolume);
        System.out.println(newVolume);
    }
    /**
     * Fades in a sound on a console if a soundscape is loaded and is not already playing
     * @param scp {@link SoundControlPanel}
     * @param time time in miliseconds for the fade to take.
     * @throws IllegalStateException if the soundscape is already playing or not loaded
     * 
     * TODO: Bug: When fade out button is pressed, then fade in is pressed, should fade back in. Instead stops sound.
     */
    public void masterVolumeFadeIn(SoundControlPanel scp, int time){
    	boolean hasCurrentFade = currentFade != null;
    	if (hasCurrentFade){
    		currentFade.interrupt();
    	}
    	
    	Soundscape ss = scp.getSoundscape();
    	if(ss == null){
    		throw new IllegalStateException("No soundscape loaded");
    	}
    	switch(this.panelID){
    	case TOP:
    		if (soundEngine.stage.consoleOne.isPlaying() && !hasCurrentFade){
    			throw new IllegalStateException("Soundscape is already playing");
    		}
    		break;
    	case BOTTOM:
    		if (soundEngine.stage.consoleTwo.isPlaying() && !hasCurrentFade){
    			throw new IllegalStateException("Soundscape is already playing");
    		}
    		break;
    	}
    //the interval, in ms, between each point the volume should go down
    	int ssVol = ss.getMasterVolume();
    	int interval = time / ssVol * 5;
    	
    	FadeThread fader = new FadeThread(ssVol, interval, 5, scp, hasCurrentFade);
    	currentFade = fader;
    	fader.start();
    }
    /**
     * Fades out a sound on a console if a soundscape is playing
     * @param scp {@link SoundControlPanel}
     * @param time The time in milliseconds the fade should take
     * @throws IllegalStateException if a soundscape is not playing or not loaded
     */
    public void masterVolumeFadeOut(SoundControlPanel scp, int time){
    	boolean hasCurrentFade = currentFade != null;
    	if (hasCurrentFade){
    		currentFade.interrupt();
    	}
    	
    	Soundscape ss = scp.getSoundscape();
    	if(ss == null){
    		throw new IllegalStateException("No soundscape loaded");
    	}
    	switch(panelID){
    	case TOP:
    		if (!soundEngine.stage.consoleOne.isPlaying()){
    			throw new IllegalStateException("Soundscape is not playing");
    		}
    		break;
    	case BOTTOM:
    		if (!soundEngine.stage.consoleTwo.isPlaying()){
    			throw new IllegalStateException("Soundscape is not playing");
    		}
    		break;
    	}
    //the interval, in ms, between each point the volume should go down
    	int ssVol = ss.getMasterVolume();
    	int interval = time / ssVol * 5;
    	
    	FadeThread fader = new FadeThread(ssVol, interval, -5, scp, hasCurrentFade);
    	currentFade = fader;
    	fader.start();
    }
    
    public void singleSoundVolumeChanged(Soundscape soundscape, int row, int newVolume){
        soundscape.getSound(row).changeVolume(newVolume);
        changeVolumeSingleSound(newVolume, row);
    }
    
    public void singleSoundModeChange(Soundscape soundscape, int row, int mode){
        soundscape.getSound(row).setPlaybackType(mode);
        changeModeSingleSound(mode,row);
    }
    
    public void deleteSingleSoundfromSoundscape(Soundscape soundscape,int row){
        
        switch(panelID){
            case 1:
                soundEngine.stage.consoleOne.stopPlayback(row);
                soundEngine.stage.consoleOne.deletePlaybackObject(row);
                try{
                    soundEngine.preview.consoleOne.stopPlayback(row);
                    soundEngine.preview.consoleOne.deletePlaybackObject(row);
                }
                catch(NullPointerException ex){}
                break;
            case 2:
                soundEngine.stage.consoleTwo.stopPlayback(row);
                soundEngine.stage.consoleTwo.deletePlaybackObject(row);
                try{
                    soundEngine.preview.consoleTwo.stopPlayback(row);
                    soundEngine.preview.consoleTwo.deletePlaybackObject(row);
                }
                catch(NullPointerException ex){}
                break;
            case 3:
                System.out.println("Error 441");
        }
        soundscape.deleteSound(row);
    }    
    public Vector<ResultObject> getSoundscapeSubStringDbResults(String searchString){
        Vector<ResultObject> dbResults = new Vector<ResultObject>();
        db.getSearchPrefixMatch(dbResults,searchString,SOUNDSCAPE);
        return dbResults;
    }
    public Vector<ResultObject> getSoundSubStringDbResults(String searchString){
        Vector<ResultObject> dbResults = new Vector<ResultObject>();
        db.getSearchPrefixMatch(dbResults,searchString,SOUND);
        return dbResults;
    }
    public Vector<ResultObject> getSoundSearchSet(String searchString){
        Vector<ResultObject> dbResults = new Vector<ResultObject>();
        db.getNarrowedSearchResults(dbResults, searchString,SOUND);
        resultObject=dbResults;
        return dbResults;
    }
    public Vector<ResultObject> getSoundscapeSearchSet(String searchString){
        Vector<ResultObject> dbResults = new Vector<ResultObject>();
        db.getNarrowedSearchResults(dbResults, searchString,SOUNDSCAPE);
        resultObject=dbResults;
        return dbResults;
    }
    public int rowSelectToObjectID(int row){
        return resultObject.elementAt(row).resultID;
    }
    
    
//Nested Classes
    
    /**
     * This thread works to keep track of sounds that are in play once mode. After
     * the sound has been playing for the length of the sound clip, this thread
     * sends a click to play pause button so that the button will be repainted as off.
     * 
     * @author ckidwell
     *
     */
    class PlayOnceThread extends Thread{
    	int row;
    	boolean enabled;
    	int sleepTime;
    	
    	public PlayOnceThread(int row, int timer) {
    		this.row = row;
    		this.sleepTime = timer;
    	}
    	
    	public void run() {
			enabled = true;
			try {
				Thread.sleep(sleepTime);

				if(enabled){
					if(panelID == 1){
						System.out.println("Auto stop play on 1:"+row);
						app.getSoundPanelOne().repaintButtons(app.getSoundPanelOne().chief.evaluateListenerResult(row, 
								app.getSoundPanelOne().rowCount, SoundControlPanel.PLAYPAUSEBUTTON, app.getSoundPanelOne().soundscape, 
								app.getSoundPanelOne().stateMap));
					} else if(panelID == 2) {
						System.out.println("Auto stop play on 2:"+row);
						app.getSoundPanelTwo().repaintButtons(app.getSoundPanelTwo().chief.evaluateListenerResult(row, 
								app.getSoundPanelTwo().rowCount, SoundControlPanel.PLAYPAUSEBUTTON, app.getSoundPanelTwo().soundscape, 
								app.getSoundPanelTwo().stateMap));
					}
				}
				
			} catch (InterruptedException exception) {
			}
		}
    	
    	public int getRow(){
    		return row;
    	}
    }
    
	class PreviewButtonIconChanger extends Thread{
	    public PreviewButtonIconChanger(JButton button, int row,int panelID){
	        final int SLEEPTIME = 125;
	        boolean done = false;
	//        boolean isPlaying = true;
	        
	        try{
	            while(!done){
	                switch(panelID){
	                case TOP:
	                if (soundEngine.preview.consoleOne.isPlaying(row)){
	                    button.setIcon(new ImageIcon("/src/Primary/headphones.gif"));
	                    done=true;
	                }
	                break;
	                    case BOTTOM:
	                         if (soundEngine.preview.consoleTwo.isPlaying(row)){
	                    button.setIcon(new ImageIcon("/src/Primary/headphones.gif"));
	                    done=true;
	                }
	                break;
	                    default:
	                        System.out.print("Error 805");
	                }
	                Thread.sleep(SLEEPTIME);
	            }
	        } catch (InterruptedException e){
	            e.printStackTrace();
	        }
	    }
	}

	/**
	 * Thread fades the sound of a particular soundscape
	 * @author Kevin
	 *
	 */
	class FadeThread extends Thread{
		private final int timeInterval;
		private final int volumeInterval;
		private final int originalVolume;
		private final boolean interrupting;
		private int currentVolume;
		private int endVolume;
		private final SoundControlPanel panel;
		
		/**
		 * 
		 * @param soundscapeVolume 
		 * @param timeInterval
		 * @param volumeInterval
		 * @param panel
		 */
		public FadeThread(int soundscapeVolume, int timeInterval, int volumeInterval, SoundControlPanel panel, boolean interrupting){
			if (soundscapeVolume < 0 || timeInterval < 0 || volumeInterval == 0){
				throw new IllegalArgumentException();
			}
			
			this.timeInterval = timeInterval;
			this.volumeInterval = volumeInterval;
			
			if (this.volumeInterval > 0){
				this.currentVolume = 0;
				this.endVolume = soundscapeVolume;
			} else if (this.volumeInterval < 0){
				this.currentVolume = soundscapeVolume;
				this.endVolume = 0;
			}
			this.originalVolume = soundscapeVolume;
			
			this.panel = panel;
			this.interrupting = interrupting;
		}
		
		public FadeThread(int soundscapeVolume, int timeInterval, int volumeInterval, SoundControlPanel panel){
			this(soundscapeVolume, timeInterval, volumeInterval, panel, false);
		}
		
		@Override
		public void run(){
			if (volumeInterval > 0 && !interrupting){
				changeVolumeConsole(currentVolume);
				panel.repaintButtons(panel.chief.evaluateListenerResult(-1, panel.rowCount,
						SoundControlPanel.PLAYPAUSEBUTTON, panel.soundscape, panel.stateMap));
			}
			try {
				while (currentVolume != endVolume){
					currentVolume += volumeInterval;
					if (currentVolume < 0){
						currentVolume = 0;
					} else if (currentVolume > endVolume && volumeInterval >= 0){
						currentVolume = endVolume;
					}
					changeVolumeConsole(currentVolume);
					Thread.sleep(timeInterval);
				}
			} catch (InterruptedException ex){
				ex.printStackTrace();
				return;
			}
			if (volumeInterval < 0){
				changeVolumeConsole(originalVolume);
				panel.repaintButtons(panel.chief.evaluateListenerResult(-1, panel.rowCount,
						SoundControlPanel.PLAYPAUSEBUTTON, panel.soundscape, panel.stateMap));
				
				//clear up single play threads
				endSinglePlayThreads();
			} else {
				// Bug fix - ensure that fade button repaints state
			}
			currentFade = null;
		}
	}
}



