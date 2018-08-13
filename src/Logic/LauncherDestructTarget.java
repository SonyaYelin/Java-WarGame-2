package Logic;


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
		return target.getID();
	}
	
	public String getType() {
		return theDestructor.getType();
	}
	
	public MissileLauncher getTarget() {
		return target;
	}
	
	public MissileLauncherDestructor getDestructor() {
		return theDestructor;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(waitingTime*1000);
			
			if(!theDestructor.isGameOver() && !target.isGameOver()) {

				if (!target.isHidden()) 
					target.setIsDestroyed(true);
				
				theDestructor.notifyAllListenerResult(this);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
