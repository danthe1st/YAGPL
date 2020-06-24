package io.github.danthe1st.yagpl.api.concrete.literals;

import io.github.danthe1st.yagpl.api.Expression;

public class GenericLiteral<T, C> extends Expression<T, C> {

	public GenericLiteral(T value) {
		super("lit-"+value, params->value,new Class<?>[0]);
	}
	public GenericLiteral(String name, T value) {
		super(name, params->value,new Class<?>[0]);
	}
}
