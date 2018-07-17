package UI;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class MissileDestructorView extends BorderPane {

	public MissileDestructorView(String id) {
		Text txtName = new Text(id);
		Image temp = new Image(MissileLauncherView.class.getResourceAsStream(SETTINGS.MISSILE_DESTRUCTOR_IMAGE));
		ImageView image = new ImageView(temp);
		image.setFitHeight(SETTINGS.MISSILE_IMAGE_HEIGHT);
		image.setFitWidth(SETTINGS.MISSILE_IMAGE_WIDTH);
		this.setTop(image);
		this.setBottom(txtName);

	}
}
