package io.github.danthe1st.yagpl.api;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

import io.github.danthe1st.yagpl.api.throwables.YAGPLException;
import io.github.danthe1st.yagpl.api.util.Identifier;

public abstract class GenericObjectAdapter<R, C> implements GenericObject<R, C>,Cloneable {
	private String name;
	private Class<?>[] expectedParams = null;
	private Identifier id;

	public GenericObjectAdapter(String name) {
		super();
		id = new Identifier();
		this.name = name;
	}

	public GenericObjectAdapter(String name, Class<?>[] expectedParameters) {
		super();
		id = new Identifier();
		this.name = name;
		this.expectedParams = expectedParameters;
	}
	
	@Override
	public Class<?>[] getExpectedParameters() {
		return expectedParams;
	}

	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int hashCode() {
		return Objects.hash(this.getClass(),id);
	}
	@Override
	public <T> GenericObject<R, T> createCopy() throws YAGPLException {
		try {
			@SuppressWarnings("unchecked")
			GenericObjectAdapter<R, T> copy=(GenericObjectAdapter<R, T>)clone();
			copy.id = new Identifier();
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new YAGPLException(e);
		}
	}
	@Override
	public boolean equals(Object obj) {
		return obj!=null&&obj.getClass()==this.getClass() && this.id == ((GenericObjectAdapter<?, ?>) obj).id;
	}
}
