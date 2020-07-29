package io.github.danthe1st.yagpl.api.concrete.debug;

import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

@StandardElement
public class Substract extends Add{
	private Multiply multiplier=new Multiply();
	public Substract() {
		super("-",new Class<?>[] {String.class,String.class});
	}
	
	@Override
	public Number execute(FunctionContext ctx, Object... params) throws YAGPLException {
		return super.execute(ctx, params[0],multiplier.execute(ctx, params[1],-1));
	}
}
