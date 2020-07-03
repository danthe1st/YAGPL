package io.github.danthe1st.yagpl.api.concrete.debug;

import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

@StandardElement
public class DecrementBy<C> extends IncrementBy<C>{
	private Multiply<C> multiplier=new Multiply<>();
	//TODO create new class variable in order to pass just the variable, not just the string
	public DecrementBy() {
		this("-=",new Class<?>[] {String.class,Number.class});
	}
	protected DecrementBy(String name,Class<?>[] expectedParameters) {
		super(name,expectedParameters);
	}
	
	@Override
	public Number execute(FunctionContext<C> ctx, Object... params) throws YAGPLException {
		return super.execute(ctx, params[0],multiplier.execute(ctx, params[1],-1));
	}
}
