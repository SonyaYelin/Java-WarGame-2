package UI;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class MissileLauncherDestructorView extends BorderPane {

	Text txtName;
	private String id;

	public MissileLauncherDestructorView(String id, String imageType) {
		this.id = id;
		txtName = new Text(id);
		Image temp = null;
		if (imageType.equals("plane")) {
			temp = new Image(MissileLauncherView.class.getResourceAsStream(SETTINGS.MISSILE_LAUNCHER_DESTRUCTOR_PLAIN));
		} else if (imageType.equals("ship"))
			temp = new Image(MissileLauncherView.class.getResourceAsStream(SETTINGS.MISSILE_LAUNCHER_DESTRUCTOR_SHIP));
		ImageView image = new ImageView(temp);
		image.setFitHeight(SETTINGS.MISSILE_IMAGE_HEIGHT);
		image.setFitWidth(SETTINGS.MISSILE_IMAGE_WIDTH);
		this.setTop(image);
		this.setBottom(txtName);
	}
	
	public void updateText(String text){
		txtName.setText(id +" "+ text);
	}
}
