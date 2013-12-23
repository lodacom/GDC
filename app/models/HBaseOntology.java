package models;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class HBaseOntology {

	public static Model m=ModelFactory.createDefaultModel();
	
	//Préfixes
	private static final String hbase="http://hbase.apache.org/";
	private static final String region=hbase+"region/";
	private static final String departement=hbase+"departement/";
	
	//Ressources
	public static Resource Region=null;
	public static Resource Departement=null;
	public static Resource RSA=null;
	
	//Propriétés
	public static Property RegionProp=m.createProperty(hbase+"regionProp");
	public static Property DepartementProp=m.createProperty(hbase+"departementProp");
	public static Property RSA_2009Prop=m.createProperty(hbase+"rsa2009");
	public static Property RSA_2010Prop=m.createProperty(hbase+"rsa2010");
	
	public static String getHbase() {
		return hbase;
	}
	public static String getRegion() {
		return region;
	}
	public static String getDepartement() {
		return departement;
	}
	
	
}
