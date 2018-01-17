package ase.operations;

/**
 * Basic logging class. Implements 3 levels of logging:
 * <ul>
 * 	<li>Debug</li>
 * 	<li>Production</li>
 * 	<li>Development</li>
 * </ul>
 * 
 * Right now, logs are printed to Standard Out. Will change to out stream so that
 * it can be set by the programmer
 * @author KevinCGall
 *
 */
public class Log {
	public static enum LogLevel{
		DEBUG, DEV, PROD
	}
	
	private final LogLevel lv;
	
	public Log(LogLevel lv){
		this.lv = lv;
	}
	
	public void log(LogLevel lv, String message){
		if (!shouldLog(lv)) {return;}
		
		System.out.println(message);
	}
	
	public void log(LogLevel lv, StackTraceElement[] stackTrace) {
		if (!shouldLog(lv)) {return;}
		
		for (StackTraceElement stackLine : stackTrace) {
			System.out.println(stackLine.toString());
		}
	}
	
	public void logError(String prodMessage, Exception ex) {
		log(LogLevel.PROD, prodMessage);
		log(LogLevel.DEV, ex.getMessage());
		log(LogLevel.DEBUG, ex.getStackTrace());
	}
	
	private boolean shouldLog(LogLevel lv) {
		if (this.lv == LogLevel.PROD && lv != LogLevel.PROD){
			return false;
		}
		if (this.lv == LogLevel.DEV && lv == LogLevel.DEBUG) {
			return false;
		}
		
		return true;
	}
}
