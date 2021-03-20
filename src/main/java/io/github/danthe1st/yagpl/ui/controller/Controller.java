package io.github.danthe1st.yagpl.ui.controller;

import io.github.danthe1st.yagpl.api.GlobalContext;
import io.github.danthe1st.yagpl.ui.YAGPL;
import javafx.scene.Parent;

/**
 * Interface for UI controllers
 * @author dan1st
 *
 * @param <T> the node this controller is associated with
 */
public interface Controller<T extends Parent> {
	/**
	 * sets the main object.<br/>
	 * This method should be called right after initializing the view
	 * @param main the main object
	 */
	void setMain(YAGPL main);
	/**
	 * sets the view object.<br/>
	 * This method should be called right after initializing the view
	 * @param view the root view
	 */
	void setView(T view);
	/**
	 * gets the root view
	 * @return the root view
	 */
	T getView();
	/**
	 * sets the global context used for this controller
	 * @param globalCtx the {@link GlobalContext}
	 */
	void setGlobalContext(GlobalContext globalCtx);
}
