package Logic;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import Logger.ToLog;
import MVC.GameModelEventsListener;

public class Game implements MissileLaunchListener, LauncherDestructListener, MissileDestructListener {


	private static final int RANDOM_BOUND = 10;
	private final static int MAX_NUM_OF_MISSILE_LAUNCHER = 5;
	private final static int MAX_NUM_OF_MISSILE_LAUNCHER_DESTRUCTOR = 10;
	private final static int MAX_NUM_OF_MISSILE_DESTRUCTOR = 5;

	// singleton object
	private static Game theGame;

	private Vector<Thread> threadList = new Vector<>();
	
	private HashMap<String, MissileLauncher> missileLaunchers;
	private HashMap<String, MissileDestructor> missileDestructors;
	private HashMap<String, MissileLauncherDestructor> missileLauncherDestructors;
	private Vector<GameModelEventsListener> allListeners;

	public Game() {
		allListeners = new Vector<GameModelEventsListener>();
		missileLaunchers = new HashMap<String, MissileLauncher>();
		missileDestructors = new HashMap<String, MissileDestructor>();
		missileLauncherDestructors = new HashMap<String, MissileLauncherDestructor>();
		
	}

	public static Game getInstance() {
		if (theGame == null) {
			// synchronized block to remove overhead
			synchronized (Game.class) {
//				if (theGame == null) {
//					ApplicationContext theContext = new ClassPathXmlApplicationContext(LOGS_CONFIG);
//					theGame = (Game) theContext.getBean(GAME);
//				}
				theGame = new Game();
			}
		}
		return theGame;
	}

	public void registerListener(GameModelEventsListener listener) {
		allListeners.add(listener);
	}

	public void addMissileLauncherFromConfig(MissileLauncher missileLauncher) {
		try {
			if (missileLaunchers.size() < MAX_NUM_OF_MISSILE_LAUNCHER
					&& !missileLaunchers.containsKey(missileLauncher.getID())) {
				this.addMissileLauncher(missileLauncher);
			} else
				fireNotificationFailedAddMissileLauncher("Too Many Missile Launchers / already exist");
		} catch (Exception e) {
			fireNotificationFailedAddMissileLauncher(e.getMessage());
		}
	}

	public void addMissileLauncher(String id) {
		try {
			if (missileLaunchers.size() < MAX_NUM_OF_MISSILE_LAUNCHER && !missileLaunchers.containsKey(id)) {
				MissileLauncher missileLauncher = new MissileLauncher(id);
				Thread newmlT = new Thread(missileLauncher);
				newmlT.start();
				threadList.add(newmlT);
				addMissileLauncher(missileLauncher);
			} else
				fireNotificationFailedAddMissileLauncher("Too Many Missile Launchers/already exist");
		} catch (Exception e) {
			fireNotificationFailedAddMissileLauncher(e.getMessage());
		}
	}

	@ToLog
	private void addMissileLauncher(MissileLauncher missileLauncher) {
		try {
			missileLaunchers.put(missileLauncher.getID(), missileLauncher);
			missileLauncher.registerListener(theGame);
			fireAddMissileLauncher(missileLauncher.getID(), missileLauncher.isHidden());

		} catch (Exception e) {
			fireNotificationFailedAddMissileLauncher(e.getMessage());
		}
	}

	public void addMissileDestructorFromConfig(MissileDestructor destructor) {
		try {
			if (missileDestructors.size() < MAX_NUM_OF_MISSILE_DESTRUCTOR
					&& !missileDestructors.containsKey(destructor.getID())) {
				addMissileDestructor(destructor);
			} else
				fireNotificationFailedAddMissileDestructor("Too Many Missile Destructors/already exist");
		} catch (Exception e) {
			fireNotificationFailedAddMissileDestructor(e.getMessage());
		}
	}

