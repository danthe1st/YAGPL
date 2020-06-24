package io.github.danthe1st.yagpl.api;

import java.util.Objects;

public abstract class GenericObjectAdapter<R, C> implements GenericObject<R, C> {
	private String name;
	private Class<?>[] expectedParams = null;
	private static long currentId = 0L;
	private final long id;

	@Override
	public Class<?>[] getExpectedParameters() {
		return expectedParams;
	}

	public GenericObjectAdapter(String name) {
		super();
		id = currentId++;
		this.name = name;
	}

	public GenericObjectAdapter(String name, Class<?>[] expectedParameters) {
		super();
		id = currentId++;
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
	public boolean equals(Object obj) {
		return obj instanceof GenericObjectAdapter && this.id == ((GenericObjectAdapter<?, ?>) obj).id;
	}
}
