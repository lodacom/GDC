package models;

public class EntityInformation {
	
	public ISFInformation isfInfo;
	public GeonamesInformation gInfo;
	public NeoInformation nInfo;
	
	public EntityInformation(ISFInformation isfInfo,GeonamesInformation gInfo,NeoInformation nInfo){
		this.isfInfo=isfInfo;
		this.gInfo=gInfo;
		this.nInfo=nInfo;
	}
	
	public String toString(){
		return "Nom de l'entite:"+this.isfInfo.entite+"\n"
				+ "Nombre d'habitants de l'entité:"+this.gInfo.population+"\n"
				+ "Nom de la commune:"+this.isfInfo.nom_commune+"\n"
				+ "Nombre de redevalbes à l'ISF:"+this.isfInfo.nbre_redev+"\n"
				+"Impot moyen:"+this.isfInfo.impot_moyen+"\n"
				+"Patrimoine moyen:"+this.isfInfo.patrimoine_moyen+"\n"
				+"Longitude:"+this.gInfo.longitude+"\n"
				+"Latitude:"+this.gInfo.latitude;
	}
	
	public String getAbstract(){
		return nInfo.resume;
	}
}
