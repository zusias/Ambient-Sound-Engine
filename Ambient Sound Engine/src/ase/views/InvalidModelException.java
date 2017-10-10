package ase.views;

/**
 * Indicates programmer error: the model is out of sync with
 * the views in an unrecoverable way
 * @author Kevin C. Gall
 *
 */
public class InvalidModelException extends RuntimeException {
	private static final long serialVersionUID = 8549803609198226465L;
	
	public InvalidModelException() {super();}
	public InvalidModelException(String message) {super(message);}
	public InvalidModelException(Throwable cause) {super(cause);}
	public InvalidModelException(String message, Throwable cause) {super(message, cause);}
}
