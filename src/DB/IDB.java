package DB;

public interface IDB {

	public static final String DB_NAME = "war";
	public static final String MISSILE_LAUNCHERS = "missile_launchers";
	public static final String LAUNCHER_DESTRUCTORS = "launcher_destructors";
	public static final String MISSILE_DESTRUCTORS = "missile_destructors";

	public static final String ID = "id";
	public static final String IS_HIDDEN = "is_hidden";
	public static final String IS_DESTRUCTED = "is_destructed";
	public static final String LAUNCHED_MISSILES = "launched_missiles";
	public static final String MISSILE_HITS = "missile_hits";
	public static final String DESTRUCTED_MISSILES = "destructed_missiles";
	public static final String TYPE = "type";
	public static final String DESTRUCTED_LAUNCHERS = "destructed_launchers";

	
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
