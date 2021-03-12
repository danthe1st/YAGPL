package io.github.danthe1st.yagpl.api.concrete.aithmentical;

import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

@StandardElement
public class Increment extends IncrementBy{
	//TODO create new class variable in order to pass just the variable, not just the string
	public Increment() {
		super("++",new Class<?>[] {String.class});
	}
	
	@Override
	public Number execute(FunctionContext ctx, Object... params) throws YAGPLException {
		return super.execute(ctx, params[0],1);
	}
}
