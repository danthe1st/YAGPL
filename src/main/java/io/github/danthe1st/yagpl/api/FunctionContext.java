package io.github.danthe1st.yagpl.api;

import java.util.HashMap;
import java.util.Map;

public class FunctionContext<R> implements Context{
	private Map<String, Object> variables=new HashMap<>();
	private GlobalContext globalCtx;
	private R ret=null;
	private boolean goOn=true;
	public FunctionContext(GlobalContext globalCtx) {
		this.globalCtx=globalCtx;
	}
	public FunctionContext(FunctionContext<?> ctx) {
		this.globalCtx=ctx.globalCtx;
	}
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
		Object ret=variables.get(name);
		if(ret==null) {
			ret=globalCtx.getVariable(name);
		}
		return ret;
	}
	public void setVariable(String name,Object value) {
		if(!variables.containsKey(name)&&globalCtx.hasVariable(name)) {
			globalCtx.setVariable(name, value);
		}else {
			variables.put(name, value);
		}
	}
	@Override
	public String toString() {
		return "FunctionContext [variables=" + variables + ", globalCtx=" + globalCtx + ", ret=" + ret + ", goOn="
				+ goOn + "]";
	}
	@Override
	public boolean hasVariable(String name) {
		return variables.containsKey(name)||globalCtx.hasVariable(name);
	}
	
	
}
