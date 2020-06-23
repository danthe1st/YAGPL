package io.github.danthe1st.yagpl.api.throwables;

public class IllegalArgumentCountException extends YAGPLException{
	private static final long serialVersionUID = -6681199236354060843L;

	public IllegalArgumentCountException(int actual,int expected) {
		this(actual+" arguments given, expected: "+expected);
	}
	
	public IllegalArgumentCountException() {
		super();
	}

	public IllegalArgumentCountException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IllegalArgumentCountException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalArgumentCountException(String message) {
		super(message);
	}

	public IllegalArgumentCountException(Throwable cause) {
		super(cause);
	}
	
}
