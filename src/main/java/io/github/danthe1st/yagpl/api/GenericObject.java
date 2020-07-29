package io.github.danthe1st.yagpl.api;

import java.io.Serializable;

import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

/**
 * 
 * @author Daniel
 *
 * @param <R> return value
 * @param <C> return value of current function
 */
public interface GenericObject<R> extends Serializable{
	String getName();
	Class<?>[] getExpectedParameters();//if null-->any params valid
	R execute(FunctionContext ctx,Object... params) throws YAGPLException;
	GenericObject<R> createCopy() throws YAGPLException;
}
