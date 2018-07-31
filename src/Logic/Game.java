package Logic;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import DB.MongoDB;
import DB.SqlDB;
import DB.IDB;
import Logger.GameLogger;
import Logger.ToLog;
import MVC.GameModelEventsListener;

public class Game implements MissileLaunchListener,LauncherDestructListener,MissileDestructListener {

	private HashMap<String, MissileLauncher> 			missileLaunchers;
	private HashMap<String, MissileDestructor> 			missileDestructors;
	private HashMap<String, MissileLauncherDestructor>	missileLauncherDestructors;
	private Vector<GameModelEventsListener> 			allListeners;
	
	private static final int 							RANDOM_BOUND = 10;
	final static int 									MAX_NUM_OF_MISSILE_LAUNCHER = 5;
	final static int 									MAX_NUM_OF_MISSILE_LAUNCHER_DESTRUCTOR = 10;
	final static int 									MAX_NUM_OF_MISSILE_DESTRUCTOR = 5;

	// singleton object
	private static Game 								theGame;

	
	private Game() {
		allListeners = new Vector<GameModelEventsListener>();
		missileLaunchers = new HashMap<String, MissileLauncher>();
		missileDestructors = new HashMap<String, MissileDestructor>();
		missileLauncherDestructors = new HashMap<String, MissileLauncherDestructor>();
		GameLogger.removeConsoleHandler();
		GameLogger.addFileHandler(this, "game");
	}

	public static Game getInstance() {
		if (theGame == null) {
			// synchronized block to remove overhead
			synchronized (Game.class) {
				if (theGame == null) {
					ApplicationContext theContext = new ClassPathXmlApplicationContext("spring.xml");
					
					// if theGame is null, initialize
					theGame = (Game) theContext.getBean("Game");
					//theGame = new Game();
				}
			}
		}
		return theGame;
	}

	public void registerListener(GameModelEventsListener listener) {
		allListeners.add(listener);
	}

	public void addMissileLauncherFromConfig(MissileLauncher missileLauncher) {
		try {
			if(missileLaunchers.size()<MAX_NUM_OF_MISSILE_LAUNCHER && !missileLaunchers.containsKey(missileLauncher.getId())){
				missileLaunchers.put(missileLauncher.getId(), missileLauncher);
				missileLauncher.registerListener(theGame);
				fireAddMissileLauncher(missileLauncher.getId(),missileLauncher.isHidden());
			}
			else
				fireNotificationFailedAddMissileLauncher("Too Many Missile Launchers / already exist");
		} catch (Exception e) {
			fireNotificationFailedAddMissileLauncher(e.getMessage());
		}
	}
	public void addMissileLauncher(String id) {
		try {
			if(missileLaunchers.size()<MAX_NUM_OF_MISSILE_LAUNCHER && !missileLaunchers.containsKey(id)){
				MissileLauncher newml = new MissileLauncher(id);
				missileLaunchers.put(id, newml);
				newml.registerListener(theGame);
				Thread newmlT = new Thread(newml);
				newmlT.start();
				fireAddMissileLauncher(id,newml.isHidden());
			}
			else
				fireNotificationFailedAddMissileLauncher("Too Many Missile Launchers/already exist");
		} catch (Exception e) {
			fireNotificationFailedAddMissileLauncher(e.getMessage());
		}
	}

	public void addMissileDestructorFromConfig(MissileDestructor destructor) {
		try {
				if(missileDestructors.size()<MAX_NUM_OF_MISSILE_DESTRUCTOR &&  !missileDestructors.containsKey(destructor.getId())){
					missileDestructors.put(destructor.getId(), destructor);
					destructor.registerListener(theGame);

					for (MissileLauncher launcher : missileLaunchers.values()) {
						launcher.registerListener(destructor);
					}
					fireAddMissileDestructor(destructor.getId());
				}
				else
					fireNotificationFailedAddMissileDestructor("Too Many Missile Destructors/already exist");
			} catch (Exception e) {
				fireNotificationFailedAddMissileDestructor(e.getMessage());
			}
		
	}

		public void addMissileDestructor(String id) {
			try {
				if(missileDestructors.size()<MAX_NUM_OF_MISSILE_DESTRUCTOR && !missileDestructors.containsKey(id)){
				MissileDestructor newmd = new MissileDestructor(id);
				missileDestructors.put(id, newmd);
				Thread newmdT = new Thread(newmd);
				newmdT.start();
				newmd.registerListener(theGame);
				for (MissileLauncher launcher : missileLaunchers.values()) {
					launcher.registerListener(newmd);
				}
				fireAddMissileDestructor(id);
				}
				else
					fireNotificationFailedAddMissileDestructor("Too Many Missile Destructors/already exist");
			} catch (Exception e) {
				fireNotificationFailedAddMissileDestructor(e.getMessage());
			}
		}

