package DB;


import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;


public class MongoDB implements IDB{

	
	private static MongoClient 	mongoClient;
	private static MongoDatabase database;
	
	private static final String LOCAL_HOST = "localhost";

	
	static {
		mongoClient = new MongoClient(LOCAL_HOST, 27017);
		database = mongoClient.getDatabase(DB_NAME);
	}

	@Override
	public void closeDB() {
		
		mongoClient.close();		
	}

	@Override
	public  synchronized  void addMissileLuauncher(String id, boolean isHidden) {
		MongoCollection<Document> collection = database.getCollection(MISSILE_LAUNCHERS);
		
		collection.updateOne(Filters.eq(ID, id),  Updates.combine( 
								Updates.set(ID, id), 
								Updates.set(IS_HIDDEN, isHidden),
								Updates.set(IS_DESTRUCTED, false),
								Updates.set(LAUNCHED_MISSILES, 0),
								Updates.set(MISSILE_HITS, 0)),
				new UpdateOptions().upsert(true).bypassDocumentValidation(true));
	}

	@Override
	public synchronized  void addMissileDestructor(String id) {
		MongoCollection<Document> collection = database.getCollection(MISSILE_DESTRUCTORS);
		
		collection.updateOne(Filters.eq(ID, id),  Updates.combine( Updates.set(ID, id), Updates.set(DESTRUCTED_MISSILES, 0) ),
                new UpdateOptions().upsert(true).bypassDocumentValidation(true));
	}

	@Override
	public  synchronized  void addLauncherDestructor(String id, String type) {
		MongoCollection<Document> collection = database.getCollection(LAUNCHER_DESTRUCTORS);
	
		collection.updateOne(Filters.eq(ID, id),  Updates.combine( 
								Updates.set(ID, id), 
								Updates.set(TYPE, type),
								Updates.set(DESTRUCTED_LAUNCHERS, 0)),
				new UpdateOptions().upsert(true).bypassDocumentValidation(true));
	}

	@Override
	public  synchronized  void addMissileLaunch(String id) {
		MongoCollection<Document> collection = database.getCollection(MISSILE_LAUNCHERS);
		Document d = collection.find(Filters.eq(ID, id)).first();
		
		int launchedMissiles = d.getInteger(LAUNCHED_MISSILES, 0) + 1;
		collection.updateMany(Filters.eq(ID, id), Updates.set(LAUNCHED_MISSILES, launchedMissiles));    
	}

	@Override
	public  synchronized void addMissileHit(String id) {
		MongoCollection<Document> collection = database.getCollection(MISSILE_LAUNCHERS);
		Document d = collection.find(Filters.eq(ID, id)).first();
		
		int missileHits = d.getInteger(MISSILE_HITS, 0) + 1;
		collection.updateMany(Filters.eq(ID, id), 
								Updates.set(MISSILE_HITS, missileHits));    
	} 

	@Override
	public  synchronized  void addMissileDestruct(String id) {
		MongoCollection<Document> collection = database.getCollection(MISSILE_DESTRUCTORS);
		Document d = collection.find(Filters.eq(ID, id)).first();
		
		int destructedMissiles = d.getInteger(DESTRUCTED_MISSILES, 0) + 1;
		collection.updateMany(Filters.eq(ID , id), 
								Updates.set(DESTRUCTED_MISSILES, destructedMissiles));    
	}

	@Override
	public synchronized void addLauncherDestruct(String destructorID, String launcherID) {
		MongoCollection<Document> collection = database.getCollection(LAUNCHER_DESTRUCTORS);
		Document d = collection.find(Filters.eq(ID, destructorID)).first();
		
		int destructedLaunchers = d.getInteger(DESTRUCTED_LAUNCHERS, 0) + 1;
		collection.updateMany(Filters.eq(ID, destructorID), 
								Updates.set(DESTRUCTED_LAUNCHERS, destructedLaunchers));    
		
		collection = database.getCollection(MISSILE_LAUNCHERS);
		collection.updateMany(Filters.eq(ID, launcherID), 
								Updates.set(IS_DESTRUCTED, true));    
	}
}