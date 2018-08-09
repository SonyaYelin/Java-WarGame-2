package UI;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import MVC.GameUIEventsListener;
import UI.SETTINGS.MISSSILE_ANIMATION;
import Util.CloseApplicationUtil;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GamePane extends AnchorPane implements GameUI {
	final static int NUM_OF_TREES = 6;


	private Vector<GameUIEventsListener> allListeners;
	private VisualApplication theApplication;
	private VBox missileLauncherVBox;
	private VBox missileDestructorVBox;
	private HBox missileLauncherDestructorPlaneHBox;
	private HBox missileLauncherDestructorShipHBox;
	private VBox treeVBox;
	private HashMap<String, Integer> missileLaunchersLocationMap = new HashMap<>();
	private HashMap<String, MissileView> missilesOnScreen = new HashMap<>();
	private HashMap<String, Integer> missiledestructorLocationMap = new HashMap<>();
	private HashMap<String, Integer> missileLauncherDestructorLocationMap = new HashMap<>();

	private int numOfMissileL = 0;
	private int numOfMissileD = 0;
	private int numOfMissileLD_ship = 0;
	private int numOfMissileLD_plane = 0;


	public GamePane(VisualApplication theApplication) {

		this.theApplication = theApplication;
		this.allListeners = new Vector<GameUIEventsListener>();
		//set game pane size relatively
		this.prefHeightProperty().bind(theApplication.getPrimaryStage().getScene().heightProperty());
		this.prefWidthProperty().bind(theApplication.getPrimaryStage().getScene().widthProperty()
				.subtract(theApplication.getMenu().widthProperty()));

		// create vBox for missileLaunchers - on left side
		missileLauncherVBox = new VBox();
		missileLauncherVBox.setId("missileLauncherVBox");

		// create vBox for missileDestructor on right
		missileDestructorVBox = new VBox();
		missileDestructorVBox.setId("missileDestructorVBox");
		
		// create hBox for missileLauncherDestructorPlaneHBox on top
		missileLauncherDestructorPlaneHBox = new HBox();
		missileLauncherDestructorPlaneHBox.setId("missileLauncherDestructorPlaneHBox");
		
		// create hBox for missileLauncherDestructorShipHBox on top
		missileLauncherDestructorShipHBox = new HBox();
		missileLauncherDestructorShipHBox.setId("missileLauncherDestructorShipHBox");
		
		// create vBox for trees in the middle
		treeVBox = new VBox();
		treeVBox.setId("treeVBox");
		
		createTrees(treeVBox);

		// set location for all children
		setLocationViews();

		this.getChildren().addAll(missileLauncherVBox, missileDestructorVBox, treeVBox,
				missileLauncherDestructorPlaneHBox, missileLauncherDestructorShipHBox);

	}

	private void setLocationViews() {
		AnchorPane.setBottomAnchor(missileLauncherVBox, 60.0);
		AnchorPane.setLeftAnchor(missileLauncherVBox, 10.0);
		AnchorPane.setRightAnchor(missileDestructorVBox, 10.0);
		AnchorPane.setBottomAnchor(missileDestructorVBox, 60.0);
		AnchorPane.setRightAnchor(treeVBox, 200.0);
		AnchorPane.setBottomAnchor(treeVBox, 60.0);
		AnchorPane.setTopAnchor(missileLauncherDestructorPlaneHBox, 10.0);
		AnchorPane.setLeftAnchor(missileLauncherDestructorPlaneHBox, 10.0);
		AnchorPane.setTopAnchor(missileLauncherDestructorShipHBox, 120.0);
		AnchorPane.setLeftAnchor(missileLauncherDestructorShipHBox, 10.0);
	}

	private void createTrees(VBox treeVbox) {

		for (int i = 0; i < NUM_OF_TREES; i++) {
			TreeView t = new TreeView();
			treeVbox.getChildren().add(t);
		}

	}

	@Override
	public void registerListener(GameUIEventsListener listener) {
		allListeners.add(listener);

	}

	@Override
	public void addMissileLauncher(String id) {
		for (GameUIEventsListener l : allListeners)
			l.addMissileLauncherFromUI(id);
	}
	@Override
	public void showAddMissileLauncher(String id,boolean isHidden) {

		MissileLauncherView m = new MissileLauncherView(id,isHidden);
		missileLaunchersLocationMap.put(id, numOfMissileL);
		numOfMissileL++;
		missileLauncherVBox.getChildren().add(m);
	}

	@Override
	public void addMissileLauncherDestructor(String type) {
		if (type.contains("plane") || type.contains("ship"))
			for (GameUIEventsListener l : allListeners)
				l.addMissileLauncherDestructorFromUI(type);
		else {
			showMessage("type must be ship/plane");
		}
	}

	@Override 
	public void showAddMissileLauncherDestructor(String type) {
		if (type.contains("plane")) {
			MissileLauncherDestructorView mLd = new MissileLauncherDestructorView(type, "plane");
			missileLauncherDestructorPlaneHBox.getChildren().add(numOfMissileLD_plane,mLd);
			missileLauncherDestructorLocationMap.put(type, numOfMissileLD_plane);
			numOfMissileLD_plane++;
		} else if (type.contains("ship")) {
			MissileLauncherDestructorView mLd = new MissileLauncherDestructorView(type, "ship");
			missileLauncherDestructorShipHBox.getChildren().add(numOfMissileLD_ship,mLd);
			missileLauncherDestructorLocationMap.put(type, numOfMissileLD_ship);
			numOfMissileLD_ship++;
		}

	}

	@Override
	public void addMissileDestructor(String id) {
		for (GameUIEventsListener l : allListeners)
			l.addMissileDestructorFromUI(id);
	}

	@Override
	public void showAddMissileDestructor(String id) {
		MissileDestructorView md = new MissileDestructorView(id);
		missileDestructorVBox.getChildren().add(md);
		missiledestructorLocationMap.put(id, numOfMissileD);
		numOfMissileD++;

	}

	@Override
	public void launchMissile(String missileLauncherId, String missileId, String destination, int damage) {
		for (GameUIEventsListener l : allListeners)
			l.launchMissileFromUI(missileLauncherId, missileId, destination, damage);
	}
	@Override
	public void showMissileLaunch(String missileLauncherId, String missileId, String destination, int damage,
			int flytime) {
		Platform.runLater(() -> {
			((MissileLauncherView) missileLauncherVBox.getChildren().get(missileLaunchersLocationMap.get(missileLauncherId))).updateText(false);;
			MissileView missile = new MissileView(missileId, (MissileLauncherView) missileLauncherVBox.getChildren()
					.get(missileLaunchersLocationMap.get(missileLauncherId)));
			missilesOnScreen.put(missileId, missile);
			this.getChildren().add(missile);
			missile.MissileAnimation(flytime, MISSSILE_ANIMATION.START);
		});
	}

	@Override
	public void showMissileResult(String missileId, boolean isHit, boolean isDestructed,boolean isHidden,String launcherId) {
		Platform.runLater(() -> {
			((MissileLauncherView) missileLauncherVBox.getChildren().get(missileLaunchersLocationMap.get(launcherId))).updateText(isHidden);
			MissileView missile = missilesOnScreen.get(missileId);
			if (isHit)
				missile.MissileAnimation(0, MISSSILE_ANIMATION.HIT);
			else if (isDestructed)
				missile.MissileAnimation(0, MISSSILE_ANIMATION.DESTRUCT);
			else
				missile.MissileAnimation(0, MISSSILE_ANIMATION.MISS);

		});
	}

	@Override
	public void destructMissileLauncher(String missileLaucherDestructType, String missileLaucherDestructId) {
		for (GameUIEventsListener l : allListeners)
			l.destructMissileLauncherFromUI(missileLaucherDestructType, missileLaucherDestructId);

	}

	@Override
	public void showDestructMissileLauncher(String type, String missileLauncherId) {
		Platform.runLater(() -> {
			if(type.contains("ship")){
				((MissileLauncherDestructorView) missileLauncherDestructorShipHBox.getChildren()
						.get(missileLauncherDestructorLocationMap.get(type))).updateText("trying to \n destruct "+missileLauncherId);
			}
			else if(type.contains("plane")){
				((MissileLauncherDestructorView) missileLauncherDestructorPlaneHBox.getChildren()
						.get(missileLauncherDestructorLocationMap.get(type))).updateText("trying to \n destruct "+missileLauncherId);;
			}
		});
	}
	
	@Override
	public void showLauncherDestructResult(String type , String missileLauncherId ,boolean isDestructed) {
		Platform.runLater(() -> {
			((MissileLauncherView) missileLauncherVBox.getChildren().get(missileLaunchersLocationMap.get(missileLauncherId))).updateImage(isDestructed);
			String result;
			if(isDestructed)
				result = "Success";
			else
				result = "Failed";
			if(type.contains("ship")){
				
				((MissileLauncherDestructorView) missileLauncherDestructorShipHBox.getChildren()
						.get(missileLauncherDestructorLocationMap.get(type))).updateText("trying to \n destruct "+missileLauncherId + " " + result);
			}
			else if(type.contains("plane")){
				((MissileLauncherDestructorView) missileLauncherDestructorPlaneHBox.getChildren()
						.get(missileLauncherDestructorLocationMap.get(type))).updateText("trying to \n destruct "+missileLauncherId+ " "+ result);
			}
		});		
	}

	@Override
	public void destructMissile(String missileIdToDestruct, String missileDestructorId) {
		for (GameUIEventsListener l : allListeners)
			l.destructMissileFromUI(missileIdToDestruct, missileDestructorId);
	}

	@Override
	public void showDestructMissile(String missileIdToDestruct, String missileDestructorId,int waitingTime) {
		Platform.runLater(() -> {
			MissileDestructView destruct = new MissileDestructView((MissileDestructorView) missileDestructorVBox.getChildren()
					.get(missiledestructorLocationMap.get(missileDestructorId)),waitingTime);
			this.getChildren().add(destruct);
			destruct.destructAnimation(missilesOnScreen.get(missileIdToDestruct));
		});

	}

	@Override
	public void viewGameStatus() {
		for (GameUIEventsListener l : allListeners)
			l.viewGameStatusFromUI();

	}

	@Override
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "Info" + "",
				JOptionPane.INFORMATION_MESSAGE);

	}

	@Override
	public void exit() {
		for (GameUIEventsListener l : allListeners)
			l.exitFromUI();
		CloseApplicationUtil.closeApplication(theApplication);
	}

	


}
