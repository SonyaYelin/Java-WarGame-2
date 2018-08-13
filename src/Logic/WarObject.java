package Logic;

public abstract class WarObject {
	private String id;
	private boolean gameOver = false, isWaiting = false; 

	public WarObject(String id) {
		this.id = id;
	}
	
	public String getID() {
		return id;
	}
	
	public void endGame() {
		this.gameOver = true;
		if ( this.isWaiting) {
			synchronized (this) {
				this.notifyAll();
			}
		}
	}
	
	public void setWaiting(boolean isWaiting) {
		this.isWaiting = isWaiting;
	}
	
	public boolean isGameOver() {
		return gameOver;
	}
	
	public boolean isWaiting() {
		return isWaiting;
	}
}
