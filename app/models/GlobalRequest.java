package models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

public class GlobalRequest {

	public final String NL=System.getProperty("line.separator");
	private QueryExecution query;
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
	
	public Model m;
	public OracleRequest or;
	public TripleStoreRequest tsr;
	public NeoRequest neoR;

	public GlobalRequest(){
		m=ModelFactory.createDefaultModel();
		or=new OracleRequest();
		tsr=new TripleStoreRequest();
		neoR=new NeoRequest();
		tsr.consult();
		m.add(or.d2rq);
		m.add(tsr.tsr);
	}
	
	public GlobalRequest(int index){
		
	}
	
	public void setRootNeoGraph(){
	
		Properties configFile = new Properties() {
			private final static long serialVersionUID = 1L; {
				try {
					load(new FileInputStream(NeoRequest.neoProp));
				} catch (Exception e) {}
			}
		};
		String rootId=configFile.getProperty("neoRoot");
		neoR.setNeoNodeId(Integer.parseInt(rootId));
	}
	
	public ResultSet request(String requete){
		String request=cog + NL + departement + NL + region + NL + impot + NL + 
				geonames + NL + rdf + NL + pos + NL + skos + neo + NL + dc + requete;
		Query qu=QueryFactory.create(request);
		query=QueryExecutionFactory.create(qu,m);
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

	public ArrayList<TownInformation> regions(String region){
		ArrayList<TownInformation> regions=new ArrayList<TownInformation>();
		
		setRootNeoGraph();
		m.add(neoR.buildRegionsModel());
		
		String request;
		if (region!="regions"){
			request="SELECT ?communes (SUM(?redev) AS ?nbre_redev) (AVG(?im) AS ?impot_moyen) (AVG(?pm) AS ?patrimoine_moyen) ?long ?lat ?pop "
					+"WHERE { "
					+"?num_commune cog_r:Cog_R_NccEnr ?communes . "
					+"?num_reg region:Region_ChefLieu ?cheflieu . "
					+"?num_commune cog_r:Cog_R_Insee ?code_insee . "
					+"?num_reg region:Region_NccEnr ?region . "
					+ "?i_codeinsee impot:Impot_Insee ?num_commune . "
					+ "?i_codeinsee impot:Annee ?annee . "
					+ "?i_codeinsee impot:ImpotMoyen ?im . "
					+"?i_codeinsee impot:NbreRedevable ?redev . "
					+ "?i_codeinsee impot:PatrimoineM ?pm . "
					+ "?s geonames:featureCode ?c . "
					+ "?s geonames:name ?t  . "
					+ "?s geonames:population ?pop . "
					+ "?s pos:lat ?lat . "
					+ "?s pos:long ?long . "
					+"FILTER (str(?cheflieu)=str(?code_insee)) ."
					+ "FILTER (str(?region)=\""+region+"\") . "
					+ "FILTER (regex(str(?c),\".*A.ADM4\") && str(?communes)=str(?t)) "
					+"} "
					+ "GROUP BY ?annee ?communes ?long ?lat ?pop ";
		}else{
			request="SELECT ?communes (SUM(?redev) AS ?nbre_redev) (AVG(?im) AS ?impot_moyen) (AVG(?pm) AS ?patrimoine_moyen) ?lat ?long "
					+"WHERE { "
					+"?num_commune cog_r:Cog_R_NccEnr ?communes . "
					+"?num_reg region:Region_ChefLieu ?cheflieu . "
					+"?num_commune cog_r:Cog_R_Insee ?code_insee . "
					+"?num_reg region:Region_NccEnr ?region . "
					+ "?i_codeinsee impot:Impot_Insee ?num_commune . "
					+ "?i_codeinsee impot:Annee ?annee . "
					+ "?i_codeinsee impot:ImpotMoyen ?im . "
					+"?i_codeinsee impot:NbreRedevable ?redev . "
					+ "?i_codeinsee impot:PatrimoineM ?pm . "
					+"FILTER (str(?cheflieu)=str(?code_insee)) "
					+"} "
					+ "GROUP BY (?annee)";
		}
		ResultSet recup=request(request);
		TownInformation info;
		while(recup.hasNext()){
			QuerySolution sol=(QuerySolution)recup.next();
			String commune=sol.get("?communes").toString();
			String nbre_redev=sol.get("?nbre_redev").toString();
			String impot_moyen=sol.get("?impot_moyen").toString();
			String patrimoine_moyen=sol.get("?patrimoine_moyen").toString();
			String longitude=sol.get("?long").toString();
			String latitude=sol.get("?lat").toString();
			String pop=sol.get("?pop").toString();
			nbre_redev=nbre_redev.replaceAll("\\^.*", "");
			impot_moyen=impot_moyen.replaceAll("\\^.*", "");
			patrimoine_moyen=patrimoine_moyen.replaceAll("\\^.*", "");

			info=new TownInformation(region,Integer.parseInt(pop),commune, Integer.parseInt(nbre_redev), Float.parseFloat(impot_moyen), Float.parseFloat(patrimoine_moyen),Double.parseDouble(longitude),Double.parseDouble(latitude));
			regions.add(info);
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

	public ArrayList<TownInformation>departements(String departement){
		ArrayList<TownInformation> departements=new ArrayList<TownInformation>();
		
		/*setRootNeoGraph();
		m.add(neoR.buildDepartementModel());*/
		
		String request="";
		if (departement!="departements"){
			request="SELECT ?communes (SUM(?redev) AS ?nbre_redev) (AVG(?im) AS ?impot_moyen) (AVG(?pm) AS ?patrimoine_moyen) ?long ?lat ?pop "
					+"WHERE { "
					+"?num_commune cog_r:Cog_R_NccEnr ?communes . "
					+"?num_reg departement:Departement_ChefLieu ?cheflieu . "
					+"?num_commune cog_r:Cog_R_Insee ?code_insee . "
					+"?num_reg departement:Departement_NccEnr ?region . "
					+ "?i_codeinsee impot:Impot_Insee ?num_commune . "
					+ "?i_codeinsee impot:Annee ?annee . "
					+ "?i_codeinsee impot:ImpotMoyen ?im . "
					+"?i_codeinsee impot:NbreRedevable ?redev . "
					+ "?i_codeinsee impot:PatrimoineM ?pm . "
					+ "?s geonames:featureCode ?c . "
					+ "?s geonames:name ?t  . "
					+ "?s geonames:population ?pop . "
					+ "?s pos:lat ?lat . "
					+ "?s pos:long ?long . "
					+"FILTER (str(?cheflieu)=str(?code_insee)) ."
					+ "FILTER (str(?region)=\""+departement+"\") . "
					+ "FILTER (regex(str(?c),\".*A.ADM4\") && str(?communes)=str(?t)) "
					+"} "
					+ "GROUP BY ?annee ?communes ?long ?lat ?pop ";
		}else{

		}
		ResultSet recup=request(request);
		TownInformation info;
		while(recup.hasNext()){
			QuerySolution sol=(QuerySolution)recup.next();
			String commune=sol.get("?communes").toString();
			String nbre_redev=sol.get("?nbre_redev").toString();
			String impot_moyen=sol.get("?impot_moyen").toString();
			String patrimoine_moyen=sol.get("?patrimoine_moyen").toString();
			String longitude=sol.get("?long").toString();
			String latitude=sol.get("?lat").toString();
			String pop=sol.get("?pop").toString();
			nbre_redev=nbre_redev.replaceAll("\\^.*", "");
			impot_moyen=impot_moyen.replaceAll("\\^.*", "");
			patrimoine_moyen=patrimoine_moyen.replaceAll("\\^.*", "");

			info=new TownInformation(departement,Integer.parseInt(pop),commune, Integer.parseInt(nbre_redev), Float.parseFloat(impot_moyen), Float.parseFloat(patrimoine_moyen),Double.parseDouble(longitude),Double.parseDouble(latitude));
			departements.add(info);
		}
		return departements;
	}
}
