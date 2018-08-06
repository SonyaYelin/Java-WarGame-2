package Logger;

import java.util.logging.Level;

import Logic.DestructTarget;
import Logic.LauncherDestructTarget;
import Logic.Missile;
import Logic.MissileDestructor;
import Logic.MissileLauncher;
import Logic.MissileLauncherDestructor;

public aspect ToLogAspect {
	
	pointcut addLauncher() : execution (@Logger.ToLog * addMissileLauncher(..));
	pointcut addMissileDestructor() : execution (@Logger.ToLog * addMissileDestructor(..));
	pointcut addLauncherDestructor() : execution (@Logger.ToLog * addLauncherDestructor(..));
	
	pointcut missileLaunchEvent() : execution (@Logger.ToLog * onMissileLaunchEvent(..));
	pointcut missileLandEvent() : execution (@Logger.ToLog * onMissileLandEvent(..));

	pointcut launcherDestructorLaunchEvent() : execution (@Logger.ToLog * onLauncherDestructorLaunchEvent(..));
	pointcut launcherDestructorLandEvent() : execution (@Logger.ToLog * onlauncherDestructResult(..));
	
	pointcut missileDestructorLaunchEvent() : execution (@Logger.ToLog * onMissileDestructorLaunchEvent(..));
	pointcut missileDestructorLandEvent() : execution (@Logger.ToLog * onMissileDestructResult(..));

	pointcut exit() : execution (@Logger.ToLog * onExit(..));

	after() : exit() {
		GameLogger.close();
	}
	
	//add
	after() : addLauncher() {
		MissileLauncher ml = (MissileLauncher) thisJoinPoint.getArgs()[0];
		GameLogger.addFileHandler(ml, ml.getID());
	}
	
	after() : addMissileDestructor() {
		MissileDestructor md = (MissileDestructor)thisJoinPoint.getArgs()[0];
		GameLogger.addFileHandler(md, md.getID());
	}
	
	after() : addLauncherDestructor() {
		MissileLauncherDestructor ld = (MissileLauncherDestructor)thisJoinPoint.getArgs()[0];
		GameLogger.addFileHandler(ld, ld.getID());
	}
	
	//launch
	after() : missileLaunchEvent() {
		Missile m = (Missile)thisJoinPoint.getArgs()[0];
		MissileLauncher ml = m.getTheLauncher();
		GameLogger.log(ml, Level.INFO, "missile-launcher #" + ml.getID()
		+ " launched missile #" + m.getMissileId() );
	}
	
	after() : missileLandEvent() {
		Missile m = (Missile)thisJoinPoint.getArgs()[0];
		MissileLauncher ml = m.getTheLauncher();	
		GameLogger.log(ml, Level.INFO, "missile #" + m.getMissileId()
		 + " launched by missile-launcher #" + ml.getID()
		 + " landed, hit target: " + m.isHitTarget()
		 + " is detructd: " + m.isDestructed());
	}
	
	//destruct launcher	
	after() : launcherDestructorLaunchEvent() {
		LauncherDestructTarget ldt = (LauncherDestructTarget)thisJoinPoint.getArgs()[0];
		 MissileLauncherDestructor mld = ldt.getDestructor();
		GameLogger.log(mld, Level.INFO, "missile-launcher-destructor #" + mld.getID()
		+ " just started destructing launcher #" + ldt.getTargetID());
	}

	after() : launcherDestructorLandEvent() {
		LauncherDestructTarget ldt = (LauncherDestructTarget)thisJoinPoint.getArgs()[0];
		 MissileLauncherDestructor mld = ldt.getDestructor();
		GameLogger.log(mld, Level.INFO, "missile-launcher-destructor #" + mld.getID()
		+ "finished destructing launcher #" + ldt.getTargetID() 
		+ " is destroyed: " + ldt.getTarget().isDestroyed());
	}

	//destruct missile
	after() : missileDestructorLaunchEvent() {
		DestructTarget dt = (DestructTarget)thisJoinPoint.getArgs()[0];
		MissileDestructor md = dt.getDestructor();
		 GameLogger.log(md, Level.INFO, "missile-destructor #" + md.getID() 
		 + " just started destructing missile #" + dt.getTarget().getMissileId());
	}
		
	after() : missileDestructorLandEvent() {
		DestructTarget dt = (DestructTarget)thisJoinPoint.getArgs()[0];
		 MissileDestructor md = dt.getDestructor();		
		 GameLogger.log( md, Level.INFO, "missile-destructor #" + md.getID()
		 + " finished destructing missile #" + dt.getTarget().getMissileId()
		 + "is destructed: " + dt.getTarget().isDestructed() );
	}
	

}

