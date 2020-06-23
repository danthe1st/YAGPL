package io.github.danthe1st.yagpl.api;

import java.util.HashMap;
import java.util.Map;

public class FunctionContext<R> {
	private Map<String, Object> variables=new HashMap<>();
	private R ret=null;
	private boolean goOn=true;
	public R getReturn() {
		return ret;
	}
	public void doReturn(R ret) {
		this.ret=ret;
		goOn=false;
	}
	public boolean isGoOn() {
		return goOn;
	}
	public Object getVariable(String name) {
		return variables.get(name);
	}
	public void setVariable(String name,Object value) {
		variables.put(name, value);
	}
	@Override
	public String toString() {
		return "FunctionContext [variables=" + variables + ", ret=" + ret + ", goOn=" + goOn + "]";
	}
	
}
