package io.github.danthe1st.yagpl.api.concrete.aithmentical;

import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;
import io.github.danthe1st.yagpl.api.util.Resolver;

@StandardElement
public class IncrementBy extends Add{
	//TODO create new class variable in order to pass just the variable, not just the string
	public IncrementBy() {
		this("+=",new Class<?>[] {String.class,Number.class});
	}
	protected IncrementBy(String name,Class<?>[] expectedParameters) {
		super(name,expectedParameters);
	}
	
	@Override
	public Number execute(FunctionContext ctx, Object... params) throws YAGPLException {
		Number ret=super.execute(ctx, Resolver.resolveVariable(ctx, (String) params[0]),params[1]);
		ctx.setVariable((String) params[0], ret);
		return ret;
	}
}
