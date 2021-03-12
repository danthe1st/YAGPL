package io.github.danthe1st.yagpl.api.concrete.aithmentical;

import io.github.danthe1st.yagpl.api.Expression;
import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

@StandardElement
public class Multiply extends Expression<Number> {
	public Multiply() {
		this("*",new Class<?>[] { Number.class,Number.class });
	}
	protected Multiply(String name,Class<?>[] expectedParameters) {
		super(name, null, expectedParameters);
	}

	@Override
	public Number execute(FunctionContext ctx, Object... params) throws YAGPLException {
		Number ret;
		Number a = (Number) params[0];
		Number b = (Number) params[1];
		// https://stackoverflow.com/questions/2721390/how-to-add-two-java-lang-numbers
		if (a instanceof Double || b instanceof Double) {
			ret = a.doubleValue() * b.doubleValue();
		} else if (a instanceof Float || b instanceof Float) {
			ret = a.floatValue() * b.floatValue();
		} else if (a instanceof Long || b instanceof Long) {
			ret = a.longValue() * b.longValue();
		} else {
			ret = a.intValue() * b.intValue();
			// TODO maybe allow smaller types-->fix problems with out of bounds when using
			// java compiler (not yet implemented)..?
			// and...smaller types are not implemented in any case
		}
		return ret;
	}
}
