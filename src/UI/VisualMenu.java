package UI;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Vector;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

public class VisualMenu extends VBox {

	private VisualApplication theApplication;

	private Vector<Button> buttons = new Vector<>();

	public VisualMenu(VisualApplication theApplication) {
		this.theApplication = theApplication;

		for (int i = 0; i < GameUI.MENU_FUNCTIONS_NAMES.length; i++) {
			Button btn = new Button(GameUI.MENU_FUNCTIONS_NAMES[i]);
			buttons.addElement(btn);
			setOnClick(i);

		}
		for (Button button : buttons) {
			this.getChildren().add(button);
		}

	}

	private void setOnClick(int i) {
		TextInputDialog dialog = new TextInputDialog();

		switch (i) {
		case 0:
			buttons.get(0).setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					try {
						dialog.setTitle("Add Missile Launcher");
						dialog.setHeaderText("Missile Launcher");
						dialog.setContentText("Please enter Missile Launcher id:");
						Optional<String> result = dialog.showAndWait();
						theApplication.getGamePanel().addMissileLauncher(result.get());
					} catch (NoSuchElementException e) {

					}
				}
			});
			break;

		case 1:
			buttons.get(1).setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					try {
						dialog.setTitle("Add Missile Launcher Destructor");
						dialog.setHeaderText("Missile Launcher Destructor");
						dialog.setContentText("Please enter Missile Launcher Destructor id:");
						Optional<String> result = dialog.showAndWait();
						theApplication.getGamePanel().addMissileLauncherDestructor(result.get());
					} catch (NoSuchElementException e) {

					}
				}
			});
			break;
		case 2:
			buttons.get(2).setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					try {
						dialog.setTitle("Add Missile Destructor");
						dialog.setHeaderText("Missile Destructor");
						dialog.setContentText("Please enter Missile Destructor id:");
						Optional<String> result = dialog.showAndWait();
						theApplication.getGamePanel().addMissileDestructor(result.get());
					} catch (NoSuchElementException e) {

					}
				}
			});
			break;

		case 3:
			buttons.get(3).setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					try {
						MissileLaunchDialog ld = new MissileLaunchDialog();
						ld.showAndWait();
						theApplication.getGamePanel().launchMissile(ld.getMissileLauncherId().getText(),
								ld.getMissileID().getText(), ld.getDestination().getText(),
								Integer.parseInt(ld.getDamage().getText()));
					} catch (Exception e) {

					}
				}
			});
			break;
		case 4:
			buttons.get(4).setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					try {
						DestructLauncherDialog ldr = new DestructLauncherDialog();
						ldr.showAndWait();
						theApplication.getGamePanel().destructMissileLauncher(ldr.getType().getText(),
								ldr.getMissileLauncherId().getText());
					} catch (Exception e) {

					}

				}
			});
			break;
		case 5:
			buttons.get(5).setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					try {
						DestructMissileDialog dml = new DestructMissileDialog();
						dml.showAndWait();
						theApplication.getGamePanel().destructMissile(dml.getMissileID().getText(),
								dml.getMissileDestructorId().getText());
					} catch (Exception e) {

					}
				}
			});
			break;

		case 6:
			buttons.get(6).setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					theApplication.getGamePanel().viewGameStatus();
				}
			});
			break;

		case 7:
			buttons.get(7).setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					theApplication.getGamePanel().exit();
				}
			});
			break;
		default:
			break;
		}
	}
}