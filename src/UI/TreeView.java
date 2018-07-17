package UI;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class TreeView  extends BorderPane{

	public TreeView() {
		Image temp = new Image(MissileLauncherView.class.getResourceAsStream(SETTINGS.TREE_IMAGE));
		ImageView image = new ImageView(temp);
		image.setFitHeight(SETTINGS.MISSILE_IMAGE_HEIGHT);
		image.setFitWidth(SETTINGS.MISSILE_IMAGE_WIDTH);
		this.setTop(image);
	}
}
