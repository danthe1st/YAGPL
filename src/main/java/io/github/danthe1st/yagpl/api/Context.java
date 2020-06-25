package io.github.danthe1st.yagpl.api;

public interface Context {
	public Object getVariable(String name);
	public void setVariable(String name,Object value);
	public boolean hasVariable(String name);
}
