package Logger;

import java.util.logging.Level;

import Logic.DestructTarget;
import Logic.LauncherDestructTarget;
import Logic.Missile;
import Logic.MissileDestructor;
import Logic.MissileLauncher;
import Logic.MissileLauncherDestructor;
import Logic.WarObject;

public aspect ToLogAspect {
	
	pointcut add(): execution (@Logger.ToLog * add*(..));
	
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
	
	after() : add() {
		WarObject wo = (WarObject) thisJoinPoint.getArgs()[0];
		GameLogger.addFileHandler(wo, wo.getID());
	}
	
	//launch
	before() : missileLaunchEvent() {
		if( !GameLogger.isClosed() )
			return;
		Missile m = (Missile)thisJoinPoint.getArgs()[0];
		MissileLauncher ml = m.getTheLauncher();
		GameLogger.log(ml, Level.INFO, "missile-launcher #" + ml.getID()
		+ " launched missile #" + m.getMissileId() );
	}
	
	before() : missileLandEvent() {
		Missile m = (Missile)thisJoinPoint.getArgs()[0];
		MissileLauncher ml = m.getTheLauncher();	
		GameLogger.log(ml, Level.INFO, "missile #" + m.getMissileId()
		 + " launched by missile-launcher #" + ml.getID()
		 + " landed, hit target: " + m.isHitTarget()
		 + " is detructd: " + m.isDestructed());
	}
	
	//destruct launcher	
	before() : launcherDestructorLaunchEvent() {
		LauncherDestructTarget ldt = (LauncherDestructTarget)thisJoinPoint.getArgs()[0];
		 MissileLauncherDestructor mld = ldt.getDestructor();
		GameLogger.log(mld, Level.INFO, "missile-launcher-destructor #" + mld.getID()
		+ " just started destructing launcher #" + ldt.getTargetID());
	}

	before() : launcherDestructorLandEvent() {
		LauncherDestructTarget ldt = (LauncherDestructTarget)thisJoinPoint.getArgs()[0];
		 MissileLauncherDestructor mld = ldt.getDestructor();
		GameLogger.log(mld, Level.INFO, "missile-launcher-destructor #" + mld.getID()
		+ "finished destructing launcher #" + ldt.getTargetID() 
		+ " is destroyed: " + ldt.getTarget().isDestroyed());
	}

	//destruct missile
	before() : missileDestructorLaunchEvent() {
		DestructTarget dt = (DestructTarget)thisJoinPoint.getArgs()[0];

		MissileDestructor md = dt.getDestructor();
		 GameLogger.log(md, Level.INFO, "missile-destructor #" + md.getID() 
		 + " just started destructing missile #" + dt.getTarget().getMissileId());
	}
		
	before() : missileDestructorLandEvent() {
		DestructTarget dt = (DestructTarget)thisJoinPoint.getArgs()[0];

		 MissileDestructor md = dt.getDestructor();		
		 GameLogger.log( md, Level.INFO, "missile-destructor #" + md.getID()
		 + " finished destructing missile #" + dt.getTarget().getMissileId()
		 + "is destructed: " + dt.getTarget().isDestructed() );
	}
	

}

