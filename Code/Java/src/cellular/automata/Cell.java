public class Cell {
	
	private boolean electron;
	private int electronLifeTime;
	private int[] photonLifeTimes;
	private int photonCount;
	
	protected Cell(boolean electron, int electronLifeTime, int[] photonLifeTimes, int photonCount) {
		this.electron = electron;
		this.electronLifeTime = electronLifeTime;
		this.photonLifeTimes = photonLifeTimes;
		this.photonCount = photonCount;
	}
	
	public boolean isHigh() {
		return electron;
	}
	
	public void setElectronLevel(boolean electron) {
		this.electron = electron;
	}
	
	public int getElectronLifeTime() {
		return electronLifeTime;
	}
	
	public void setElectronLifeTime(int electronLifeTime) {
		this.electronLifeTime = electronLifeTime;
	}
	
	public int getPhotonLifeTimes(int pos) {
		return photonLifeTimes[pos];
	}
	
	public void setPhotonLifeTimes(int photonLifeTimes, int pos) {
		this.photonLifeTimes[pos] = photonLifeTimes;
	}
	
	public void setPhotonCount(int count) {
		this.photonCount = count;
	}
	
	public int getPhotonCount() {
		return photonCount;
	}
}
