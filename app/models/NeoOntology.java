package models;

import com.hp.hpl.jena.rdf.model.Resource;

public class NeoOntology {

	//Préfixes
	private static final String neo="http://www.neo4j.org/";
	private static final String region=neo+"region/";
	private static final String departement=neo+"departement/";
	private static final String ville=neo+"ville/";
	
	//Ressources
	public static Resource Region=null;
	public static Resource Departement=null;
	public static Resource Ville=null;
	public static Resource Resume=null;
	
	public static String getNeo() {
		return neo;
	}
	public static String getRegion() {
		return region;
	}
	public static String getDepartement() {
		return departement;
	}
	public static String getVille() {
		return ville;
	}
	
	
}
