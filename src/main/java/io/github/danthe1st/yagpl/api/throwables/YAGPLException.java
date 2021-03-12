package io.github.danthe1st.yagpl.api.throwables;

import java.lang.StackWalker.StackFrame;
import java.lang.invoke.MethodType;
import java.lang.module.ModuleDescriptor;
import java.util.ArrayList;
import java.util.List;

import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.GenericObject;

public class YAGPLException extends Exception {
	private static final long serialVersionUID = -4080281387365480014L;

	public YAGPLException() {
		super();
	}

	public YAGPLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public YAGPLException(String message, Throwable cause) {
		super(message, cause);
	}

	public YAGPLException(String message) {
		super(message);
	}

	public YAGPLException(Throwable cause) {
		super(cause);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		StackWalker stackWalker=StackWalker.getInstance();
		setStackTrace(stackWalker.walk(frames->frames
				.filter(YAGPLException::isExecuteFrame)
				.map(YAGPLException::convertStackFrameToStackTraceElement)
				.toArray(StackTraceElement[]::new)
		));
		return this;
	}
	private static boolean isExecuteFrame(StackFrame frame) {
		
		if(!frame.getDeclaringClass().isAssignableFrom(GenericObject.class)) {
			return false;
		}
		if(!"execute".equals(frame.getMethodName())) {
			return false;
		}
		MethodType methodType = frame.getMethodType();
		if(methodType.parameterCount()==0) {
			return false;
		}
		return FunctionContext.class.isAssignableFrom(methodType.parameterArray()[0]);
	}
	private static StackTraceElement convertStackFrameToStackTraceElement(StackFrame frame) {
		return new StackTraceElement(
				frame.getDeclaringClass().getClassLoader().getName(),
				frame.getDeclaringClass().getModule().getName(),
				frame.getDeclaringClass().getModule().getDescriptor().version().map(Object::toString).orElse(null),
				frame.getClassName(),
				frame.getMethodName(),
				frame.getFileName(),
				frame.getLineNumber());
	}

}
