package nord.no;

import java.io.Serializable;


public class Kjoretoy implements Serializable {  // Legger til Serializable som forberedelse for lagring
	private static final long serialVersionUID = 1L;
	private final String chassiNumber;
	private final char vehicleType;
	private final int wheels;
	private String regNumber;
	private int engineSize;
	private int numPassengers;
	private int topSpeed;
	private int tankVolume;
	private int idDB;
	private double milprLiter;

	public static final char PERSONBIL 	= '1';
	public static final char VAREBIL 		= '2';
	public static final char BUSS 			= '3';
	public static final char LASTEBIL 		= '4';
	public static final char TRAILER 		= '5';
	public static final char TRAKTOR 		= '6';
	public static final char MOTORSYKKEL 	= '7';
	public static final char MOPED 		= '8';

	public Kjoretoy(char vehicleType, String chassiNumber, int numWheels) {
		super();
		this.chassiNumber = chassiNumber;
		this.vehicleType = vehicleType;
		this.wheels = numWheels;
	}

	public int getWheels() {
		return wheels;
	}
	
	public String getRegNumber() {
		return regNumber.toUpperCase();
	}
	public void setRegNumber( String regNumber) {
		this.regNumber = regNumber;
	}	
	
	public int getEngineSize() {
		return engineSize;
	}
	
	public void setEngineSize(int engineSize) {
		this.engineSize = engineSize;
	}
	
	public int getNumPassengers() {
		return numPassengers;
	}
	
	public void setNumPassengers(int numPassengers) {
		this.numPassengers = numPassengers;
	}
	
	public int getTopSpeed() {
		return topSpeed;
	}
	
	public void setTopSpeed(int topSpeed) {
		this.topSpeed = topSpeed;
	}
	
	public int getTankVolume() {
		return tankVolume;
	}
	
	public void setTankVolume(int tankVolume) {
		this.tankVolume = tankVolume;
	}
	
	public String getChassiNumber() {
		return chassiNumber;
	}
		
	public char getVehicleType() {
		return vehicleType;
	}
	public int getTypenr(){
		int type = 0;
		switch (this.getVehicleType()){
		case PERSONBIL:
			type=1;
			break;
		case VAREBIL:
			type=2;
			break;
		case BUSS:
			type=3;
			break;
		case LASTEBIL:
			type=4;
			break;
		case TRAILER:
			type=5;
			break;
		case TRAKTOR:
			type=6;
			break;
		case MOTORSYKKEL:
			type=7;
			break;
		case MOPED:
			type =8;
			break;
		}
		return type;
	}
	
	public String getVehicleName() {
		String name = "";
		switch (this.getVehicleType()) {
			case PERSONBIL:
				name = "personbil";
				break;
			case VAREBIL:
				name = "varebil";
				break;
			case BUSS:
				name = "buss";
				break;
			case LASTEBIL:
				name = "lastebil";
				break;
			case TRAILER:
				name = "trailer";
				break;
			case TRAKTOR:
				name = "traktor";
				break;
			case MOTORSYKKEL:
				name = "motorsykkel";
				break;
			case MOPED:
				name = "moped";
				break;
		}
		return name;
	}
	@Override
	public String toString() {		
		return this.getVehicleName() + 
			", ID: "+this.getIdDB()+
			", regnr: " + this.getRegNumber() + 
			", chassinr: " + this.getChassiNumber() + 
			", antall hjul: " + this.getWheels() +
			", antall passasjerer: " + this.getNumPassengers() +
			", motorstørrelse: " + this.getEngineSize() +
			", maksfart: " + this.getTopSpeed() +
			", tank: " + this.getTankVolume() +
			", liter pr.mil: " + this.getPerMil()+
			", rekkevidde: " + this.getRekkeVidde();
	}
	public void setIdDB(int idnr){
		this.idDB = idnr;
	}
	public int getIdDB(){
		return idDB;
	}
	public void setPerMil(double milprLiter) {
		// TODO Auto-generated method stub
		this.milprLiter = milprLiter;
	}
	
	public double getPerMil() {
		return milprLiter;
		
	}
	public double getRekkeVidde() {
		double rekv = getPerMil();
		if (rekv != 0.0){
			double rek = (double)Math.round(getTankVolume()/getPerMil());
			return rek;					
		}
		else {
			return 0.0;
		}
	}
}