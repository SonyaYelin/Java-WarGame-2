package MVC;

public interface GameUIEventsListener {

	void addMissileLauncherFromUI(String id);

	void addMissileLauncherDestructorFromUI(String type);

	void addMissileDestructorFromUI(String id);

	void launchMissileFromUI(String missileLauncherId, String missileId, String destination, int damage);

	void destructMissileLauncherFromUI(String missileLaucherDestructType, String missileLaucherDestructId);

	void destructMissileFromUI(String missileIdToDestruct, String missileDestructorId);
	
	void viewGameStatusFromUI();

	void exitFromUI();
}
