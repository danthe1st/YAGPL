module io.github.danthe1st.yagpl{
	exports io.github.danthe1st.yagpl.api;
	exports io.github.danthe1st.yagpl.ui to javafx.graphics;
	opens io.github.danthe1st.yagpl.ui.controller to javafx.fxml;
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires javafx.base;
}