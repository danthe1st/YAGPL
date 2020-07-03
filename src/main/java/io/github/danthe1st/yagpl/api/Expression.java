package io.github.danthe1st.yagpl.api;

import java.util.function.Function;

import io.github.danthe1st.yagpl.api.lambdas.SerializableFunction;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

public abstract class Expression<R,C> extends GenericObjectAdapter<R,C>{
	private SerializableFunction<Object[], R> action;
	protected Expression(String name,SerializableFunction<Object[], R> action) {
		super(name);
		this.action=action;
	}
	protected Expression(String name,SerializableFunction<Object[], R> action,Class<?>[] expectedParameters) {
		super(name,expectedParameters);
		this.action=action;
	}

	@Override
	public R execute(FunctionContext<C> ctx, Object... params) throws YAGPLException {
		return action.apply(params);
	}

}
