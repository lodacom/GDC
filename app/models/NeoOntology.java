package models;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class NeoOntology {

	public static Model m=ModelFactory.createDefaultModel();
	
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
	
	//Propriétés
	public static Property RegionProp=m.createProperty(neo+"regionProp");
	public static Property DepartementProp=m.createProperty(neo+"departementProp");
	public static Property VilleProp=m.createProperty(neo+"villeProp");
	
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
