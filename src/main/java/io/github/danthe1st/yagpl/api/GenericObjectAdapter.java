package io.github.danthe1st.yagpl.api;

public abstract class GenericObjectAdapter<R,C> implements GenericObject<R,C> {
	private String name;

	public GenericObjectAdapter(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
