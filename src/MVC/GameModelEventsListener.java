package MVC;

public interface GameModelEventsListener {

	void addMissileLauncherInModel(String id,boolean isHidden);

	void addMissileLauncherDestructorInModel(String id, String type);

	void addMissileDestructorInModel(String id);

	void launchMissileInModel(String missileLauncherId, String missileId, String destination, int damage,int flyTime);

	void destructMissileLauncherInModel(String id, String type,String missileLauncherId);

	void destructMissileInModel(String missileIdToDestruct, String missileDestructorId, int waitingTime);
	
	void missileResultInModel(String missileId,boolean isHit,boolean isDestructed,boolean isHidden,String launcherId);

	void notifyFailedAddMissileLauncherInModel(String message);

	void notifyFailedAddMissileLauncherDestructorInModel(String message);

	void notifyFailedAddMissileDestructorInModel(String message);

	void missileDestructResultInModel(String destructorID ,boolean isDestructed);

	void missileLauncherDestructResultInModel(String id, String type , String missileLauncherId ,boolean isDestructed);

	void notifyFailedLaunchMissileInModel(String message);

	void notifyFailedDestructMissileInModel(String message);

	void notifyFailedDestructMissileLaucherInModel(String message);

	//void exitInModel();



	
//	void notifyFaildLaunchMissileInModel(String massage);
//
//	void notifyFaildDestructMissileLauncherInModel(String massage);
//
//	void notifyFaildDestructMissileInModel(String massage);

}
