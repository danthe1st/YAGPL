package io.github.danthe1st.yagpl.api;

import java.util.function.Consumer;

import io.github.danthe1st.yagpl.api.lambdas.SerializableConsumer;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

public abstract class Statement<C> extends GenericObjectAdapter<Void,C>{
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
	public Void execute(FunctionContext<C> ctx, Object... params) throws YAGPLException {
		action.accept(params);
		return null;
	}
	
}
