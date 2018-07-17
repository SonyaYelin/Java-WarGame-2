package UI;

import MVC.GameUIEventsListener;


public interface GameUI {
	
	static final String[] MENU_FUNCTIONS_NAMES = {"Add Missile Launcher","Add Missile Launcher Destructor","Add Missile Destructor",
			"Launch Missile","Destruct Missile Launcher","Destruct Missile","View Game Status","Exit"};
	void addMissileLauncher(String id);
	void addMissileLauncherDestructor(String type);
	void addMissileDestructor(String id);
	void launchMissile(String missileLauncherId, String missileId, String destination, int damage);
	void destructMissileLauncher(String missileLaucherDestructType, String missileLaucherDestructId);
	void destructMissile(String missileIdToDestruct, String missileDestructorId);
	void viewGameStatus();
	void exit();
	void registerListener(GameUIEventsListener listener);
	
	void showAddMissileLauncher(String id,boolean isHidden);
	void showAddMissileLauncherDestructor(String type);
	void showAddMissileDestructor(String id);
	void showMissileLaunch(String missileLauncherId, String missileId, String destination, int damage,int flytime);
	void showDestructMissileLauncher(String type,String missileLauncherId);
	void showDestructMissile(String missileIdToDestruct, String missileDestructorId, int waitingTime);
	void showMissileResult(String missileId, boolean isHit, boolean isDestructed,boolean isHidden,String launcherId);
	void showLauncherDestructResult(String type , String missileLauncherId ,boolean isDestructed);
	void showMessage(String message);
}
