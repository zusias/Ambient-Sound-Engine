package ase_source;

/**
 *
 * @author Lance
 */
public class ResultObject {
    public int resultID;
    public String primaryResultString;
    public String secondaryResultString;
    
    /** Creates a new instance of resultObject */
    public ResultObject(int ID, String string){
        resultID=ID;
        primaryResultString = string;
    }
    public ResultObject(String string){
        secondaryResultString = string;
    }
    
}
