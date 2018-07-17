package UI;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class DestructLauncherDialog extends Dialog<Object> {
	private TextField type = new TextField();
	private TextField missileLauncherId = new TextField();


	public TextField getType() {
		return type;
	}

	public TextField getMissileLauncherId() {
		return missileLauncherId;
	}

	public DestructLauncherDialog() {

		this.setTitle("Missile Launch");
		this.setHeaderText("Launch Missile");

		// Set the button types.
		
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(SETTINGS.DIALOG_GAP);
		grid.setVgap(SETTINGS.DIALOG_GAP);
		grid.setPadding(new Insets(20, 150, 10, 10));

		grid.add(new Label("Enter Launcher Destructor Type:"), 0, 0);
		grid.add(type, 1, 0);
		grid.add(new Label("Enter Missile Launcher ID:"), 0, 1);
		grid.add(missileLauncherId, 1, 1);
		

		this.getDialogPane().setContent(grid);

	}
}
