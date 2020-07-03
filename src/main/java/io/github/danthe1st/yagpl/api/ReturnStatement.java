package io.github.danthe1st.yagpl.api;

import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

public class ReturnStatement<R,C> extends Statement<C>{
	private Expression<C,C> loader;
	public ReturnStatement(Expression<C,C> action) {
		super("return", null,new Class<?>[] {Object.class});
		this.loader=action;
	}
	@Override
	public Void execute(FunctionContext<C> ctx, Object... params) throws YAGPLException {
		ctx.doReturn(loader.execute(ctx, params));
		return null;
	}
}
