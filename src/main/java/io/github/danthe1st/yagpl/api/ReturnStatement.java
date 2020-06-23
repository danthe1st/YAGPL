package io.github.danthe1st.yagpl.api;

public class ReturnStatement<R,C> extends Statement<C>{
	private Expression<C,C> loader;
	public ReturnStatement(String name, Expression<C,C> action) {
		super(name, null);
		this.loader=action;
	}
	@Override
	public Void execute(FunctionContext<C> ctx, Object... params) {
		ctx.doReturn(loader.execute(ctx, params));
		return null;
	}
}
