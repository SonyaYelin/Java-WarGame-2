package DB;

public interface IDB {

	public static final String MISSILE_LAUNCHERS = "missile_launchers";
	public static final String LAUNCHER_DESTRUCTORS = "launcher_destructors";
	public static final String MISSILE_DESTRUCTORS = "missile_destructors";

	void addMissileLuauncher(String id, boolean isHidden);
	
	void addMissileLaunch(String id);
	
	void addMissileHit(String id);
	
	void addMissileDestructor(String id);
	
	void addMissileDestruct(String id);
	
	void addLauncherDestructor(String id, String type);

	void addLauncherDestruct(String destructorID, String launcherID);
	
	void closeDB();
	
	//void getFromDB(String id, String from);

	
	
	//List<String> getAllTablesNames();
	
	//get all table data
	//Vector<String[]> getQueryData(String tableName, Vector<String> headers);
		
}
