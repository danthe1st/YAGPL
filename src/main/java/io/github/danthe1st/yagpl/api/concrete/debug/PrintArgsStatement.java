package io.github.danthe1st.yagpl.api.concrete.debug;

import java.util.Arrays;

import io.github.danthe1st.yagpl.api.Statement;

public class PrintArgsStatement<C> extends Statement<C>{
	public PrintArgsStatement() {
		super("print-args", args->System.out.println(Arrays.toString(args)));
	}
}
