package models;

public class GeonamesInformation {
	
	public double longitude;
	public double latitude;
	public int population;
	
	public GeonamesInformation(int population,double longitude,double latitude){
		this.population=population;
		this.longitude=longitude;
		this.latitude=latitude;
	}
}
