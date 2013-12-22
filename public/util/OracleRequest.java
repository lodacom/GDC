package neo;

import java.util.ArrayList;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.d2rq.jena.ModelD2RQ;

public class OracleRequest {
	public Model d2rq;
	public final String NL=System.getProperty("line.separator");
	private String cog="PREFIX cog_r: <http://www.gdcproject.fr/gdcproject/cog_r/>";
	private String departement="PREFIX departement: <http://www.gdcproject.fr/gdcproject/departement/>";
	private String region="PREFIX region: <http://www.gdcproject.fr/gdcproject/region/>";
	private String impot="PREFIX impot: <http://www.gdcproject.fr/gdcproject/impot/>";
	private String geonames = "PREFIX geonames: <http://www.geonames.org/ontology#>" ;
	private String rdf = "PREFIX rdf: <"+RDF.getURI()+">" ;
	private String skos = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" ;
	private String pos = "PREFIX pos: <http://www.w3.org/2003/01/geo/wgs84_pos#>";
	private String neo="PREFIX neo: <http://www.neo4j.org/>";
	private String dc="PREFIX dc: <"+DC.getURI()+">";
	private QueryExecution query;
	
	public OracleRequest(){
		d2rq=new ModelD2RQ("oracle-mapping.ttl");
	}
	
	public ResultSet request(String requete){
		String request=cog + NL + departement + NL + region + NL + impot + NL + 
				geonames + NL + rdf + NL + pos + NL + skos + neo + NL + dc + requete;
		Query qu=QueryFactory.create(request);
		query=QueryExecutionFactory.create(qu,d2rq);
		ResultSet recup=query.execSelect();
		return recup;
	}
	
	public ArrayList<String> cities(){
		ArrayList<String> cities=new ArrayList<String>();
		String request=
				"SELECT ?communes "
						+ "WHERE { "
						+ "?num_commune cog_r:Cog_R_NccEnr ?communes . "
						+" } "
						+ "ORDER BY ASC(?communes)";
		ResultSet recup=request(request);
		while(recup.hasNext()){
			QuerySolution sol=(QuerySolution)recup.next();
			String city=sol.get("?communes").toString();
			cities.add(city);
		}
		return cities;
	}

	public ArrayList<String> regions(){
		ArrayList<String> regions=new ArrayList<String>();
		String request="SELECT ?region "
				+ "WHERE { "
				+ "?num_reg region:Region_NccEnr ?region"
				+ "} "
				+ "ORDER BY ASC(?region)";
		ResultSet recup=request(request);
		while(recup.hasNext()){
			QuerySolution sol=(QuerySolution)recup.next();
			String region=sol.get("?region").toString();
			regions.add(region);
		}
		return regions;
	}
	
	public ArrayList<String> departements(){
		ArrayList<String> departements=new ArrayList<String>();
		String request="SELECT ?departement "
				+ "WHERE { "
				+ "?num_dep departement:Departement_NccEnr ?departement"
				+ "} "
				+ "ORDER BY ASC(?departement)";
		ResultSet recup=request(request);
		while(recup.hasNext()){
			QuerySolution sol=(QuerySolution)recup.next();
			String departement=sol.get("?departement").toString();
			departements.add(departement);
		}
		return departements;
	}
}
