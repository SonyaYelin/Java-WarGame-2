package UI;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class MissileLauncherView extends BorderPane {
	Image regularImage = new Image(MissileLauncherView.class.getResourceAsStream(SETTINGS.MISSILE_LAUNCHER_IMAGE));
	Image destructed = new Image(MissileLauncherView.class.getResourceAsStream(SETTINGS.MISSILE_LAUNCHER_DESTRUCT));

	ImageView image;
	private String id;
	Text txtName;
	public MissileLauncherView(String id, boolean isHidden) {
		this.id = id;
		 txtName = new Text(id + " isHidden " + isHidden);
		 image= new ImageView(regularImage);
		image.setFitHeight(SETTINGS.MISSILE_IMAGE_HEIGHT);
		image.setFitWidth(SETTINGS.MISSILE_IMAGE_WIDTH);
		this.setTop(image);
		this.setBottom(txtName);
	}
	
	public void updateText(boolean isHidden){
		txtName.setText(id + " isHidden " + isHidden);
	}
	
	public void updateImage(boolean isDestructed){
		if (isDestructed) {
			image.setImage(destructed);
		}
		else {
			image.setImage(regularImage);
		}
	}
}
