package UI;

import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

public class MissileDestructView  extends BorderPane{


	
	private MissileDestructorView from;
	private int waitingTime;
	private Image regularImage = new Image(MissileLauncherView.class.getResourceAsStream(SETTINGS.MISSILE_DESTRUCT));
	ImageView image;

	public MissileDestructView(MissileDestructorView from, int waitingTime) {
		this.waitingTime=waitingTime;
		this.from = from;
		 image = new ImageView(regularImage);
		image.setFitHeight(SETTINGS.MISSILE_IMAGE_HEIGHT);
		image.setFitWidth(SETTINGS.MISSILE_IMAGE_WIDTH);
		this.setTop(image);
		
	}
	
	public void destructAnimation(MissileView to){
		TranslateTransition transition = new TranslateTransition();
		Bounds boundsInScene;
	
			transition.setDuration(Duration.seconds(waitingTime));
			boundsInScene = from.localToScene(from.getBoundsInLocal());
			transition.setFromX(boundsInScene.getMaxX());
			transition.setFromY(boundsInScene.getMinY());
			boundsInScene = to.localToScene(to.getBoundsInLocal());

			transition.setToX(to.getxLoc());
			transition.setToY(to.getyLoc());
			transition.setNode(this);
			transition.play();
		
		
	}
		
	
	

}

