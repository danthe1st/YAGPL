package io.github.danthe1st.yagpl.api.concrete.bool;

import io.github.danthe1st.yagpl.api.Expression;
import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

@StandardElement
public class Not extends Expression<Boolean>{

	public Not() {
		super("!",null, new Class<?>[] {Boolean.class});
	}
	
	@Override
	public Boolean execute(FunctionContext ctx, Object... params) throws YAGPLException {
		return !((Boolean)params[0]);
	}
}
