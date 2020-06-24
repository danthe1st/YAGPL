package io.github.danthe1st.yagpl.api.throwables;

public class NotResolveableException extends YAGPLException{
	private static final long serialVersionUID = -8145913142060705703L;
	public NotResolveableException(String toResolve) {
		super("\""+toResolve+"\" cannot be resolved.");
	}
}
