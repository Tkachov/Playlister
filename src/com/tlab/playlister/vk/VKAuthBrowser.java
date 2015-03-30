package com.tlab.playlister.vk;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class VKAuthBrowser extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("VK authorize");

		Scene scene = new Scene(new Browser(stage), 660, 380, Color.web("#FFFFFF"));
		stage.setScene(scene);
		stage.show();
	}

	class Browser extends Region {
		final WebView browser = new WebView();
		final WebEngine webEngine = browser.getEngine();

		public Browser(final Stage stage) {
			webEngine.load(VK.ACCESS_TOKEN_URL);

			final WebHistory history = webEngine.getHistory();
			history.getEntries().addListener(new ListChangeListener<WebHistory.Entry>() {
				@Override
				public void onChanged(Change<? extends WebHistory.Entry> c) {
					c.next();
					for(WebHistory.Entry e : c.getAddedSubList()) {
						if(e.getUrl().startsWith("https://oauth.vk.com/blank.html")) {
							VK.authorized(e.getUrl());
							stage.close();
						}
					}
				}
			});

			getChildren().add(browser);
		}

		@Override
		protected void layoutChildren() {
			double w = getWidth();
			double h = getHeight();
			layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
		}

		@Override
		protected double computePrefWidth(double height) {
			return 660;
		}

		@Override
		protected double computePrefHeight(double width) {
			return 380;
		}
	}
}