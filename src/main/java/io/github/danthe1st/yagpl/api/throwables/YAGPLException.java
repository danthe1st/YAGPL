package io.github.danthe1st.yagpl.api.throwables;

import java.lang.StackWalker.StackFrame;
import java.lang.invoke.MethodType;
import java.lang.module.ModuleDescriptor;
import java.util.ArrayList;
import java.util.List;

import io.github.danthe1st.yagpl.api.Context;
import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.GenericObject;

public class YAGPLException extends Exception {
	private static final long serialVersionUID = -4080281387365480014L;
	
	private FunctionContext ctx;

	public YAGPLException(FunctionContext ctx) {
		super();
		this.ctx=ctx;
		fillInStackTrace();
	}
	
	public YAGPLException() {
		super();
	}

	public YAGPLException(FunctionContext ctx,String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.ctx=ctx;
		fillInStackTrace();
	}

	public YAGPLException(FunctionContext ctx,String message, Throwable cause) {
		super(message, cause);
		this.ctx=ctx;
		fillInStackTrace();
	}

	public YAGPLException(FunctionContext ctx,String message) {
		super(message);
		this.ctx=ctx;
		fillInStackTrace();
	}
	
	public YAGPLException(String message) {
		super(message);
	}

	public YAGPLException(FunctionContext ctx,Throwable cause) {
		super(cause);
		this.ctx=ctx;
		fillInStackTrace();
	}
	
	public YAGPLException(Throwable cause) {
		super(cause);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		FunctionContext context=this.ctx;
		List<StackTraceElement> stackTraceElements=new ArrayList<>();
		while(context!=null) {
			GenericObject<?> operation = ctx.getCurrentOperation();
			if(operation!=null) {
				stackTraceElements.add(new StackTraceElement("Main", operation.getName(), operation.getClass().getCanonicalName(), 0));
			}
			context=context.getParentCtx();
		}
		setStackTrace(stackTraceElements.toArray(StackTraceElement[]::new));
		
		return this;
	}
	
}
