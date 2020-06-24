package io.github.danthe1st.yagpl.api.concrete.debug;

import java.util.function.Consumer;

import io.github.danthe1st.yagpl.api.Statement;

public class LambdaStatement<C> extends Statement<C> {

	public LambdaStatement(String name, Consumer<Object[]> action) {
		super("lambda-stmt-"+name, action);
	}

}
