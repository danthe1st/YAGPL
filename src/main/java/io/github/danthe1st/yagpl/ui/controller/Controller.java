package io.github.danthe1st.yagpl.ui.controller;

import io.github.danthe1st.yagpl.ui.YAGPL;
import javafx.scene.Parent;

public interface Controller<T extends Parent> {
	void setMain(YAGPL main);
	void setView(T view);
	T getView();
}
