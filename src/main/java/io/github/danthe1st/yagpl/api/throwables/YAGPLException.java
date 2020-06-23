package io.github.danthe1st.yagpl.api.throwables;

public class YAGPLException extends Exception{
	private static final long serialVersionUID = -4080281387365480014L;

	public YAGPLException() {
		super();
	}

	public YAGPLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public YAGPLException(String message, Throwable cause) {
		super(message, cause);
	}

	public YAGPLException(String message) {
		super(message);
	}

	public YAGPLException(Throwable cause) {
		super(cause);
	}
	
	
}
