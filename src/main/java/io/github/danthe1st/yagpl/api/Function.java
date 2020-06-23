package io.github.danthe1st.yagpl.api;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

//TODO expected arguments? --> names

public class Function<R,C> extends GenericObjectAdapter<R,C>{
	//List of 
	//- action
	//- array of params of that action
	private List<Map.Entry<GenericObject<?,R>,String[]>> operations;

	public Function(String name,List<Map.Entry<GenericObject<?,R>,String[]>> operations) {
		super(name);
		this.operations=operations;
	}

	@Override
	public R execute(FunctionContext<C> outerCtx, Object... params) throws YAGPLException {
		FunctionContext<R> innerCtx=new FunctionContext<>();
		for (int i = 0; i < params.length; i++) {
			innerCtx.setVariable("param"+i, params[i]);
		}
		Iterator<Map.Entry<GenericObject<?,R>,String[]>> it=operations.iterator();
		while(it.hasNext()&&innerCtx.isGoOn()) {
			Map.Entry<GenericObject<?,R>,String[]> next = it.next();
			String[] paramsNamesToPass = next.getValue();
			Object[] paramsToPass=new Object[paramsNamesToPass.length];
			for (int i = 0; i < paramsNamesToPass.length; i++) {
				paramsToPass[i]=innerCtx.getVariable(paramsNamesToPass[i]);
			}
			next.getKey().execute(innerCtx,paramsToPass);
		}
		return innerCtx.getReturn();
	}
}
