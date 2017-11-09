package nord.no;

public class KjoretoyMedDor extends Kjoretoy  {
	private static final long serialVersionUID = 1L;
	private final int numDoors;

	public KjoretoyMedDor(char vehicleType, String chassiNumber, int numWheels, int numDoors) {
		super(vehicleType, chassiNumber, numWheels);
		this.numDoors = numDoors;
	}

	public int getNumDoors() {
		return numDoors;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", antall dører: " + this.getNumDoors();
	}
}