package neo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.DC;

public class Test {

	public final String NL = System.getProperty("line.separator");
	public String service="http://dbpedia.org/sparql";
	public String dbprop="PREFIX dbpprop: <http://dbpedia.org/property/>";
	public String dbpedia_owl="PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>";
	public final String prefix="http://neo4j.org#";
	public final String neo="http://neo4j.org";
	public final String isLink="isLink";
	private QueryExecution query;
	NeoManager neoM;
	public Node region,departement,ville;
	private long neoNodeId;
	Model m;
	
	private static enum RelTypes implements RelationshipType
	{
		REGION_NODE,
		DEPARTEMENT_NODE,
		TOWN_NODE,
		HAS_REGION,
		HAS_DEPARTEMENT,
		HAS_TOWN,
		HAS_ABSTRACT
	}

	public Test(){
		m=ModelFactory.createDefaultModel();
		neoM=NeoManager.getInstance();
		Node matrix=neoM.addNode();
		neoNodeId=matrix.getId();
		saveRoot();
		region=neoM.addNode();
		neoM.addProperty(region, "region", "region");
		departement=neoM.addNode();
		neoM.addProperty(departement, "departement", "departement");
		ville=neoM.addNode();
		neoM.addProperty(ville, "ville", "ville");
		neoM.addRelationship(matrix, region, RelTypes.REGION_NODE);
		neoM.addRelationship(matrix, departement, RelTypes.DEPARTEMENT_NODE);
		neoM.addRelationship(matrix, ville, RelTypes.TOWN_NODE);
		
		OracleRequest gr=new OracleRequest();
		ArrayList<String> regions=gr.regions();
		ArrayList<String> departements=gr.departements();
		ArrayList<String> cities=gr.cities();
		
		/*System.out.println("Ajout des villes dans le graphe en cours...");
		for (String ville:cities){
			searchAllEntities(ville, "ville");
		}
		System.out.println("Ajout des départements dans le graphe en cours...");
		for (String dep:departements){
			searchAllEntities(dep, "departement");
		}*/
		System.out.println("Ajout des régions dans le graphe en cours...");
		for (String reg:regions){
			searchAllEntities(reg, "region");
		}
	}

	public Test(int index){
		neoM=NeoManager.getInstance();
		m=ModelFactory.createDefaultModel();
		neoNodeId=1;
	}
	
	public void saveRoot(){
		String adressedufichier = "neo.properties";
		try{
			FileWriter fw = new FileWriter(adressedufichier, false);
			BufferedWriter output = new BufferedWriter(fw);
			output.write("neoRoot "+neoNodeId);
			output.flush();
			output.close();
		}
		catch(IOException ioe){
			System.out.print("Erreur : ");
			ioe.printStackTrace();
			}
	}
	
