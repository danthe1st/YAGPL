package io.github.danthe1st.yagpl.api;

import java.util.HashMap;
import java.util.Map;

public class GlobalContext implements Context {
	private Map<String, Object> variables=new HashMap<>();
	@Override
	public Object getVariable(String name) {
		return variables.get(name);
	}
	@Override
	public void setVariable(String name,Object value) {
		variables.put(name, value);
	}
	@Override
	public boolean hasVariable(String name) {
		return variables.containsKey(name);
	}
	@Override
	public String toString() {
		return "GlobalContext [variables=" + variables + "]";
	}
}
