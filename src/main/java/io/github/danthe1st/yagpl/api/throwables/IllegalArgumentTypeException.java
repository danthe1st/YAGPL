package io.github.danthe1st.yagpl.api.throwables;

public class IllegalArgumentTypeException extends YAGPLException{
	private static final long serialVersionUID = -6082080424776035201L;

	public IllegalArgumentTypeException(int argumentNumber,Class<?> expectedType,Class<?> actualType) {
		super("argument "+argumentNumber+" should be of type "+expectedType+" but is "+actualType);
	}
	

}
