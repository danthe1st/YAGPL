package io.github.danthe1st.yagpl.api.blocks;

import java.util.ArrayList;
import java.util.List;

import io.github.danthe1st.yagpl.api.Expression;
import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.GenericObject;
import io.github.danthe1st.yagpl.api.OperationBlock;
import io.github.danthe1st.yagpl.api.ParameterizedGenericObject;
import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.api.throwables.IllegalArgumentCountException;
import io.github.danthe1st.yagpl.api.throwables.IllegalArgumentTypeException;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

@StandardElement
public class If extends OperationBlock<Void> {
	public If() {
		this("Loop",new ArrayList<>());
	}
	
	protected If(String name, List<ParameterizedGenericObject<?>> operations) {
		super(name, operations,new Class<?>[] {Expression.class});
	}

	@Override
	public Void execute(FunctionContext ctx, Object... params) throws YAGPLException {
		if(params.length!=1) {
			throw new IllegalArgumentCountException(params.length, 1);
		}
		if(!(params[0] instanceof Expression)) {
			throw new IllegalArgumentTypeException(0, Expression.class, params[0]==null?void.class:params[0].getClass());
		}
		Expression<?> exp=(Expression<?>) params[0];
		while(Boolean.TRUE.equals(exp.execute(ctx, params))&&!Thread.currentThread().isInterrupted()) {
			executeAll(ctx, params);
		}
		return null;
	}
	
	@Override
	public GenericObject<Void> createCopy() throws YAGPLException {
		return super.createCopy();
	}
}
