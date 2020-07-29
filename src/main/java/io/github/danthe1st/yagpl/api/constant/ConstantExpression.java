package io.github.danthe1st.yagpl.api.constant;

import io.github.danthe1st.yagpl.api.Expression;
import io.github.danthe1st.yagpl.api.lambdas.SerializableFunction;

public class ConstantExpression<R> extends Expression<R> {

	public ConstantExpression(R value) {
		super("constant expression", ignored->value);
	}

}
