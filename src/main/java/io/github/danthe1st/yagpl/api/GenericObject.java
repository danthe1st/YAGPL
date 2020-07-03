package io.github.danthe1st.yagpl.api;

import java.io.Serializable;

import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

/**
 * 
 * @author Daniel
 *
 * @param <R> return value
 * @param <CR> return value of current function
 */
public interface GenericObject<R,C> extends Serializable{
	String getName();
	Class<?>[] getExpectedParameters();//if null-->any params valid
	R execute(FunctionContext<C> ctx,Object... params) throws YAGPLException;
	<T>GenericObject<R,T> createCopy() throws YAGPLException;
}
