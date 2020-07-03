package io.github.danthe1st.yagpl.api;

import java.util.Iterator;
import java.util.List;

import io.github.danthe1st.yagpl.api.throwables.YAGPLException;
import io.github.danthe1st.yagpl.api.util.Resolver;

//TODO expected arguments? --> names

public class Function<R,C> extends GenericObjectAdapter<R,C>{
	//List of 
	//- action
	//- array of params of that action
	private List<ParameterizedGenericObject<?, R>> operations;

	public Function(String name,List<ParameterizedGenericObject<?, R>> operations) {
		super("func-"+name);
		this.operations=operations;
	}
	public Function(String name,List<ParameterizedGenericObject<?, R>> operations,Class<?>[] expectedParameters) {
		super("func-"+name,expectedParameters);
		this.operations=operations;
	}

	@Override
	public R execute(FunctionContext<C> outerCtx, Object... params) throws YAGPLException {
		FunctionContext<R> innerCtx=new FunctionContext<>(outerCtx);
		for (int i = 0; i < params.length; i++) {
			innerCtx.setVariable("param"+i, params[i]);
		}
		Iterator<ParameterizedGenericObject<?, R>> it=operations.iterator();
		while(it.hasNext()&&innerCtx.isGoOn()) {
			ParameterizedGenericObject<?, R> next = it.next();
			String[] paramsNamesToPass = next.getParams();
			Object[] paramsToPass=new Object[paramsNamesToPass.length];
			for (int i = 0; i < paramsNamesToPass.length; i++) {
				paramsToPass[i]=Resolver.resolveVariable(innerCtx, paramsNamesToPass[i]);
			}
			next.getObj().execute(innerCtx,paramsToPass);
		}
		return innerCtx.getReturn();
	}
	public List<ParameterizedGenericObject<?, R>> getOperations() {
		return operations;
	}
	public void setOperations(List<ParameterizedGenericObject<?, R>> operations) {//TODO not use that in UI but change it manually
		this.operations=operations;
	}
	
}