	public void addMissileDestructor(String id) {
		try {
			if (missileDestructors.size() < MAX_NUM_OF_MISSILE_DESTRUCTOR && !missileDestructors.containsKey(id)) {
				MissileDestructor newmd = new MissileDestructor(id);
				Thread newmdT = new Thread(newmd);
				newmdT.start();
				threadList.add(newmdT);

				addMissileDestructor(newmd);
			} else
				fireNotificationFailedAddMissileDestructor("Too Many Missile Destructors/already exist");
		} catch (Exception e) {
			fireNotificationFailedAddMissileDestructor(e.getMessage());
		}
	}

	@ToLog
	public void addMissileDestructor(MissileDestructor md) {
		missileDestructors.put(md.getID(), md);
		md.registerListener(theGame);
		for (MissileLauncher launcher : missileLaunchers.values()) {
			launcher.registerListener(md);
		}
		fireAddMissileDestructor(md.getID());
	}

	public void addLauncherDestructorFromConfig(MissileLauncherDestructor missileLauncherDestructor) {
		try {
			if (missileLauncherDestructors.size() < MAX_NUM_OF_MISSILE_LAUNCHER_DESTRUCTOR
					&& !missileLauncherDestructors.containsKey(missileLauncherDestructor.getType())) {
				addLauncherDestructor(missileLauncherDestructor);
			} else
				fireNotificationFailedAddMissileLauncherDestructor(
						"Too Many Missile Launcher Destructors/already exist");
		} catch (Exception e) {
			fireNotificationFailedAddMissileLauncherDestructor(e.getMessage());
		}
	}

	public void addLauncherDestructor(String type) {
		try {
			if (missileLauncherDestructors.size() < MAX_NUM_OF_MISSILE_LAUNCHER_DESTRUCTOR
					&& !missileLauncherDestructors.containsKey(type)) {
				MissileLauncherDestructor newmld = new MissileLauncherDestructor(type);
				Thread newmldT = new Thread(newmld);
				newmldT.start();
				threadList.add(newmldT);

				addLauncherDestructor(newmld);
			} else
				fireNotificationFailedAddMissileLauncherDestructor(
						"Too Many Missile Launcher Destructors/already exist");
		} catch (Exception e) {
			fireNotificationFailedAddMissileLauncherDestructor(e.getMessage());
		}
	}

	@ToLog
	public void addLauncherDestructor(MissileLauncherDestructor mld) {
		mld.registerListener(theGame);
		missileLauncherDestructors.put(mld.getType(), mld);
		fireAddMissileLauncherDestructor(mld.getID(), mld.getType());
	}
	
	public void startAllObjects() {
		for (MissileLauncher launcher : missileLaunchers.values()) {
			Thread newmlT = new Thread(launcher);
			newmlT.start();
			threadList.add(newmlT);

		}

		for (MissileDestructor destructor : missileDestructors.values()) {
			Thread newmD = new Thread(destructor);
			newmD.start();
			threadList.add(newmD);
		}
		for (MissileLauncherDestructor launcherDestructor : missileLauncherDestructors.values()) {
			Thread newmlD = new Thread(launcherDestructor);
			newmlD.start();
			threadList.add(newmlD);
		}
	}

	public void launchMissile(String missileLauncherId, String missileId, String destination, int damage)
			throws InterruptedException {
		Random r = new Random();
		if (missileLaunchers.containsKey(missileLauncherId)) {
			Missile newMissile = new Missile(missileId, destination, r.nextInt(RANDOM_BOUND) + 1, 0, damage,
					missileLaunchers.get(missileLauncherId));
			missileLaunchers.get(missileLauncherId).addMissile(newMissile);
		} else {
			fireNotificationFailedLaunchMissile("Missile Launcher " + missileLauncherId + " Not Exist");
		}
	}

	public void destructMissileLauncher(String missileLaucherDestructType, String missileLaucherDestructId)
			throws InterruptedException {
		if (missileLauncherDestructors.containsKey(missileLaucherDestructType)) {
			missileLauncherDestructors.get(missileLaucherDestructType)
					.addlauncherToDestruct(missileLaunchers.get(missileLaucherDestructId), 0);
		} else {
			fireNotificationFailedDestructMissileLaucher(
					"Missile Launcher Destructor " + missileLaucherDestructType + " Not Exist");
		}
	}