	public void searchAllEntities(String entite,String typeEntite){
		String request=dbprop + NL + dbpedia_owl + NL +
				"SELECT ?entity ?abstract "+
				"WHERE { "+
				"?truc dbpprop:name ?entity . "+
				"?truc dbpedia-owl:abstract ?abstract "+
				"FILTER(str(?entity)=\""+entite+"\" && lang(?abstract)=\"fr\") "+
				"} "+
				"LIMIT 1";
		Query quer = QueryFactory.create(request);
		query = QueryExecutionFactory.sparqlService(service, quer.toString());
		
		try{
			ResultSet results = query.execSelect();

			while(results.hasNext()) {
				QuerySolution sol = (QuerySolution) results.next();
				String entity=sol.get("?entity").toString();
				String resume=sol.get("?abstract").toString();
				System.out.println(entity+" "+resume);
				Node noeud1=neoM.addNode();
				neoM.addProperty(noeud1, "entity",entity );
				neoM.addProperty(noeud1, "abstract",resume );
				switch(typeEntite){
				case "ville": neoM.addRelationship(ville, noeud1, RelTypes.HAS_TOWN);
					break;
				case "departement": neoM.addRelationship(departement, noeud1, RelTypes.HAS_DEPARTEMENT);
					break;
				case "region": neoM.addRelationship(region, noeud1, RelTypes.HAS_REGION);
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			query.close();
		}
	}
	
	public String printRegions(){
		Node regionNode=getRegionNode();
		Traverser regionsTraverser=getRegions(regionNode);
		String output="";
		for (Path regionPath : regionsTraverser){
			output+="Region: "+regionPath.endNode().getProperty("entity")+"\n"
					+"Résumé: "+regionPath.endNode().getProperty("abstract")+"\n";
		}
		return output;
	}
	
	public String printDepartement(){
		Node departementNode=getDepartementNode();
		Traverser departementsTraverser=getDepartements(departementNode);
		String output="";
		for (Path regionPath : departementsTraverser){
			output+="Département: "+regionPath.endNode().getProperty("entity")+"\n"
					+"Résumé: "+regionPath.endNode().getProperty("abstract")+"\n";
		}
		return output;
	}
	
	public String printVilles(){
		Node villeNode=getVilleNode();
		Traverser villesTraverser=getVilles(villeNode);
		String output="";
		for (Path regionPath : villesTraverser){
			output+="Ville: "+regionPath.endNode().getProperty("entity")+"\n"
					+"Résumé: "+regionPath.endNode().getProperty("abstract")+"\n";
		}
		return output;
	}
	
	private Node getRegionNode(){
		return neoM.dataBase.getNodeById(neoNodeId)
				.getSingleRelationship(RelTypes.REGION_NODE, Direction.OUTGOING)
				.getEndNode();
	}
	
	private Traverser getRegions(Node region){
		TraversalDescription td=Traversal.description()
				.breadthFirst()
				.relationships(RelTypes.HAS_REGION,Direction.OUTGOING)
				.evaluator(Evaluators.excludeStartPosition());
		return td.traverse(region);
	}
	
	private Node getDepartementNode(){
		return neoM.dataBase.getNodeById(neoNodeId)
				.getSingleRelationship(RelTypes.DEPARTEMENT_NODE, Direction.OUTGOING)
				.getEndNode();
	}
	
	private Traverser getDepartements(Node departement){
		TraversalDescription td=Traversal.description()
				.breadthFirst()
				.relationships(RelTypes.HAS_DEPARTEMENT,Direction.OUTGOING)
				.evaluator(Evaluators.excludeStartPosition());
		return td.traverse(departement);
	}
	
	private Node getVilleNode(){
		return neoM.dataBase.getNodeById(neoNodeId)
				.getSingleRelationship(RelTypes.TOWN_NODE, Direction.OUTGOING)
				.getEndNode(); 
	}
	
	private Traverser getVilles(Node ville){
		TraversalDescription td=Traversal.description()
				.breadthFirst()
				.relationships(RelTypes.HAS_TOWN,Direction.OUTGOING)
				.evaluator(Evaluators.excludeStartPosition());
		return td.traverse(ville);
	}
	
	public Model buildVillesModel(){
		Node villeNode=getVilleNode();
		Traverser villesTraverser=getVilles(villeNode);
		//String output="";
		for (Path villePath : villesTraverser){
			NeoOntology.Ville=m.createResource(villePath.endNode().getProperty("entity").toString());
			NeoOntology.Resume=m.createResource(villePath.endNode().getProperty("abstract").toString());
			m.add(NeoOntology.Ville,NeoOntology.VilleProp,NeoOntology.Ville);//TODO propriété à changer
			m.add(NeoOntology.Ville,DC.description,NeoOntology.Resume);
			/*output+="Ville: "+villePath.endNode().getProperty("entity")+"\n"
					+"Résumé: "+villePath.endNode().getProperty("abstract")+"\n";*/
		}
		//return output;
		return m;
	}
	
	public Model buildRegionsModel(){
		Node regionNode=getRegionNode();
		Traverser regionsTraverser=getRegions(regionNode);
		for (Path regionPath : regionsTraverser){
			NeoOntology.Region=m.createResource(regionPath.endNode().getProperty("entity").toString());
			NeoOntology.Resume=m.createResource(regionPath.endNode().getProperty("abstract").toString());
			m.add(NeoOntology.Region,NeoOntology.RegionProp,NeoOntology.Region);//TODO propriété à changer
			m.add(NeoOntology.Region,DC.description,NeoOntology.Resume);
		}
		return m;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Test t=new Test(0);
		Model m=t.buildRegionsModel();
		t.m.add(m);
		
		String NL=System.getProperty("line.separator");
		String neo="PREFIX neo: <http://www.neo4j.org/>";
		String dc="PREFIX dc: <"+DC.getURI()+">";
		String region="Languedoc-Roussillon";
		Query qu=QueryFactory.create(neo+NL+dc+NL+"SELECT ?abstract { "
				+ "?truc neo:regionProp ?region . "
				+ "?truc dc:description ?abstract "
				+ "FILTER regex(str(?region),\""+region+"\") "
				+ "}");
		//\""+region+"\"
		QueryExecution query=QueryExecutionFactory.create(qu,m);
		ResultSet recup=query.execSelect();
		while(recup.hasNext()){
			QuerySolution sol=recup.next();
			System.out.println(sol.get("?abstract").toString());
		}
	}

}
