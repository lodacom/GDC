package models;

public class TownInformation {
	
	public String entite;
	public String nom_commune;
	public int nbre_redev;
	public float impot_moyen;
	public float patrimoine_moyen;
	public double longitude;
	public double latitude;
	public int population;
	
	public TownInformation(String entite,int population,String nom_commune,int nbre_redev,float impot_moyen,float patrimoine,double longitude,double latitude){
		this.entite=entite;
		this.population=population;
		this.nom_commune=nom_commune;
		this.nbre_redev=nbre_redev;
		this.impot_moyen=impot_moyen;
		this.patrimoine_moyen=patrimoine;
		this.longitude=longitude;
		this.latitude=latitude;
	}
	
	public String toString(){
		return "Nom de l'entite:"+this.entite+"\n"
				+ "Nombre d'habitants de l'entité:"+this.population+"\n"
				+ "Nom de la commune:"+this.nom_commune+"\n"
				+ "Nombre de redevalbes à l'ISF:"+this.nbre_redev+"\n"
				+"Impot moyen:"+this.impot_moyen+"\n"
				+"Patrimoine moyen:"+this.patrimoine_moyen+"\n"
				+"Longitude:"+this.longitude+"\n"
				+"Latitude:"+this.latitude;
	}
}
