package io.github.danthe1st.yagpl.ui.controller;

import io.github.danthe1st.yagpl.ui.YAGPL;
import javafx.scene.Parent;

public abstract class ControllerAdapter<T extends Parent> implements Controller<T> {

	protected YAGPL main;
	protected T view;
	
	@Override
	public void setMain(YAGPL main) {
		this.main=main;
	}

	@Override
	public void setView(T view) {
		this.view=view;
	}
	
	public T getView() {
		return view;
	}
	
}