		public void addMissileLauncherDestructorFromConfig(MissileLauncherDestructor missileLauncherDestructor) {
			try {
				if(missileLauncherDestructors.size()<MAX_NUM_OF_MISSILE_LAUNCHER_DESTRUCTOR && !missileLauncherDestructors.containsKey(missileLauncherDestructor.getType())){
				missileLauncherDestructors.put(missileLauncherDestructor.getType(), missileLauncherDestructor);
				missileLauncherDestructor.registerListener(theGame);
				fireAddMissileLauncherDestructor(missileLauncherDestructor.getID(), missileLauncherDestructor.getType());
				}
				else
					fireNotificationFailedAddMissileLauncherDestructor("Too Many Missile Launcher Destructors/already exist");
			} catch (Exception e) {
				fireNotificationFailedAddMissileLauncherDestructor(e.getMessage());
			}
		}

		public void addMissileLauncherDestructor(String type) {
			try {
				if(missileLauncherDestructors.size()<MAX_NUM_OF_MISSILE_LAUNCHER_DESTRUCTOR && !missileLauncherDestructors.containsKey(type)){
				MissileLauncherDestructor newmld = new MissileLauncherDestructor(type);
				newmld.registerListener(theGame);
				missileLauncherDestructors.put(type, newmld);
				Thread newmldT = new Thread(newmld); 
				newmldT.start();
				fireAddMissileLauncherDestructor(newmld.getID(), type);
				}
				else
					fireNotificationFailedAddMissileLauncherDestructor("Too Many Missile Launcher Destructors/already exist");
			} catch (Exception e) {
				fireNotificationFailedAddMissileLauncherDestructor(e.getMessage());
			}
		}

		public void startAllObjects(){
			for (MissileLauncher launcher : missileLaunchers.values()) {
				Thread newmlT = new Thread(launcher);
				newmlT.start();
			}

			for (MissileDestructor destructor : missileDestructors.values()) {
				Thread newmD = new Thread(destructor);
				newmD.start();
			}
			for (MissileLauncherDestructor launcherDestructor : missileLauncherDestructors.values()) {
				Thread newmlD = new Thread(launcherDestructor);
				newmlD.start();
			}
		}

		public void launchMissile(String missileLauncherId, String missileId, String destination, int damage)
				throws InterruptedException {
			Random r = new Random();
			if(missileLaunchers.containsKey(missileLauncherId)){
				Missile newMissile = new Missile(missileId, destination, r.nextInt(RANDOM_BOUND)+1, 0, damage,
					missileLaunchers.get(missileLauncherId));
				missileLaunchers.get(missileLauncherId).addMissile(newMissile);
			}
			else{
				fireNotificationFailedLaunchMissile("Missile Launcher "+missileLauncherId+ " Not Exist" );
			}
		}

		public void destructMissileLauncher(String missileLaucherDestructType, String missileLaucherDestructId)
				throws InterruptedException {
			if(missileLauncherDestructors.containsKey(missileLaucherDestructType)){
			missileLauncherDestructors.get(missileLaucherDestructType)
			.addlauncherToDestruct(missileLaunchers.get(missileLaucherDestructId), 0);
			}
			else{
				fireNotificationFailedDestructMissileLaucher("Missile Launcher Destructor "+missileLaucherDestructType+ " Not Exist");
			}
		}

		public void destructMissile(String missileIdToDestruct, String missileDestructorId) {
			if(missileDestructors.containsKey(missileDestructorId)){
			Random r = new Random();
			missileDestructors.get(missileDestructorId).addMissileToDestruct(missileIdToDestruct, r.nextInt(RANDOM_BOUND)+1);
			}
			else{
				fireNotificationFailedDestructMissile("Missile  Destructor "+missileDestructorId+ " Not Exist");
			}
		}

		
		
		private void fireAddMissileLauncher(String id, boolean isHidden) {
			for (GameModelEventsListener g : allListeners) {
				g.addMissileLauncherInModel(id,isHidden);
			}
		}

		private void fireAddMissileDestructor(String id) {
			for (GameModelEventsListener g : allListeners) {
				g.addMissileDestructorInModel(id);
			}

		}

		private void fireAddMissileLauncherDestructor(String id, String type) {
			for (GameModelEventsListener g : allListeners) {
				g.addMissileLauncherDestructorInModel(id, type);
			}
		}

		private void fireNotificationFailedAddMissileLauncher(String message) {
			for (GameModelEventsListener g : allListeners) {
				 g.notifyFailedAddMissileLauncherInModel(message);
			}
		}

		private void fireNotificationFailedAddMissileLauncherDestructor(String message) {
			for (GameModelEventsListener g : allListeners) {
				 g.notifyFailedAddMissileLauncherDestructorInModel(message);
			}

		}

		private void fireNotificationFailedAddMissileDestructor(String message) {
			for (GameModelEventsListener g : allListeners) {
				 g.notifyFailedAddMissileDestructorInModel(message);
			}
		}
		
