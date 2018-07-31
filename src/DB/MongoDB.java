package DB;


import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class MongoDB implements IDB{

	
	private static MongoClient 		mongoClient;
	private static MongoDatabase	database;
	
	static {
		mongoClient = new MongoClient("localhost", 27017);
		database = mongoClient.getDatabase("war");
	}

	@Override
	public void closeDB() {
		mongoClient.close();		
	}

	@Override
	public void addMissileLuauncher(String id, boolean isHidden) {
		MongoCollection<Document> collection = database.getCollection("missile_launchers");

		Document doc = new Document("id", id)
	            		.append("is_hidden", isHidden)
	            		.append("is_destructed", false)
						.append("launched_missiles", 0)
						.append("missile_hits", 0);

		collection.insertOne(doc);
	}

	@Override
	public void addMissileDestructor(String id) {
		MongoCollection<Document> collection = database.getCollection("missile_destructors");

		Document doc = new Document("id", id).append("destructed_missiles", 0);
	
		collection.insertOne(doc);
	}

	@Override
	public void addLauncherDestructor(String id, String type) {
		MongoCollection<Document> collection = database.getCollection("launcher_destructors");

		Document doc = new Document("id", id).append("type", type).append("destructed_launchers", 0);
	
		collection.insertOne(doc);
	}

	@Override
	public void addMissileLaunch(String id) {
		MongoCollection<Document> collection = database.getCollection("missile_launchers");
		Document d = collection.find(Filters.eq("id", id)).first();
		
		int launchedMissiles = d.getInteger("launched_missiles", 0) + 1;
		collection.updateMany(Filters.eq("id", id), Updates.set("launched_missiles", launchedMissiles));    
	}

	@Override
	public void addMissileHit(String id) {
		MongoCollection<Document> collection = database.getCollection("missile_launchers");
		Document d = collection.find(Filters.eq("id", id)).first();
		
		int missileHits = d.getInteger("missile_hits", 0) + 1;
		collection.updateMany(Filters.eq("id", id), 
								Updates.set("missile_hits", missileHits));    
	}

	@Override
	public void addMissileDestruct(String id) {
		MongoCollection<Document> collection = database.getCollection("missile_destructors");
		Document d = collection.find(Filters.eq("id", id)).first();
		
		int destructedMissiles = d.getInteger("destructed_missiles", 0) + 1;
		collection.updateMany(Filters.eq("id", id), 
								Updates.set("destructed_missiles", destructedMissiles));    
	}

	@Override
	public void addLauncherDestruct(String destructorID, String launcherID) {
		MongoCollection<Document> collection = database.getCollection("launcher_destructors");
		Document d = collection.find(Filters.eq("id", destructorID)).first();
		
		int destructedLaunchers = d.getInteger("destructed_launchers", 0) + 1;
		collection.updateMany(Filters.eq("id", destructorID), 
								Updates.set("destructed_launchers", destructedLaunchers));    
		
		collection = database.getCollection("missile_launchers");
		collection.updateMany(Filters.eq("id", launcherID), 
								Updates.set("is_destructed", true));    
	}

	
//	public void getFromDB(String id, String from) {
//		MongoCollection<Document> collection = 	database.getCollection(from);
//
//		FindIterable<Document> ret = collection.find(Filters.eq("id", id));
//	}

//	@Override
//	public List<String> getAllTablesNames() {
//		return null; 
//	}
//
//	@Override
//	public Vector<String[]> getQueryData(String tableName, Vector<String> headers) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}