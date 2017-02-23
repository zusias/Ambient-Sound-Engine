package ase_source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/** Quick sloppy attempt to throw together some command line argument handling
 *  Very incomplete, called from the main function
 *
 * @author Chris Kidwell
 */
public class EnvVariables {
    
    public static String loadFrom;
    public static String saveTo;
    public static boolean debug = false;
    public static Logger logger;
        
    /** Creates a new instance of EnvVariables */
    public EnvVariables() {
    }
    
    /** Inititalizes the saveto and loadfrom settings, saved in settings.log, these two settings are used in the add file to database window (test class)
     */
    public static void initVars(){
        String text;
        try (BufferedReader in = new BufferedReader(new FileReader("settings.log"))){
            text = in.readLine();
            loadFrom = text;
            text = in.readLine();
            saveTo = text;
            text = in.readLine();
            if(text != null){
            	if(text.equalsIgnoreCase("debug")){
            		debug = true;
            	}
            }
            in.close();
        } catch(IOException ioe) {
        	System.out.println("Unable to read settings from settings.log");
            loadFrom = "";
            saveTo = "";
            return;
        }
        
    }
    
    public static void setSaveTo(String _string){
        BufferedWriter out;
        saveTo = _string;

        try{
            out = new BufferedWriter(new FileWriter("settings.log"));
        } catch(IOException ioe) {
            return;
        }
        
        try{
            if (loadFrom != null)
                out.write(loadFrom);
            out.newLine();
            out.write(saveTo);
            out.close();
        }
        catch (IOException ioe){
            
        }
    }
    
    public static void setLoadFrom(String _string){
        BufferedWriter out;
        loadFrom = _string;

        try{
            out = new BufferedWriter(new FileWriter("settings.log"));
        } catch(IOException ioe) {
            return;
        }
        
        try{
            out.write(loadFrom);
            out.newLine();
            if (saveTo != null)
                out.write(saveTo);
            out.close();
        }
        catch (IOException ioe){
            
        }       

    }
    
    /** I'm not quite sure how this code works, it was copy-pasted from the internet for code to handle logging, creates an error log, works fine
     * Still need to direct lots of error messages from the console to here.
     */
    static {
        try{
            boolean append = true;
            FileHandler fh = new FileHandler("TestLog.log", append);
            fh.setFormatter(new SimpleFormatter());
            logger = Logger.getLogger("TestLog");
            logger.addHandler(fh);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static void logMessage(String outString){
            logger.info(outString);
    }
    
}
