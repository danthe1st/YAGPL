package io.github.danthe1st.yagpl.api;

import java.util.function.Consumer;

public class Statement<C> extends GenericObjectAdapter<Void,C>{
	private Consumer<Object[]> action;

	public Statement(String name, Consumer<Object[]> action) {
		super(name);
		this.action = action;
	}

	@Override
	public Void execute(FunctionContext<C> ctx, Object... params) {
		action.accept(params);
		return null;
	}
	
}
