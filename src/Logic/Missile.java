package Logic;

import java.util.Random;
import java.util.logging.Level;


public class Missile extends Thread implements Comparable<Missile> {

	private String id;
	private String destination;
	private int flyTime;
	private int launchTime;
	private int damage;
	private boolean Destructed = false;
	private boolean willHit = true;
	private boolean hitTarget =false;
	private MissileLauncher theLauncher;

	public Missile(String id, String destination, int flyTime, int launchTime, int damage, MissileLauncher theLauncher) {
		Random r = new Random();

		this.id = id;
		this.destination = destination;
		this.flyTime = flyTime;
		this.launchTime = launchTime;
		this.damage = damage;
		this.theLauncher = theLauncher;
		this.willHit = r.nextBoolean();
	}
	
	public void launch() throws InterruptedException {
		synchronized (this) {
			Thread.sleep(launchTime * 1000);
			theLauncher.addWaitingMissile(this);
			//GameLogger.log(theLauncher, Level.INFO,"Missile #" + getMissileId() + " is waiting to launch ");
			// gets notified by launcher
			wait();
			//GameLogger.log(theLauncher, Level.INFO,"Missile #" + getMissileId() + " started launch to "+destination);
		}

	}

	public synchronized void fly() throws InterruptedException {
		synchronized (theLauncher) {
			//GameLogger.log(theLauncher, Level.INFO,"Missile #" + getMissileId() + " starts flying for " + flyTime + " seconds");
			synchronized (this) {
				wait(flyTime * 1000);// if missile was destroyed MissileDestructor notify
				if(Destructed)	{
					willHit = false;
					//GameLogger.log(theLauncher, Level.INFO,"Missile #" + getMissileId() + " was destructed");
				}
			}
			if(willHit){
				hitTarget=true;
				//GameLogger.log(theLauncher, Level.INFO,"Missile #" + getMissileId() + " hit target "+ destination +" damage: "+ damage);
			}
			else
				//GameLogger.log(theLauncher, Level.INFO,"Missile #" + getMissileId() + " missed target "+ destination );

			theLauncher.notify();
			

		}

	}

	public String getMissileId() {
		return id;
	}

	@Override
	public void run() {
		try {
			launch();
			fly();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public int getLaunchTime() {
		return launchTime;
	}
	
	public boolean isDestructed() {
		return Destructed;
	}
	
	public void setDestructed(boolean destructed) {
		Destructed = destructed;
	}
	
	public int getFlyTime() {
		return flyTime;
	}
	
	 public boolean isHitTarget() {
		return hitTarget;
	}
	 public int getDamage() {
		return damage;
	}
	 public MissileLauncher getTheLauncher() {
		return theLauncher;
	}
	 public String getDestination() {
		return destination;
	}
	
	@Override
	public int compareTo(Missile otherMissile) {
		if (this.launchTime > otherMissile.launchTime)
			return 1;
		else if (this.launchTime < otherMissile.launchTime)
			return -1;
		return 0;
	}

}
