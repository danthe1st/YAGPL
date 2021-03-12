package io.github.danthe1st.yagpl.api.concrete.bool;

import java.util.Objects;

import io.github.danthe1st.yagpl.api.Expression;
import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

//checks object equality
@StandardElement
public class Equality extends Expression<Boolean>{

	public Equality() {
		super("==", null, new Class[] {Object.class,Object.class});
	}
	
	@Override
	public Boolean execute(FunctionContext ctx, Object... params) throws YAGPLException {
		return Objects.equals(params[0], params[1]);
	}

}
