package io.github.danthe1st.yagpl.api.throwables;

import io.github.danthe1st.yagpl.api.Context;
import io.github.danthe1st.yagpl.api.FunctionContext;

public class NotResolveableException extends YAGPLException {
	private static final long serialVersionUID = -8145913142060705703L;

	public NotResolveableException(String toResolve) {
		super("\"" + toResolve + "\" cannot be resolved.");
	}
	
	public NotResolveableException(FunctionContext ctx, String toResolve) {
		super(ctx, "\"" + toResolve + "\" cannot be resolved.");
	}

	public NotResolveableException(FunctionContext ctx) {
		super(ctx);
	}
	
	public NotResolveableException() {
		super();
	}

	public NotResolveableException(FunctionContext ctx, String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(ctx, message, cause, enableSuppression, writableStackTrace);
	}

	public NotResolveableException(FunctionContext ctx, String message, Throwable cause) {
		super(ctx, message, cause);
	}

	public NotResolveableException(FunctionContext ctx, Throwable cause) {
		super(ctx, cause);
	}
	
	public NotResolveableException(Throwable cause) {
		super(cause);
	}
}
