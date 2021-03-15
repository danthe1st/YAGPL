package io.github.danthe1st.yagpl.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.danthe1st.yagpl.api.concrete.ResolveableExpression;
import io.github.danthe1st.yagpl.api.constant.ConstantExpression;
import io.github.danthe1st.yagpl.api.throwables.IllegalArgumentTypeException;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;
import io.github.danthe1st.yagpl.api.util.Resolver;

public abstract class OperationBlock<R> extends GenericObjectAdapter<R> {
	// List of
	// - action
	// - array of params of that action
	private List<ParameterizedGenericObject<?>> operations;

	protected OperationBlock(String name, List<ParameterizedGenericObject<?>> operations) {
		super(name);
		this.operations = operations;
	}

	public OperationBlock(String name, List<ParameterizedGenericObject<?>> operations, Class<?>[] expectedParameters) {
		super(name, expectedParameters);
		this.operations = operations;
	}

	public Void executeAll(FunctionContext ctx, Object... params) throws YAGPLException {
		Iterator<ParameterizedGenericObject<?>> it = operations.iterator();
		while (it.hasNext() && ctx.isGoOn()) {
			ParameterizedGenericObject<?> next = it.next();
			String[] paramsNamesToPass = next.getParams();
			Object[] paramsToPass = new Object[paramsNamesToPass.length];
			Class<?>[] expectedParams=next.getObj().getExpectedParameters();
			ctx.setCurrentOperation(next.getObj());
			for (int i = 0; i < paramsNamesToPass.length; i++) {
				paramsToPass[i] = Resolver.resolveVariable(ctx, paramsNamesToPass[i]);
				if(paramsToPass[i]!=null) {
					Class<?> expectedParam=expectedParams[i];
					if(expectedParam!=null&&!expectedParam.isInstance(paramsToPass[i])) {
						if(expectedParam.isAssignableFrom(Expression.class)) {
							paramsToPass[i]=new ResolveableExpression(paramsNamesToPass[i]);
						}else {
							throw new IllegalArgumentTypeException(ctx,i, expectedParam, paramsToPass[i].getClass());
						}
					}
				}
			}
			Object returnValue = next.getObj().execute(ctx, paramsToPass);
			ctx.setVariable("$?", returnValue);
		}
		return null;
	}

	public List<ParameterizedGenericObject<?>> getOperations() {
		return operations;
	}

	public void setOperations(List<ParameterizedGenericObject<?>> operations) {// TODO not use that in UI but change it manually
		this.operations = operations;
	}

	@Override
	public GenericObject<R> createCopy() throws YAGPLException {
		OperationBlock<R> copy = (OperationBlock<R>) super.createCopy();
		copy.operations = new ArrayList<>(operations);
		return copy;
	}
}
