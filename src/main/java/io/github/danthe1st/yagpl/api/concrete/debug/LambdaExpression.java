package io.github.danthe1st.yagpl.api.concrete.debug;

import io.github.danthe1st.yagpl.api.Expression;
import io.github.danthe1st.yagpl.api.lambdas.SerializableFunction;

public class LambdaExpression<R> extends Expression<R> {

	public LambdaExpression(String name, SerializableFunction<Object[], R> action) {
		super("lambda-e-"+name, action);
	}

}
