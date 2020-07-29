package io.github.danthe1st.yagpl.api.concrete.debug;

import java.util.Arrays;

import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.api.Statement;

@StandardElement
public class PrintArgsStatement extends Statement{
	public PrintArgsStatement() {
		super("print-args", args->System.out.println(Arrays.toString(args)));
	}
}
