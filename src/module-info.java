module CaLouseIF {
	requires javafx.graphics;
	requires java.sql;
	requires javafx.controls;
	opens models to javafx.base;
	opens main;
}