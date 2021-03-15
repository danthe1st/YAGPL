package io.github.danthe1st.yagpl.api.throwables;

import io.github.danthe1st.yagpl.api.FunctionContext;

public class IllegalArgumentCountException extends YAGPLException{
	private static final long serialVersionUID = -6681199236354060843L;

	public IllegalArgumentCountException(FunctionContext ctx,int actual,int expected) {
		super(ctx,actual+" arguments given, expected: "+expected);
	}
}
