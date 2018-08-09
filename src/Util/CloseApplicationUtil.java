package Util;

import UI.VisualApplication;
import javafx.application.Platform;

public class CloseApplicationUtil {
	public static void closeApplication(VisualApplication theApplication) {
		/*DialogResponse response = Dialogs.showConfirmDialog(theApplication.getPrimaryStage(),
			    "Are you sure you want to exit?", "Confirm Dialog", "Goodbye");
		
		if (response == DialogResponse.YES) {*/
		if (true) {
			System.out.println("exiting platform");
			Platform.exit();
			System.exit(1);
		}
	}
}
