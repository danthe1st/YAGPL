package io.github.danthe1st.yagpl.api.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.GenericObject;
import io.github.danthe1st.yagpl.api.OperationBlock;
import io.github.danthe1st.yagpl.api.ParameterizedGenericObject;
import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

//TODO expected arguments? --> names
@StandardElement
public class Function<R> extends OperationBlock<R>{
	public Function() {
		this("Function",new ArrayList<>());
	}
	public Function(String name,List<ParameterizedGenericObject<?>> operations) {
		super("func-"+name,operations);
	}
	public Function(String name,List<ParameterizedGenericObject<?>> operations,Class<?>[] expectedParameters) {
		super("func-"+name,operations,expectedParameters);
	}

	@Override
	public R execute(FunctionContext outerCtx, Object... params) throws YAGPLException {
		FunctionContext innerCtx = new FunctionContext(outerCtx);
		for (int i = 0; i < params.length; i++) {
			innerCtx.setVariable("param" + i, params[i]);
		}
		executeAll(outerCtx, params);
		return (R) innerCtx.getReturn();//TODO check with instanceof or...whatever
	}
//	public R executeAndReturn(FunctionContext outerCtx, Object... params) {
//		FunctionContext innerCtx = new FunctionContext(outerCtx);
//		for (int i = 0; i < params.length; i++) {
//			innerCtx.setVariable("param" + i, params[i]);
//		}
//		//executeAll(innerCtx, params);
//		return innerCtx.getReturn();
//	}
	@Override
	public GenericObject<R> createCopy() throws YAGPLException {
		@SuppressWarnings("unchecked")
		Function<R> copy=(Function<R>) super.createCopy();
		copy.setName("func-"+UUID.randomUUID());
		return copy;
	}
}
