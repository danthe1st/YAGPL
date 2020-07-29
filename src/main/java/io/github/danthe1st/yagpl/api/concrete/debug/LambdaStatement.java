package io.github.danthe1st.yagpl.api.concrete.debug;

import io.github.danthe1st.yagpl.api.Statement;
import io.github.danthe1st.yagpl.api.lambdas.SerializableConsumer;

public class LambdaStatement extends Statement {

	public LambdaStatement(String name, SerializableConsumer<Object[]> action) {
		super("lambda-stmt-"+name, action);
	}

}
