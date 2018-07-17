package UI;

import UI.SETTINGS.MISSSILE_ANIMATION;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class MissileView  extends BorderPane{


	private double xLoc;
	private double yLoc;
	TranslateTransition transition ;
	private MissileLauncherView from;
	Image regular = new Image(MissileView.class.getResourceAsStream(SETTINGS.MISSILE_IMAGE));

	ImageView image;
	public MissileView(String id,MissileLauncherView from) {
		this.from = from;
		Text txtName = new Text(id);
		image = new ImageView(regular);
		image.setFitHeight(SETTINGS.MISSILE_IMAGE_HEIGHT);
		image.setFitWidth(SETTINGS.MISSILE_IMAGE_WIDTH);
		this.setTop(image);
		this.setBottom(txtName);
	}
	public double getxLoc() {
		return xLoc;
	}
	
	public double getyLoc() {
		return yLoc;
	}
	
	public TranslateTransition getTransition() {
		return transition;
	}
	public void MissileAnimation(int duration,MISSSILE_ANIMATION phase){
		transition= new TranslateTransition();
		Bounds boundsInScene;
		switch (phase) {
		case START://launch
			transition.setDuration(Duration.seconds(duration));
			boundsInScene = from.localToScene(from.getBoundsInLocal());
			transition.setFromX(boundsInScene.getMaxX());
			transition.setFromY(boundsInScene.getMinY());
			this.xLoc=boundsInScene.getMaxX()+SETTINGS.MISSILE_DISTANCE;
			this.yLoc=boundsInScene.getMinY();
			transition.setByX(SETTINGS.MISSILE_DISTANCE);
			transition.setNode(this);
			transition.play();;

			break;
		case HIT://hit
			transition.setDuration(Duration.seconds(1));
			boundsInScene = this.localToScene(this.getBoundsInLocal());
			transition.setFromX(boundsInScene.getMaxX());
			transition.setFromY(boundsInScene.getMinY());
			this.xLoc=boundsInScene.getMaxX()+SETTINGS.MISSILE_DISTANCE;
			this.yLoc=boundsInScene.getMaxY();
			transition.setByX(SETTINGS.MISSILE_DISTANCE_TO_HIT);
			transition.setNode(this);
			transition.play();
			break;
		case MISS://missed
			this.setVisible(false);
			boundsInScene = from.localToScene(from.getBoundsInLocal());
			this.xLoc=boundsInScene.getMaxX()+SETTINGS.MISSILE_DISTANCE;
			this.yLoc=boundsInScene.getMinY();
			break;
		default:
			break;
		}
	
	}
}

