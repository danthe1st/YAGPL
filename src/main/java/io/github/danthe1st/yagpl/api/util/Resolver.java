package io.github.danthe1st.yagpl.api.util;

import java.util.HashMap;
import java.util.Map;

import io.github.danthe1st.yagpl.api.Context;
import io.github.danthe1st.yagpl.api.throwables.NotResolveableException;

public class Resolver {
	//TODO keywords like null
	private static Map<Character, LambdaResolver> resolvers = new HashMap<>();
	private static Map<String, Object> keywordResolvers = new HashMap<>();
	static {
		resolvers.put('"', toResolve -> {
			if (toResolve.endsWith("\"")) {
				return toResolve.substring(1, toResolve.length() - 1);
			}
			throw new NotResolveableException(toResolve);
		});
		for (char i = '0'; i <= '9'; i++) {
			resolvers.put(i, toResolve -> {
				try {
					if (toResolve.endsWith("l") || toResolve.endsWith("L")) {
						return Long.valueOf(toResolve.substring(0, toResolve.length() - 1));
					} else {
						return Integer.valueOf(toResolve);
					}
				} catch (NumberFormatException e) {
					throw new NotResolveableException(e);
				}
			});
		}
		keywordResolvers.put("null", null);
		keywordResolvers.put("true", true);
		keywordResolvers.put("false", false);
	}

	public static Object resolveVariable(Context ctx, String toResolve) throws NotResolveableException {
		if (toResolve == null || toResolve.isEmpty()) {
			throw new NotResolveableException(toResolve);
		}
		Object ret = null;
		char firstChar = toResolve.charAt(0);
		LambdaResolver res = resolvers.get(firstChar);
		if (res == null) {
			if (keywordResolvers.containsKey(toResolve)) {
				ret = keywordResolvers.get(toResolve);
			} else if (ctx != null && ctx.hasVariable(toResolve)) {
				ret = ctx.getVariable(toResolve);
			} else {
				throw new NotResolveableException(toResolve);
			}
		} else {
			ret = res.resolve(toResolve);
		}
		return ret;
	}

	@FunctionalInterface
	private interface LambdaResolver {
		Object resolve(String toResolve) throws NotResolveableException;
	}
}
