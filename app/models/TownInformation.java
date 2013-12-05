package models;

public class TownInformation {
	
	public String nom_commune;
	public int nbre_redev;
	public float impot_moyen;
	public float patrimoine_moyen;
	public double longitude;
	public double latitude;
	
	public TownInformation(String nom_commune,int nbre_redev,float impot_moyen,float patrimoine,double longitude,double latitude){
		this.nom_commune=nom_commune;
		this.nbre_redev=nbre_redev;
		this.impot_moyen=impot_moyen;
		this.patrimoine_moyen=patrimoine;
		this.longitude=longitude;
		this.latitude=latitude;
	}
	
	public String toString(){
		return "Nom de la commune:"+this.nom_commune+"\n"
				+ "Nombre de redevalbes à l'ISF:"+this.nbre_redev+"\n"
				+"Impot moyen:"+this.impot_moyen+"\n"
				+"Patrimoine moyen:"+this.patrimoine_moyen+"\n"
				+"Longitude:"+this.longitude+"\n"
				+"Latitude:"+this.latitude;
	}
}
