package UI;

import Handlers.ConfigHandler;
import Logic.Game;
import MVC.GameController;
import Server.ServerReceiver;
import Util.CloseApplicationUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class VisualApplication extends Application {

	private Stage primaryStage;
	private BorderPane mainPanel;
	private GamePane gamePanel;
	private VisualMenu menu;
	private ServerReceiver serverReceiver;

	public BorderPane getMainPanel() {
		return mainPanel;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		mainPanel = new BorderPane();
	
		Scene scene = new Scene(mainPanel, SETTINGS.SCREEN_WIDTH, SETTINGS.SCREEN_HEIGHT);

		this.primaryStage = primaryStage;
		scene.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				e.consume(); // to prevent the application from default closing
				CloseApplicationUtil.closeApplication(VisualApplication.this);
			}
		});
		menu = new VisualMenu(this);
		gamePanel = new GamePane(this);
		serverReceiver = new ServerReceiver(this);
        
		Thread serverThread = new Thread(serverReceiver);
		serverThread.start();
		
		primaryStage.setTitle("War Game");

		gamePanel.setId("game_pane");
		menu.setId("menu_box");
		
		mainPanel.setRight(menu);
		mainPanel.setLeft(gamePanel);

		menu.setAlignment(Pos.TOP_RIGHT);
		menu.getStyleClass().addAll("vbox","button");
		
		new GameController(gamePanel);
		configDialog();
		primaryStage.show();

	
	}
	private void configDialog() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Load");
		alert.setContentText("Do you want to read game settings from config file??");
		ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonData.NO);
		alert.getButtonTypes().setAll(okButton, noButton);
		alert.showAndWait().ifPresent(type -> {
		        if (type.getText() == "Yes") {
		        	try {
						new ConfigHandler().readObjectsFromJSONFile();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
		        } else if (type == ButtonType.NO) {
		        }
		});		
	}

	public GamePane getGamePanel() {
		return gamePanel;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}
	public VisualMenu getMenu() {
		return menu;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
