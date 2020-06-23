package io.github.danthe1st.yagpl.api;

import java.util.function.Function;

public class Expression<R,C> extends GenericObjectAdapter<R,C>{
	private Function<Object[], R> action;
	public Expression(String name,Function<Object[], R> action) {
		super(name);
		this.action=action;
	}

	@Override
	public R execute(FunctionContext<C> ctx, Object... params) {
		return action.apply(params);
	}

}
