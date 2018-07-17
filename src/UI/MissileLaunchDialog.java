package UI;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class MissileLaunchDialog extends Dialog<Object> {
	private TextField missileLauncherId = new TextField();
	private TextField missileID = new TextField();
	private TextField destination = new TextField();
	private TextField damage = new TextField();

	public TextField getMissileLauncherId() {
		return missileLauncherId;
	}

	public TextField getMissileID() {
		return missileID;
	}

	public TextField getDestination() {
		return destination;
	}

	public TextField getDamage() {
		return damage;
	}

	public MissileLaunchDialog() {

		this.setTitle("Missile Launch");
		this.setHeaderText("Launch Missile");

		// Set the button types.
		
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(SETTINGS.DIALOG_GAP);
		grid.setVgap(SETTINGS.DIALOG_GAP);
		grid.setPadding(new Insets(20, 150, 10, 10));

		grid.add(new Label("Enter Missile Launcher ID:"), 0, 0);
		grid.add(missileLauncherId, 1, 0);
		grid.add(new Label("Enter Missile ID:"), 0, 1);
		grid.add(missileID, 1, 1);
		grid.add(new Label("Enter Destination:"), 0, 2);
		grid.add(destination, 1, 2);
		grid.add(new Label("Enter Damage:"), 0, 3);
		grid.add(damage, 1, 3);

		this.getDialogPane().setContent(grid);

	}
}
