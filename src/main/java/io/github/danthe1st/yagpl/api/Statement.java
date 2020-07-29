package io.github.danthe1st.yagpl.api;

import io.github.danthe1st.yagpl.api.lambdas.SerializableConsumer;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

public abstract class Statement extends GenericObjectAdapter<Void>{
	private SerializableConsumer<Object[]> action;

	public Statement(String name, SerializableConsumer<Object[]> action) {
		super(name);
		this.action = action;
	}
	public Statement(String name, SerializableConsumer<Object[]> action,Class<?>[] expectedParameters) {
		super(name,expectedParameters);
		this.action = action;
	}

	@Override
	public Void execute(FunctionContext ctx, Object... params) throws YAGPLException {
		action.accept(params);
		return null;
	}
}
