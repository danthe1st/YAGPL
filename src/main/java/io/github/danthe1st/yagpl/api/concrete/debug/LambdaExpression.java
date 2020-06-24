package io.github.danthe1st.yagpl.api.concrete.debug;

import java.util.function.Function;

import io.github.danthe1st.yagpl.api.Expression;

public class LambdaExpression<R, C> extends Expression<R, C> {

	public LambdaExpression(String name, Function<Object[], R> action) {
		super("lambda-e-"+name, action);
	}

}
