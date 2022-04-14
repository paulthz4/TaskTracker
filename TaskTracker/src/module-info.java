module TestApp {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.base;
	requires org.mongodb.driver.sync.client;
	requires org.mongodb.driver.core;
	requires org.mongodb.bson;

	opens application to javafx.graphics, javafx.fxml;
}
