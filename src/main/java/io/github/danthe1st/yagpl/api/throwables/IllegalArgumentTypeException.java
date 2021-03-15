package io.github.danthe1st.yagpl.api.throwables;

import io.github.danthe1st.yagpl.api.FunctionContext;

public class IllegalArgumentTypeException extends YAGPLException{
	private static final long serialVersionUID = -6082080424776035201L;

	public IllegalArgumentTypeException(FunctionContext ctx,int argumentNumber,Class<?> expectedType,Class<?> actualType) {
		super(ctx,"argument "+argumentNumber+" should be of type "+expectedType+" but is "+actualType);
	}
	

}
