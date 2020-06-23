package io.github.danthe1st.yagpl.api;

import io.github.danthe1st.yagpl.api.throwables.IllegalArgumentCountException;

public class Assignment<C> extends GenericObjectAdapter<Void, C>{

	private String variableName;
	
	public Assignment(String name,String variableToAssign) {
		super(name);
		variableName=variableToAssign;
	}

	@Override
	public Void execute(FunctionContext<C> ctx, Object... params) throws IllegalArgumentCountException {
		if(params.length!=1) {
			throw new IllegalArgumentCountException(params.length,1);
		}
		ctx.setVariable(variableName, params[0]);
		return null;
	}
}
