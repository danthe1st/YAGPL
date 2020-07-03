package io.github.danthe1st.yagpl.api.lambdas;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface SerializableFunction<T, R> extends Serializable, Function<T, R> {

}
