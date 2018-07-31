package Logic;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

public class MissileLauncher implements Runnable {

	private String id;
	private boolean isHidden;
	private Vector<Missile> missilesToLaunch;
	private Queue<Missile> waitingMissiles = new PriorityQueue<Missile>();
	private Vector<MissileLaunchListener> listeners;
	private boolean isDestroyed = false;
	private int launchedMissileCounter =0;

	public MissileLauncher(String id, boolean isHidden) {
		this.id = id;
		this.isHidden = isHidden;
		this.missilesToLaunch = new Vector<Missile>();
		this.listeners = new Vector<MissileLaunchListener>();
	}

	public MissileLauncher(String id) {
		this.id = id;
		// seed if hidden
		Random random = new Random();
		this.isHidden = random.nextBoolean();
		this.missilesToLaunch = new Vector<Missile>();
		this.listeners = new Vector<MissileLaunchListener>();
	}

	public String getID() {
		return id;
	}

	public void setIsDestroyed(boolean val) {
		this.isDestroyed = val;
		
	}

	public boolean isDestroyed() {
		return this.isDestroyed;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void registerListener(MissileLaunchListener newListener) {
		listeners.add(newListener);
	}

	public void notifyAllLaunchListener(Missile missileFly) {
		int size = listeners.size();
		for (int i = 0; i < size; i++)
			listeners.elementAt(i).onMissileLaunchEvent(missileFly);
	}
	public void notifyAllLandListener(Missile missileLnd) {
		int size = listeners.size();
		for (int i = 0; i < size; i++)
			listeners.elementAt(i).onMissileLandEvent(missileLnd);
	}


	public void addMissile(Missile newMissile) throws InterruptedException {
		missilesToLaunch.add(newMissile);

		newMissile.start();
	}

	public synchronized void addWaitingMissile(Missile missile) {
		waitingMissiles.add(missile);

//		GameLogger.log(this, Level.INFO, "After adding missile #" + missile.getMissileId() + " there are " + waitingMissiles.size()
//		+ " missiles waiting in " + id);

		synchronized (/* dummyWaiter */this) {
			if (waitingMissiles.size() == 1) {
				/* dummyWaiter. */notify(); // to let know there is an missile
											// waiting
			}
		}
	}

	public synchronized void launchMissile() throws InterruptedException {
		Missile firstMissile = waitingMissiles.poll();
		boolean wasHidden = false;
		if (isHidden)
			wasHidden = true;
		if (firstMissile != null) {
			//GameLogger.log(this, Level.INFO, "MissileLauncher " + id + " is notifying Missile #" + firstMissile.getMissileId());
			synchronized (firstMissile) {
				firstMissile.notifyAll();
				launchedMissileCounter++;
				isHidden = false;
			}
		}

		synchronized (this) {
			try {
				//GameLogger.log(this, Level.INFO,"MissileLauncher " + id + " waits that missile #" + firstMissile.getMissileId()
				//+ " will land/be destructed");

				notifyAllLaunchListener(firstMissile);

				wait(); // wait till the missile finishes
//				GameLogger.log(this, Level.INFO,"Missile Launcher " + id + " was announced that Missile #"
//						+ firstMissile.getMissileId() + " is landed/destructed ");
				if (wasHidden)
					isHidden = true;
				notifyAllLandListener(firstMissile);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getLaunchedMissileCounter()
	{
		return launchedMissileCounter;
	}
	
	public Vector<Missile> getMissilesToLaunch() {
		return missilesToLaunch;
	}

	public void run() {
		//GameLogger.log(this, Level.INFO,"In Missile Launcher " + id + " ::run");
		while (!isDestroyed) {
			try {
				if (!waitingMissiles.isEmpty()) {
					launchMissile();
					
				} else {
					synchronized (this) {
						//GameLogger.log(this, Level.INFO,"Missile Launcher " + id + " has no missiles waiting");

						wait(); // wait till there is a missile waiting

						// gets notified
						//GameLogger.log(this, Level.INFO,"Missile Launcher " + id + " was notified there is a missile waiting");


					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//gets destroyed
		//GameLogger.log(this, Level.INFO,"Missile Launcher " + id + " was Desrructed");
		Thread.currentThread().interrupt();

	}

}
