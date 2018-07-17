package Handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import Logic.Game;
import Logic.Missile;
import Logic.MissileDestructor;
import Logic.MissileLauncher;
import Logic.MissileLauncherDestructor;

public class ConfigHandler {

	Game theGame;
	HashMap<String, MissileLauncher> launchers = new HashMap<>();
	public  void readObjectsFromJSONFile() throws InterruptedException {
		theGame = Game.getInstance();
	
		// create ObjectMapper instance
		ObjectMapper objectMapper = new ObjectMapper();

		try {

			// read json file data to String
			byte[] jsonData = Files.readAllBytes(Paths.get("config.json")); // full path
																			// "C:\\Users\\victo\\git\\Java-Projects\\WarFare\\config.json"

			// read JSON like DOM Parser
			JsonNode rootNode = objectMapper.readTree(jsonData);
			JsonNode warNode = rootNode.path("war");

			// ****** reading missile launchers ********

			JsonNode missileLaunchers_Node = warNode.path("missileLaunchers");
			JsonNode launchersNode = missileLaunchers_Node.path("launcher");
			Iterator<JsonNode> launcher_elements = launchersNode.elements();

			while (launcher_elements.hasNext()) {

				// reading a missileLauncher JSONobject
				JsonNode launcher = launcher_elements.next();

				// reading a missileLauncher JSONobject's attributes
				JsonNode launcherId_Node = launcher.path("id");
				JsonNode launcherIsHidden_Node = launcher.path("isHidden");

				// creating a MissileLauncher Object
				MissileLauncher launcherObject = new MissileLauncher(launcherId_Node.asText(),
						launcherIsHidden_Node.asBoolean());

				// getting missiles for launcher
				JsonNode launcherMissiles_Node = launcher.path("missile");
				Iterator<JsonNode> missile_elements = launcherMissiles_Node.elements();

				while (missile_elements.hasNext()) { // *** reading missiles *****

					// reading a missile JSONobject
					JsonNode missile = missile_elements.next();

					// reading a missile JSONobject's attributes
					JsonNode missileId_Node = missile.path("id");
					JsonNode missileDestination_Node = missile.path("destination");
					JsonNode missileLaunchTime_Node = missile.path("launchTime");
					JsonNode missileFlyTime_Node = missile.path("flyTime");
					JsonNode missileDamage_Node = missile.path("damage");

					// creating a Missile Object
					Missile missileObject = new Missile(missileId_Node.asText(), missileDestination_Node.asText(),
						missileFlyTime_Node.asInt(),missileLaunchTime_Node.asInt(),missileDamage_Node.asInt(), launcherObject);
					launcherObject.addMissile(missileObject);
				}
			launchers.put(launcherObject.getId(), launcherObject);
			theGame.addMissileLauncherFromConfig(launcherObject);

			}

			// ****** reading missile destructors ********

			JsonNode missileDestructors_Node = warNode.path("missileDestructors");
			JsonNode missileDestructorsNode = missileDestructors_Node.path("destructor");
			Iterator<JsonNode> missileDestructor_elements = missileDestructorsNode.elements();

			while (missileDestructor_elements.hasNext()) {

				// reading a missileDestructor JSONobject
				JsonNode missiledestructor = missileDestructor_elements.next();

				// reading a missileDestructor JSONobject's attributes
				JsonNode missiledestructorId_Node = missiledestructor.path("id");

				// creating a MissileDestructor Object
				MissileDestructor MissileDestructorObject = new MissileDestructor(missiledestructorId_Node.asText());

				// getting destructed missiles of MissileDestructor
				JsonNode destructedMissiles_Node = missiledestructor.path("destructdMissile");
				Iterator<JsonNode> destructedMissiles_elements = destructedMissiles_Node.elements();
				HashMap<String, Integer> targets = new HashMap<>();
				while (destructedMissiles_elements.hasNext()) { // *** reading DMissiles *****

					// reading a destructedMissile JSONobject
					JsonNode destructedMissile = destructedMissiles_elements.next();

					// reading a destructedMissile JSONobject's attributes
					JsonNode destructedMissileId_Node = destructedMissile.path("id");
					JsonNode destructAfterLaunch_Node = destructedMissile.path("destructAfterLaunch");
					
					
					// creating s DMissile Object
					targets.put(destructedMissileId_Node.asText(), destructAfterLaunch_Node.asInt());
					
				}
				MissileDestructorObject.setMissilesToDestruct(targets);
				theGame.addMissileDestructorFromConfig(MissileDestructorObject);
			}


			// ****** reading missile launcher destructors ********

			JsonNode missileLauncherDestructors_Node = warNode.path("missileLauncherDestructors");
			JsonNode launcherDestructorsNode = missileLauncherDestructors_Node.path("destructor");
			Iterator<JsonNode> missileLauncherDestructor_elements = launcherDestructorsNode.elements();
			
			while (missileLauncherDestructor_elements.hasNext()) {

				// reading a missileLauncherDestructor JSONobject
				JsonNode missileLauncherDestructor_JSONobject = missileLauncherDestructor_elements.next();

				// reading a missileLauncherDestructor JSONobject's attributes
				JsonNode missileLauncherDestructorType_Node = missileLauncherDestructor_JSONobject.path("type");

				// creating a missileLauncherDestructor Object
				MissileLauncherDestructor MissileLauncherDestructorObject = new MissileLauncherDestructor(missileLauncherDestructorType_Node.asText());

				// getting destructed launchers of MissileDestructor
				JsonNode destructedMissileLaunchers_Node = missileLauncherDestructor_JSONobject
						.path("destructedLanucher");
				Iterator<JsonNode> destructedLaunchers_elements = destructedMissileLaunchers_Node.elements();
				HashMap<Integer,MissileLauncher> targetsD = new HashMap<>();

				
				while (destructedLaunchers_elements.hasNext()) { // *** reading DLaunchers *****

					// reading a destructedLauncher JSONobject
					JsonNode destructedLauncher = destructedLaunchers_elements.next();

					// reading a destructedLauncher JSONobject's attributes
					JsonNode destructedLauncherId_Node = destructedLauncher.path("id");
					JsonNode destructAfterLaunchDestructTime_Node = destructedLauncher.path("destructTime");
					// creating s DLauncher Object
					targetsD.put(destructAfterLaunchDestructTime_Node.asInt(), launchers.get(destructedLauncherId_Node.asText()));
					
				
				}
				MissileLauncherDestructorObject.setLaunchersToDestruct(targetsD);
				
				theGame.addMissileLauncherDestructorFromConfig(MissileLauncherDestructorObject);
			}
			theGame.startAllObjects();
		

		} catch (IOException e) {
			e.getMessage();
		}
	}
}
