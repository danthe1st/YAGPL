package io.github.danthe1st.yagpl.api;

import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

public class ReturnStatement<Void> extends Statement{
	private Expression<?> loader;
	public ReturnStatement(Expression<?> action) {
		super("return", null,new Class<?>[] {Object.class});
		this.loader=action;
	}
	@Override
	public java.lang.Void execute(FunctionContext ctx, Object... params) throws YAGPLException {
		ctx.doReturn(loader.execute(ctx, params));
		return null;
	}
}
