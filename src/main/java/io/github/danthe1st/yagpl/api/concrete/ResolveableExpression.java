package io.github.danthe1st.yagpl.api.concrete;

import io.github.danthe1st.yagpl.api.Expression;
import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.lambdas.SerializableFunction;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;
import io.github.danthe1st.yagpl.api.util.Resolver;

public class ResolveableExpression<R> extends Expression<R>{

	public ResolveableExpression(String name) {
		super(name, null, new Class<?>[] {});
	}
	
	@Override
	public R execute(FunctionContext ctx, Object... params) throws YAGPLException {
		return (R) Resolver.resolveVariable(ctx, getName());
	}

}
