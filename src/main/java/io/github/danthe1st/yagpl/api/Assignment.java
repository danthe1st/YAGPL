package io.github.danthe1st.yagpl.api;

import io.github.danthe1st.yagpl.api.throwables.IllegalArgumentCountException;
import io.github.danthe1st.yagpl.api.throwables.IllegalArgumentTypeException;
@StandardElement
public class Assignment<C> extends GenericObjectAdapter<Void, C>{

	public Assignment() {
		super("assign",new Class<?>[] {String.class,Object.class});
	}

	@Override
	public Void execute(FunctionContext<C> ctx, Object... params) throws IllegalArgumentCountException, IllegalArgumentTypeException {
		if(params.length!=2) {
			throw new IllegalArgumentCountException(params.length,1);
		}
		if(params[0] instanceof String) {
			ctx.setVariable((String)params[0], params[1]);
		}else {
			throw new IllegalArgumentTypeException(0, String.class, params[0].getClass());
		}
		return null;
	}
}