		private void fireNotificationFailedLaunchMissile(String message) {
			for (GameModelEventsListener g : allListeners) {
				 g.notifyFailedLaunchMissileInModel(message);
			}
		}
		private void fireNotificationFailedDestructMissile(String message) {
			for (GameModelEventsListener g : allListeners) {
				 g.notifyFailedDestructMissileInModel(message);
			}
		}
		private void fireNotificationFailedDestructMissileLaucher(String message) {
			for (GameModelEventsListener g : allListeners) {
				 g.notifyFailedDestructMissileLaucherInModel(message);
			}
		}

		private void fireMissileLaunch(Missile missile) {
			for (GameModelEventsListener g : allListeners) {
				g.launchMissileInModel(missile.getTheLauncher().getId(),missile.getMissileId(),missile.getDestination(),missile.getDamage(),missile.getFlyTime());
			}
		}

		private void fireDestructMissileLauncher(LauncherDestructTarget target) {
			for (GameModelEventsListener g : allListeners) {
				g.destructMissileLauncherInModel(target.getDestructor().getID(), target.getType(),target.getTargetID());
			}
		}
		private void fireDestructMissile(DestructTarget target) {
			for (GameModelEventsListener g : allListeners) {
				g.destructMissileInModel(target.getTarget().getMissileId(),target.getDestructor().getId(),target.getWaitingTime());
			}
		}
		private void fireMissileLand(Missile missile) {
			for (GameModelEventsListener g : allListeners) {
				g.missileResultInModel(missile.getMissileId(), missile.isHitTarget(), missile.isDestructed(),missile.getTheLauncher().isHidden(),missile.getTheLauncher().getId());
			}
		}
		
		private void fireLauncherDestructResult(LauncherDestructTarget target) {
			for (GameModelEventsListener g : allListeners) {
				g.missileLauncherDestructResultInModel(target.getDestructor().getID(), target.getType(),target.getTarget().getId(),target.getTarget().isDestroyed());
			}
		}
		
		private void fireMissileDestructResult(DestructTarget target) {
			for (GameModelEventsListener g : allListeners) {
				g.missileDestructResultInModel(target.getDestructor().getId() ,target.getTarget().isDestructed());
			}
		}
		
		public String showTotalSumarry() {
			int hitMissile = 0;
			int launchMissileCounter = 0;
			int totalDamage = 0;
			int missileLauncherDestroy = 0;

			for (MissileLauncher missileLauncher : missileLaunchers.values()) {
				// how many missile hit
				Vector<Missile> missiles = missileLauncher.getMissilesToLaunch();
				for (Missile m : missiles) {
					if (m.isHitTarget()) {
						hitMissile++;
						// total damage
						totalDamage += m.getDamage();
					}
				}
				// how many missile destroy
				if (missileLauncher.isDestroyed())
					missileLauncherDestroy++;
				// how many missile launch
				launchMissileCounter += missileLauncher.getLaunchedMissileCounter();
			}
			return printTotalSumarry(hitMissile, launchMissileCounter, totalDamage, missileLauncherDestroy);
		}

		private int[] getStatistics() {
			int missileHits = 0;
			int launchedMissiles = 0;
			int totalDamage = 0;
			int destructedLaunchers = 0;
			int destructedMissiles = 0;

			for (MissileLauncher missileLauncher : missileLaunchers.values()) {
				Vector<Missile> missiles = missileLauncher.getMissilesToLaunch();
				for (Missile m : missiles) {
					if (m.isHitTarget()) {
						missileHits++;
						totalDamage += m.getDamage();
					}
				}
				if (missileLauncher.isDestroyed())
					destructedLaunchers++;
				// how many missile launch
				launchedMissiles += missileLauncher.getLaunchedMissileCounter();
				destructedMissiles = launchedMissiles - missileHits;
			}
			int[] ret = {missileHits, launchedMissiles, totalDamage, destructedLaunchers, destructedMissiles};
			return ret;
		}
		
		
		private String printTotalSumarry(int hitMissile, int launchMissileCounter, int totalDamage,
				int missileLauncherDestroy) {
			StringBuilder builder = new StringBuilder();
			builder.append("Number of missile hits: " + hitMissile+"\n");
			builder.append("Number of missile that were launched: " + launchMissileCounter+"\n");
			builder.append("Total damage: " + totalDamage+"\n");
			builder.append("Number of missile launcher destroy destroyed: " + missileLauncherDestroy+"\n");
			return builder.toString();

		}


		@Override
		public void onLaunchEvent(Missile launchedMissile) {
			fireMissileLaunch(launchedMissile);

		}

		@Override
		public void onLaunchEvent(LauncherDestructTarget target) {
			fireDestructMissileLauncher(target);

		}

		@Override
		public void onLaunchEvent(DestructTarget target) {
			fireDestructMissile(target);

		}

		@Override
		public void onLandEvent(Missile landMissile) {
			fireMissileLand(landMissile);

		}

		@Override
		public void onDestructResult(LauncherDestructTarget target) {
			fireLauncherDestructResult(target);
		}
		
		@Override
		public void onDestructResult(DestructTarget target) {
			fireMissileDestructResult(target);
		}
		
		public void test() {
			
		}
	}
