package Logic;

import java.util.logging.Level;

import Logger.GameLogger;

public class LauncherDestructTarget extends Thread {
	private MissileLauncher target;
	private int waitingTime;
	private MissileLauncherDestructor theDestructor;

	public LauncherDestructTarget(MissileLauncher target,int waitingTime,MissileLauncherDestructor theDestructor) {
		this.target = target;
		this.waitingTime=waitingTime;
		this.theDestructor=theDestructor;
		this.start();
	}
	public String getTargetID() {
		return target.getId();
	}
	
	public String getType() {
		return theDestructor.getType();
	}
	
	public MissileLauncher getTarget() {
		return target;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(waitingTime*1000);
			
			GameLogger.log(theDestructor, Level.INFO,"Missile Launcher Desturctor "+theDestructor.getType() +" finish waiting to "+target.getId());

			if(!target.isHidden())
			{
				target.setIsDestroyed(true);
				GameLogger.log(theDestructor, Level.INFO,"Missile Launcher Desturctor "+theDestructor.getType() +" destoryed "+target.getId());
			}
			else
				GameLogger.log(theDestructor, Level.INFO,"Missile Launcher Desturctor "+theDestructor.getType() +" missed "+target.getId());
			
			theDestructor.notifyAllListenerResult(this);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
