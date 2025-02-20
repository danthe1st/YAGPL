package io.github.danthe1st.yagpl.api;

import java.io.Serializable;
import java.util.Arrays;

import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

public class ParameterizedGenericObject<T> implements Serializable {//TODO expression, object, ... -->proper name
	private GenericObject<T> obj;
	private String[] params;

	public ParameterizedGenericObject(GenericObject<T> obj, String[] params) {
		super();
		this.obj = obj;
		this.params = params;
	}

	public GenericObject<T> getObj() {
		return obj;
	}
	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((obj == null) ? 0 : obj.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ParameterizedGenericObject<?> other = (ParameterizedGenericObject<?>) obj;
		if (this.obj == null) {
			if (other.obj != null) {
				return false;
			}
		} else if (!this.obj.equals(other.obj)) {
			return false;
		}
		return true;
	}

	public ParameterizedGenericObject<T> createCopy() throws YAGPLException {
		return new ParameterizedGenericObject<>(obj.createCopy(), Arrays.copyOf(params,params.length));
	}

	@Override
	public String toString() {
		return "ParameterizedGenericObject [obj=" + obj + ", params=" + Arrays.toString(params) + "]";
	}

}
