package UI;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class DestructMissileDialog extends Dialog<Object> {
	private TextField missileID = new TextField();
	private TextField missileDestructorId = new TextField();

	public TextField getMissileID() {
		return missileID;
	}

	public TextField getMissileDestructorId() {
		return missileDestructorId;
	}

	public DestructMissileDialog() {

		this.setTitle("Missile Destruct");
		this.setHeaderText("Destruct Missile");

		// Set the button types.
		
		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(SETTINGS.DIALOG_GAP);
		grid.setVgap(SETTINGS.DIALOG_GAP);
		grid.setPadding(new Insets(20, 150, 10, 10));

		grid.add(new Label("Enter Missile Id"), 0, 0);
		grid.add(missileID, 1, 0);
		grid.add(new Label("Enter Missile Destructor ID:"), 0, 1);
		grid.add(missileDestructorId, 1, 1);
		

		this.getDialogPane().setContent(grid);

	}
}
