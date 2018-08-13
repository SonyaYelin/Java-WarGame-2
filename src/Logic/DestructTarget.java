package Logic;


public class DestructTarget extends Thread {
	private Missile target;
	private int waitingTime;
	private MissileDestructor destructor;


	public DestructTarget(Missile target,int waitingTime,MissileDestructor destructor) {
		this.target = target;
		this.waitingTime=waitingTime;
		this.destructor=destructor;
		this.start();
	}

	
	public Missile getTarget() {
		return target;
	}


	public MissileDestructor getDestructor() {
		return destructor;
	}
	
	public int getWaitingTime() {
		return waitingTime;
	}


	@Override
	public void run() {
		try {
			Thread.sleep(waitingTime*1000);

			if(!destructor.isGameOver()) {
				if(waitingTime< target.getFlyTime() && !target.isDestructed())
				{
					synchronized (target) {
						target.setDestructed(true);
						target.notify();
						target.setDestructed(true);
					}
				}
					destructor.notifyAllListenerResult(this);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
