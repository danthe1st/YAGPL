package io.github.danthe1st.yagpl.api;

import java.util.Objects;

import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

public abstract class GenericObjectAdapter<R, C> implements GenericObject<R, C>,Cloneable {
	private String name;
	private Class<?>[] expectedParams = null;
	private static long currentId = 0L;
	private long id;

	private static synchronized long getId() {
		return currentId++;
	}
	
	@Override
	public Class<?>[] getExpectedParameters() {
		return expectedParams;
	}

	public GenericObjectAdapter(String name) {
		super();
		id = getId();
		this.name = name;
	}

	public GenericObjectAdapter(String name, Class<?>[] expectedParameters) {
		super();
		id = getId();
		this.name = name;
		this.expectedParams = expectedParameters;
	}

	@Override
	public String getName() {
		return name;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public <T> GenericObject<R, T> createCopy() throws YAGPLException {
		try {
			@SuppressWarnings("unchecked")
			GenericObjectAdapter<R, T> copy=(GenericObjectAdapter<R, T>)clone();
			copy.id = getId();
			return copy;
		} catch (CloneNotSupportedException e) {
			throw new YAGPLException(e);
		}
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof GenericObjectAdapter && this.id == ((GenericObjectAdapter<?, ?>) obj).id;
	}
}
