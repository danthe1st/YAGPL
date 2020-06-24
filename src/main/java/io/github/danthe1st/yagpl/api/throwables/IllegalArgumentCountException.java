package io.github.danthe1st.yagpl.api.throwables;

public class IllegalArgumentCountException extends YAGPLException{
	private static final long serialVersionUID = -6681199236354060843L;

	public IllegalArgumentCountException(int actual,int expected) {
		super(actual+" arguments given, expected: "+expected);
	}
}
