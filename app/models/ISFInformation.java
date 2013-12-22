package models;

public class ISFInformation {

	public String entite;
	public String nom_commune;
	public int nbre_redev;
	public float impot_moyen;
	public float patrimoine_moyen;
	
	public ISFInformation(String entite,String nom_commune,int nbre_redev,float impot_moyen,float patrimoine){
		this.entite=entite;
		this.nom_commune=nom_commune;
		this.nbre_redev=nbre_redev;
		this.impot_moyen=impot_moyen;
		this.patrimoine_moyen=patrimoine;
	}
}
