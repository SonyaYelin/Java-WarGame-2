package UI;

import java.util.Vector;

import Handlers.ConfigHandler;
import Logic.Game;

import java.util.Scanner;

import MVC.GameController;
import MVC.GameUIEventsListener;

public class ConsoleApplication implements GameUI {
	private Vector<GameUIEventsListener> allListeners;
	
	public ConsoleApplication() {
		allListeners = new Vector<GameUIEventsListener>();
		new GameController(Game.getInstance(), this);
	}
	
	public static void main(String[] args) {
		ConsoleApplication console = new ConsoleApplication();
		console.run();
	}
	
	private void run() {
		Scanner s = new Scanner(System.in);
		readFromConfig(s);
		int selection;
		while(true){
			printMenu();
			selection =s.nextInt();
			funcSelect(selection,s);
		}		
	}

	private void readFromConfig(Scanner s) {
		System.out.println("Do you want to read game settings from config file? Y\\N");
		String selection = s.next();
		if(selection.equalsIgnoreCase("y"))
			try {
				new ConfigHandler().readObjectsFromJSONFile();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		else
			return;
		
	}

	private void funcSelect(int selection, Scanner s) {
		switch (selection) {
		case 1:
			System.out.println("enter missile launcher id ");
			addMissileLauncher(s.next());
			break;
		case 2:
			System.out.println("enter missile launcher destructor id ");
			addMissileLauncherDestructor(s.next());
			break;	
		case 3:
			System.out.println("enter missile destructor id ");
			addMissileDestructor(s.nextLine());
			break;
		case 4:
			System.out.println("enter missile launcher id: ");
			String missileLauncherId = s.next();
			System.out.println("enter missile id: ");
			String missileId = s.next();
			System.out.println("enter missile destination: ");
			String destination=s.next();
			System.out.println("enter missile potential damage: ");
			int damage = s.nextInt(); 
			launchMissile(missileLauncherId, missileId, destination, damage);
			break;
		case 5: 
			System.out.println("enter missile laucher destruct type: ");
			String missileLaucherDestructType = s.next();
			System.out.println("enter missile laucher destruct id: ");
			String missileLaucherDestructId = s.next();
			destructMissileLauncher(missileLaucherDestructType, missileLaucherDestructId);
			break;
		case 6:			
			System.out.println("enter missile Id: ");
			String missileIdToDestruct= s.next();
			System.out.println("enter missile destructor id: ");
			String missileDestructorId= s.next();
			destructMissile(missileIdToDestruct, missileDestructorId);
			break;
		case 7:
			viewGameStatus();
			break;
		case 8:
			exit();

		default:
			break;
		}
		
	}

	private static void printMenu() {
		for (int i = 0; i < MENU_FUNCTIONS_NAMES.length; i++) {
			System.out.println(i+1 + " - " + MENU_FUNCTIONS_NAMES[i]);
		}
		
	}

	@Override
	public void addMissileLauncher(String id) {
		for (GameUIEventsListener l : allListeners)
			l.addMissileLauncherFromUI(id);

	}

	@Override
	public void addMissileLauncherDestructor(String type) {
		for (GameUIEventsListener l : allListeners)
			l.addMissileLauncherDestructorFromUI(type);
	}

	@Override
	public void addMissileDestructor(String id) {
		for (GameUIEventsListener l : allListeners)
			l.addMissileDestructorFromUI(id);

	}

	@Override
	public void launchMissile(String missileLauncherId, String missileId, String destination, int damage) {
		for (GameUIEventsListener l : allListeners)
			l.launchMissileFromUI( missileLauncherId,  missileId,  destination,  damage);

	}

	@Override
	public void destructMissileLauncher(String missileLaucherDestructType, String missileLaucherDestructId) {
		for (GameUIEventsListener l : allListeners)
			l.destructMissileLauncherFromUI(missileLaucherDestructType, missileLaucherDestructId);

	}

	@Override
	public void destructMissile(String missileIdToDestruct, String missileDestructorId) {
		for (GameUIEventsListener l : allListeners)
			l.destructMissileFromUI(missileIdToDestruct, missileDestructorId);
	}

	@Override
	public void viewGameStatus() {
		for (GameUIEventsListener l : allListeners)
			l.viewGameStatusFromUI();

	}

	@Override
	public void exit() {
		for (GameUIEventsListener l : allListeners)
			l.exitFromUI();
		System.exit(1);
	}


	@Override
	public void registerListener(GameUIEventsListener listener) {
		allListeners.add(listener);
		
	}

	@Override
	public void showAddMissileLauncher(String id,boolean isHidden) {
		System.out.println("Missile Launcher "+ id+ "Was Added Is Hidden:"+isHidden);
		
	}

	@Override
	public void showAddMissileLauncherDestructor(String type) {
		System.out.println("Missile Launcher Destructor "+ type+ "Was Added");

		
	}

	@Override
	public void showAddMissileDestructor(String id) {
		System.out.println("Missile Destructor "+ id+ "Was Added");
		
	}

	@Override
	public void showMissileLaunch(String missileLauncherId, String missileId, String destination, int damage,
			int flytime) {
		System.out.println(missileId + " Was Launched From "+ missileLauncherId+" To "+ destination+ " For "+flytime + "Seconds");
		
	}

	@Override
	public void showDestructMissileLauncher(String type, String missileLauncherId) {
		System.out.println("Missile launcher Destructor "+ type +" trying to destruct " + missileLauncherId);
	}

	@Override
	public void showLauncherDestructResult(String type, String missileLauncherId, boolean isDestructed) {
		String result;
		if(isDestructed)
			result = "Success";
		else
			result = "Failed";
		System.out.println("Missile launcher Destructor "+ type +" attempt to destruct " + missileLauncherId+" " + result );
		
	}

	@Override
	public void showDestructMissile(String missileIdToDestruct, String missileDestructorId, int waitingTime) {
		System.out.println("Missile  Destructor "+ missileDestructorId +" trying to destruct " + missileIdToDestruct);
		
	}

	@Override
	public void showMissileResult(String missileId, boolean isHit, boolean isDestructed, boolean isHidden,
			String launcherId) {
		if(isHit)
			System.out.println("Missile  "+ missileId +" Hit Target ");
		if(isDestructed)
			System.out.println("Missile  "+ missileId +" was destructed");
		else
			System.out.println("Missile  "+ missileId +" Missed Target ");

		
	}

	@Override
	public void showMessage(String message) {
		System.out.println(message);
		
	}






}
