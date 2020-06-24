package io.github.danthe1st.yagpl.api.util;

import java.util.HashMap;
import java.util.Map;

import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.throwables.NotResolveableException;

public class Resolver {
	//TODO keywords
	private static Map<Character, LambdaResolver> resolvers=new HashMap<>();
	static {
		resolvers.put('"',toResolve->{
			if(toResolve.endsWith("\"")) {
				return toResolve.substring(1, toResolve.length()-1);
			}
			throw new NotResolveableException(toResolve);
		});
	}
	public static Object resolveVariable(FunctionContext<?> ctx,String toResolve) throws NotResolveableException {
		Object ret=null;
		char firstChar=toResolve.charAt(0);
		LambdaResolver res = resolvers.get(firstChar);
		if(res==null) {
			ret=ctx.getVariable(toResolve);
		}else {
			ret=res.resolve(toResolve);
		}
		return ret;
	}
	@FunctionalInterface
	private interface LambdaResolver{
		Object resolve(String toResolve) throws NotResolveableException;
	}
}
