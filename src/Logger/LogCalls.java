package Logger;

import java.util.logging.Level;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

import Logic.DestructTarget;
import Logic.LauncherDestructTarget;
import Logic.Missile;
import Logic.MissileDestructor;
import Logic.MissileLauncher;
import Logic.MissileLauncherDestructor;

@Aspect
public class LogCalls {
   
    @After("execution(@Logger.ToLog * addMissileLauncher(..))") 
    public void addMissileLauncher(JoinPoint theJoinPoint) {
    	MissileLauncher ml = (MissileLauncher) theJoinPoint.getArgs()[0];
    	GameLogger.addFileHandler(ml, ml.getId());
    }
    
	@After("execution(@Logger.ToLog * addMissileDestructor(..))") 
	public void addMissileDestructor(JoinPoint theJoinPoint) {
    	MissileDestructor md = (MissileDestructor) theJoinPoint.getArgs()[0];
    	GameLogger.addFileHandler(md, md.getId());
    }
	
	@After("execution(@Logger.ToLog * addLauncherDestructor(..))") 
	public void addLauncherDestructor(JoinPoint theJoinPoint) {
    	MissileLauncherDestructor mld = (MissileLauncherDestructor) theJoinPoint.getArgs()[0];
    	GameLogger.addFileHandler(mld, mld.getID());
    }
	
	
	@After("execution(@Logger.ToLog * onMissileLaunchEvent(..))") 
	public void onMissileLaunchEvent(JoinPoint theJoinPoint) {
    	Missile m = (Missile) theJoinPoint.getArgs()[0];
    	MissileLauncher ml = m.getTheLauncher();
    	GameLogger.log(ml, Level.INFO, "missile-launcher #" + ml.getId() + " launched missile #" + m.getMissileId() );
    }
	
	@After("execution(@Logger.ToLog * onMissileLandEvent(..))") 
	public void onMissileLandEvent(JoinPoint theJoinPoint) {
    	Missile m = (Missile) theJoinPoint.getArgs()[0];
    	MissileLauncher ml = m.getTheLauncher();

    	GameLogger.log(ml, Level.INFO, "missile #" + m.getMissileId() 
    				+ " launched by missile-launcher #" + ml.getId() 
    				+ " landed, hit target: " + m.isHitTarget()
    				+ " is detructd: " + m.isDestructed());
    }
	
	@After("execution(@Logger.ToLog * onLauncherDestructorLaunchEvent(..))") 
	public void onLauncherDestructorLaunchEvent(JoinPoint theJoinPoint) {
		LauncherDestructTarget ldt = (LauncherDestructTarget) theJoinPoint.getArgs()[0];
		MissileLauncherDestructor mld = ldt.getDestructor();
		    	
    	GameLogger.log(mld, Level.INFO, "missile-destructor #" + mld.getID() + " just started destructing launcher #" + ldt.getTargetID());
    }
	
	@After("execution(@Logger.ToLog * onlauncherDestructResult(..))") 
	public void onLauncherDestructResult(JoinPoint theJoinPoint) {
		LauncherDestructTarget ldt = (LauncherDestructTarget) theJoinPoint.getArgs()[0];
		MissileLauncherDestructor mld = ldt.getDestructor();
		    	
    	GameLogger.log(mld, Level.INFO, "missile-destructor #" + mld.getID() + " finished destructing launcher #" + ldt.getTargetID()
    									+ " is destroyed: " + ldt.getTarget().isDestroyed());
    }
	
	@After("execution(@Logger.ToLog * onMissileDestructorLaunchEvent(..))") 
	public void onMissileDestructorLaunchEvent(JoinPoint theJoinPoint) {
		DestructTarget dt = (DestructTarget) theJoinPoint.getArgs()[0];
    	MissileDestructor md = dt.getDestructor();

    	GameLogger.log(md, Level.INFO, "missile-destructor #" + md.getId() + " just started destructing missile #" + dt.getTarget().getMissileId());
    }
	
	@After("execution(@Logger.ToLog * onMissileDestructResult(..))") 
	public void onMissileDestructResult(JoinPoint theJoinPoint) {
		DestructTarget dt = (DestructTarget) theJoinPoint.getArgs()[0];
    	MissileDestructor md = dt.getDestructor();

    	GameLogger.log(md, Level.INFO, "missile-destructor #" + md.getId() 
    							+ " finished destructing missile #" + dt.getTarget().getMissileId()
    							+ "is destructed: " + dt.getTarget().isDestructed());
    }
}


