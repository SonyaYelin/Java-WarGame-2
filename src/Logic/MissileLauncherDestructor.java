package Logic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


public class MissileLauncherDestructor extends WarObject implements Runnable{

	private static int iDGen;
	
	private String type;
	private Map<Integer,MissileLauncher> launchersToDestruct = new HashMap<>();
	private Vector<LauncherDestructListener> listeners;

	public MissileLauncherDestructor(String type) {
		super(""+iDGen++);
		this.type = type;
		this.listeners = new Vector<LauncherDestructListener>();
	}
	
	
	public void addlauncherToDestruct(MissileLauncher newMissileLauncher,int waitingTime) throws InterruptedException {
		launchersToDestruct.put(waitingTime,newMissileLauncher);
		synchronized (this) {
			this.notify();
		}
	}
	
	public void setLaunchersToDestruct(Map<Integer, MissileLauncher> launchersToDestruct) {
		this.launchersToDestruct = launchersToDestruct;
	}
	
	public void registerListener(LauncherDestructListener newListener) {
		listeners.add(newListener);
	}

	public void notifyAllListenerLaunch(LauncherDestructTarget target) {
		int size = listeners.size();
		for (int i = 0; i < size; i++)
			listeners.elementAt(i).onLauncherDestructorLaunchEvent(target);
	}
	
	public void notifyAllListenerResult(LauncherDestructTarget target) {
		int size = listeners.size();
		for (int i = 0; i < size; i++)
			listeners.elementAt(i).onLauncherDestructResult(target);
	}
	public String getType() {
		return type;
	}
	
	@Override
	public void run() {
		while(!isGameOver()){
			if(!launchersToDestruct.isEmpty()){
				for(Iterator<Map.Entry<Integer, MissileLauncher>> it = launchersToDestruct.entrySet().iterator(); it.hasNext(); ) {
				      Map.Entry<Integer, MissileLauncher> entry = it.next();
				      LauncherDestructTarget target = new LauncherDestructTarget( entry.getValue(),entry.getKey(), this);
				      notifyAllListenerLaunch(target);  
				      it.remove();
				      }
			}
				else{
					synchronized (this) {
						try {
							setWaiting(true);
							wait();
							setWaiting(false);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
		}


	
	
}
