package models;

import java.io.FileInputStream;
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
	private String hbase="PREFIX hbase: <http://hbase.apache.org/>";
	
	public Model m;
	public OracleRequest or;
	public TripleStoreRequest tsr;
	public NeoRequest neoR;
	public HBaseRequest hbaseR;
	
	public GlobalRequest(){
		m=ModelFactory.createDefaultModel();
		or=new OracleRequest();
		tsr=new TripleStoreRequest();
		neoR=new NeoRequest();
		hbaseR=new HBaseRequest();
		tsr.consult();
		m.add(or.d2rq);
		m.add(tsr.tsr);
	}
	
	public GlobalRequest(int index){
		m=ModelFactory.createDefaultModel();
		or=new OracleRequest();
		m.add(or.d2rq);
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
				geonames + NL + rdf + NL + pos + NL + skos + neo + NL + dc + NL + hbase + requete;
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

	public ArrayList<EntityInformation> regions(String region){
		ArrayList<EntityInformation> regions=new ArrayList<EntityInformation>();
		
		setRootNeoGraph();
		m.add(neoR.buildRegionsModel(region));
		m.add(hbaseR.regionFilter(region));
		System.out.println(region);
		String request;
		if (region!="regions"){
			request="SELECT ?communes (SUM(?redev) AS ?nbre_redev) (AVG(?im) AS ?impot_moyen) (AVG(?pm) AS ?patrimoine_moyen) ?long ?lat ?pop ?abstract ?rsa2009 ?rsa2010 "
					+"WHERE { "
					+"?num_commune cog_r:Cog_R_NccEnr ?communes . "
					+"?num_reg region:Region_ChefLieu ?cheflieu . "
					+"?num_commune cog_r:Cog_R_Insee ?code_insee . "
					+"?num_reg region:Region_NccEnr \""+region+"\" . "
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
					//+ "?n neo:regionProp ?region . "
					+ "?n dc:description ?abstract . "
					+ "?h hbase:rsa2009 ?rsa2009 . "
					+ "?h hbase:rsa2010 ?rsa2010 . "
					//+ "FILTER regex(str(?region),\""+region+"\") . "
					+"FILTER (str(?cheflieu)=str(?code_insee)) ."
					+ "FILTER (regex(str(?c),\".*A.ADM4\") && str(?communes)=str(?t)) "
					+"} "
					+ "GROUP BY ?annee ?communes ?long ?lat ?pop ?abstract ?rsa2009 ?rsa2010";
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
		EntityInformation info;
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
			String resume=sol.get("?abstract").toString();
			String rsa_2009=sol.get("?rsa2009").toString();
			String rsa_2010=sol.get("?rsa2010").toString();
			
			ISFInformation isfInfo=new ISFInformation(region, commune, Integer.parseInt(nbre_redev), Float.parseFloat(impot_moyen),Float.parseFloat(patrimoine_moyen));
			GeonamesInformation gInfo=new GeonamesInformation(Integer.parseInt(pop), Double.parseDouble(longitude),Double.parseDouble(latitude));
			NeoInformation nInfo=new NeoInformation(resume);
			HBaseInformation hInfo=new HBaseInformation(Integer.parseInt(rsa_2009), Integer.parseInt(rsa_2010));
			info=new EntityInformation(isfInfo,gInfo,nInfo,hInfo);
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

	public ArrayList<EntityInformation>departements(String departement){
		ArrayList<EntityInformation> departements=new ArrayList<EntityInformation>();
		
		setRootNeoGraph();
		m.add(neoR.buildDepartementModel(departement));
		m.add(hbaseR.departementFilter(departement));
		
		String request="";
		if (departement!="departements"){
			request="SELECT ?communes (SUM(?redev) AS ?nbre_redev) (AVG(?im) AS ?impot_moyen) (AVG(?pm) AS ?patrimoine_moyen) ?long ?lat ?pop ?abstract ?rsa2009 ?rsa2010 "
					+"WHERE { "
					+"?num_commune cog_r:Cog_R_NccEnr ?communes . "
					+"?num_reg departement:Departement_ChefLieu ?cheflieu . "
					+"?num_commune cog_r:Cog_R_Insee ?code_insee . "
					+"?num_reg departement:Departement_NccEnr \""+departement+"\" . "
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
					//+ "?n neo:departementProp ?departement . "
					+ "?n dc:description ?abstract . "
					+ "?h hbase:rsa2009 ?rsa2009 . "
					+ "?h hbase:rsa2010 ?rsa2010 . "
					//+ "FILTER regex(str(?departement),\""+departement+"\") . "
					+"FILTER (str(?cheflieu)=str(?code_insee)) ."
					+ "FILTER (regex(str(?c),\".*A.ADM4\") && str(?communes)=str(?t)) "
					+"} "
					+ "GROUP BY ?annee ?communes ?long ?lat ?pop ?abstract ?rsa2009 ?rsa2010";
		}else{

		}
		ResultSet recup=request(request);
		EntityInformation info;
		while(recup.hasNext()){
			QuerySolution sol=(QuerySolution)recup.next();
			String commune=sol.get("?communes").toString();
			String nbre_redev=sol.get("?nbre_redev").toString();
			String impot_moyen=sol.get("?impot_moyen").toString();
			String patrimoine_moyen=sol.get("?patrimoine_moyen").toString();
			String longitude=sol.get("?long").toString();
			String latitude=sol.get("?lat").toString();
			String pop=sol.get("?pop").toString();
			String resume=sol.get("?abstract").toString();
			nbre_redev=nbre_redev.replaceAll("\\^.*", "");
			impot_moyen=impot_moyen.replaceAll("\\^.*", "");
			patrimoine_moyen=patrimoine_moyen.replaceAll("\\^.*", "");
			String rsa_2009=sol.get("?rsa2009").toString();
			String rsa_2010=sol.get("?rsa2010").toString();
			
			ISFInformation isfInfo=new ISFInformation(departement, commune, Integer.parseInt(nbre_redev), Float.parseFloat(impot_moyen),Float.parseFloat(patrimoine_moyen));
			GeonamesInformation gInfo=new GeonamesInformation(Integer.parseInt(pop), Double.parseDouble(longitude),Double.parseDouble(latitude));
			NeoInformation nInfo=new NeoInformation(resume);
			HBaseInformation hInfo=new HBaseInformation(Integer.parseInt(rsa_2009), Integer.parseInt(rsa_2010));//TODO à changer
			info=new EntityInformation(isfInfo,gInfo,nInfo,hInfo);
			departements.add(info);
		}
		return departements;
	}
}
