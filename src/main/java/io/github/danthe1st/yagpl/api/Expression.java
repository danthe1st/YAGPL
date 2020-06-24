package io.github.danthe1st.yagpl.api;

import java.util.function.Function;

public abstract class Expression<R,C> extends GenericObjectAdapter<R,C>{
	private Function<Object[], R> action;
	protected Expression(String name,Function<Object[], R> action) {
		super(name);
		this.action=action;
	}
	protected Expression(String name,Function<Object[], R> action,Class<?>[] expectedParameters) {
		super(name,expectedParameters);
		this.action=action;
	}

	@Override
	public R execute(FunctionContext<C> ctx, Object... params) {
		return action.apply(params);
	}

}
