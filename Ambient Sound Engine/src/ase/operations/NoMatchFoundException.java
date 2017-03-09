package ase.operations;

public class NoMatchFoundException extends IllegalArgumentException {
	private static final long serialVersionUID = -38228281073252193L;

	public NoMatchFoundException() {
		super();
	}

	public NoMatchFoundException(String message) {
		super(message);
	}

	public NoMatchFoundException(Throwable cause) {
		super(cause);
	}

	public NoMatchFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
