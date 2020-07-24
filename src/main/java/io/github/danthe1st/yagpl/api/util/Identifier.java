package io.github.danthe1st.yagpl.api.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public final class Identifier implements Externalizable{
	private static long currentId=0;
	private long id;

	public Identifier() {
		id=getNextId();
	}
	
	private static synchronized long getNextId() {
		return currentId++;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Identifier other = (Identifier) obj;
		return id == other.id;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		//do nothing
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		id=getNextId();
	}
	
}
