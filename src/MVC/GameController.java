package MVC;

import DB.SqlDB;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import DB.IDB;
import Logic.Game;
import UI.GameUI;

public class GameController implements GameModelEventsListener, GameUIEventsListener {

	private Game 				theGame;
	private GameUI 				theGameUI;
	
	private ApplicationContext	theContext;
	private IDB 				db;

	
	public GameController(Game theGame, GameUI theGameUI) {
		this.theGame = theGame;
		this.theGameUI = theGameUI;

		theGame.registerListener(this);
		theGameUI.registerListener(this);
		
		theContext = new ClassPathXmlApplicationContext("spring.xml");
		db = (IDB) theContext.getBean("dataBase");
	}
	
	@Override
	public void addMissileLauncherInModel(String id,boolean isHidden) {
		 theGameUI.showAddMissileLauncher(id,isHidden);
		 db.addMissileLuauncher(id , isHidden);
	}

	@Override
	public void addMissileLauncherDestructorInModel(String id, String type) {
		 theGameUI.showAddMissileLauncherDestructor(type);
		 db.addLauncherDestructor(id, type);
	}

	@Override
	public void addMissileDestructorInModel(String id) {
		 theGameUI.showAddMissileDestructor(id);
		 db.addMissileDestructor(id);
	}

	@Override
	public void addMissileLauncherFromUI(String id) {
		theGame.addMissileLauncher(id);

	}

	@Override
	public void addMissileLauncherDestructorFromUI(String type) {
		theGame.addMissileLauncherDestructor(type);

	} 

	@Override
	public void addMissileDestructorFromUI(String id) {
		theGame.addMissileDestructor(id);

	}

	@Override
	public 	void launchMissileInModel(String missileLauncherId, String missileId, String destination, int damage,int flytime) {
		theGameUI.showMissileLaunch(missileLauncherId, missileId, destination, damage, flytime);
		db.addMissileLaunch(missileLauncherId);
	}

	@Override
	public void destructMissileLauncherInModel(String id, String type,String missileLauncherId) {
		theGameUI.showDestructMissileLauncher(type,missileLauncherId);
	}

	@Override
	public void destructMissileInModel(String missileIdToDestruct, String missileDestructorId, int waitingTime) {
			theGameUI.showDestructMissile(missileIdToDestruct,missileDestructorId,waitingTime);
	}

	@Override
	public void missileResultInModel(String missileId, boolean isHit, boolean isDestructed,boolean isHidden,String launcherId) {
		theGameUI.showMissileResult(missileId,isHit, isDestructed,isHidden,launcherId);
		if ( isHit )
			db.addMissileHit(launcherId);
	}
	
	@Override
	public void launchMissileFromUI(String missileLauncherId, String missileId, String destination, int damage) {
		try {
			theGame.launchMissile(missileLauncherId, missileId, destination, damage);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void destructMissileLauncherFromUI(String missileLaucherDestructType, String missileLaucherDestructId) {
		try {
			theGame.destructMissileLauncher(missileLaucherDestructType, missileLaucherDestructId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	@Override
	public void missileLauncherDestructResultInModel(String id, String type , String missileLauncherId ,boolean isDestructed) {
		theGameUI.showLauncherDestructResult(type,missileLauncherId,isDestructed);
		if ( isDestructed )
			db.addLauncherDestruct(id, missileLauncherId);
	}
	
	@Override
	public void missileDestructResultInModel(String destructorID ,boolean isDestructed) {
		if ( isDestructed )
			db.addMissileDestruct(destructorID);
	}

	@Override
	public void destructMissileFromUI(String missileIdToDestruct, String missileDestructorId) {
		theGame.destructMissile(missileIdToDestruct, missileDestructorId);

	}

	

	@Override
	public void notifyFailedAddMissileLauncherInModel(String message) {
		theGameUI.showMessage(message);
		
	}

	@Override
	public void notifyFailedAddMissileLauncherDestructorInModel(String message) {
		theGameUI.showMessage(message);
		
	}

	@Override
	public void notifyFailedAddMissileDestructorInModel(String message) {
		theGameUI.showMessage(message);
		
	}

	@Override
	public void viewGameStatusFromUI() {
		theGameUI.showMessage(theGame.showTotalSumarry());
		
	}

	@Override
	public void notifyFailedLaunchMissileInModel(String message) {
		theGameUI.showMessage(message);
		
	}

	@Override
	public void notifyFailedDestructMissileInModel(String message) {
		theGameUI.showMessage(message);
		
	}

	@Override
	public void notifyFailedDestructMissileLaucherInModel(String message) {
		theGameUI.showMessage(message);
		
	}

	@Override
	public void exitFromUI() {
		db.closeDB();
	}



	

}