	public void destructMissile(String missileIdToDestruct, String missileDestructorId) {
		if (missileDestructors.containsKey(missileDestructorId)) {
			Random r = new Random();
			missileDestructors.get(missileDestructorId).addMissileToDestruct(missileIdToDestruct,
					r.nextInt(RANDOM_BOUND) + 1);
		} else {
			fireNotificationFailedDestructMissile("Missile  Destructor " + missileDestructorId + " Not Exist");
		}
	}

	private void fireAddMissileLauncher(String id, boolean isHidden) {
		for (GameModelEventsListener g : allListeners) {
			g.addMissileLauncherInModel(id, isHidden);
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
			g.launchMissileInModel(missile.getTheLauncher().getID(), missile.getMissileId(), missile.getDestination(),
					missile.getDamage(), missile.getFlyTime());
		}
	}

	private void fireDestructMissileLauncher(LauncherDestructTarget target) {
		for (GameModelEventsListener g : allListeners) {
			g.destructMissileLauncherInModel(target.getDestructor().getID(), target.getType(), target.getTargetID());
		}
	}

	private void fireDestructMissile(DestructTarget target) {
		for (GameModelEventsListener g : allListeners) {
			g.destructMissileInModel(target.getTarget().getMissileId(), target.getDestructor().getID(),
					target.getWaitingTime());
		}
	}

	private void fireMissileLand(Missile missile) {
		for (GameModelEventsListener g : allListeners) {
			g.missileResultInModel(missile.getMissileId(), missile.isHitTarget(), missile.isDestructed(),
					missile.getTheLauncher().isHidden(), missile.getTheLauncher().getID());
		}
	}

	private void fireLauncherDestructResult(LauncherDestructTarget target) {
		for (GameModelEventsListener g : allListeners) {
			g.missileLauncherDestructResultInModel(target.getDestructor().getID(), target.getType(),
					target.getTarget().getID(), target.getTarget().isDestroyed());
		}
	}

	private void fireMissileDestructResult(DestructTarget target) {
		for (GameModelEventsListener g : allListeners) {
			g.missileDestructResultInModel(target.getDestructor().getID(), target.getTarget().isDestructed());
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

	private String printTotalSumarry(int hitMissile, int launchMissileCounter, int totalDamage,
			int missileLauncherDestroy) {
		StringBuilder builder = new StringBuilder();
		builder.append("Number of missile hits: " + hitMissile + "\n");
		builder.append("Number of missile that were launched: " + launchMissileCounter + "\n");
		builder.append("Total damage: " + totalDamage + "\n");
		builder.append("Number of missile launcher destroy destroyed: " + missileLauncherDestroy + "\n");
		return builder.toString();

	}

	@ToLog
	public void onMissileLaunchEvent(Missile launchedMissile) {
		fireMissileLaunch(launchedMissile);
	}

	@ToLog
	public void onMissileLandEvent(Missile landMissile) {
		fireMissileLand(landMissile);
	}
	
	//launcher destruct
	@ToLog
	public void onLauncherDestructorLaunchEvent(LauncherDestructTarget target) {
		fireDestructMissileLauncher(target);

	}
	
	@Override
	public void onLauncherDestructResult(LauncherDestructTarget target) {
		fireLauncherDestructResult(target);
	}

	//missile destruct
	@ToLog
	public void onMissileDestructorLaunchEvent(DestructTarget target) {
		fireDestructMissile(target);

	}

	@ToLog
	public void onMissileDestructResult(DestructTarget target) {
		fireMissileDestructResult(target);
	}
	
	@ToLog
	public void onExit() {
		for(MissileDestructor md: missileDestructors.values()) 
			md.endGame();
		for(MissileLauncherDestructor mld: missileLauncherDestructors.values()) 
			mld.endGame();
		for(MissileLauncher ml: missileLaunchers.values()) 
			ml.endGame();
		
	}

}
