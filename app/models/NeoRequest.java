package models;

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
import com.hp.hpl.jena.vocabulary.RDF;

public class NeoRequest extends GlobalRequest {

	public final String NL = System.getProperty("line.separator");
	public String service="http://dbpedia.org/sparql";
	public String dbprop="PREFIX dbpprop: <http://dbpedia.org/property/>";
	public String dbpedia_owl="PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>";
	public final String prefix="http://neo4j.org#";
	public final String neo="http://neo4j.org";
	public final String isLink="isLink";
	private QueryExecution query;
	public NeoManager neoM;
	public Node region,departement,ville;
	private long neoNodeId;
	private Model m;
	public static final String neoProp = "public/ressources/neo.properties";
	
	private static enum RelTypes implements RelationshipType{
		REGION_NODE,
		DEPARTEMENT_NODE,
		TOWN_NODE,
		HAS_REGION,
		HAS_DEPARTEMENT,
		HAS_TOWN
	}

	public NeoRequest(){
		neoM=NeoManager.getInstance();
		m=ModelFactory.createDefaultModel();
		m.setNsPrefix("neo",NeoOntology.getNeo());
		m.setNsPrefix("region", NeoOntology.getRegion());
		m.setNsPrefix("departement", NeoOntology.getDepartement());
		m.setNsPrefix("ville", NeoOntology.getVille());
	}

	public long getNeoNodeId() {
		return neoNodeId;
	}

	public void setNeoNodeId(long neoNodeId) {
		this.neoNodeId = neoNodeId;
	}

	public void create(){
		Node matrix=neoM.addNode();
		neoNodeId=matrix.getId();
		
		region=neoM.addNode();
		neoM.addProperty(region, "region", "region");
		departement=neoM.addNode();
		neoM.addProperty(departement, "departement", "departement");
		ville=neoM.addNode();
		neoM.addProperty(ville, "ville", "ville");
		neoM.addRelationship(matrix, region, RelTypes.REGION_NODE);
		neoM.addRelationship(matrix, departement, RelTypes.DEPARTEMENT_NODE);
		neoM.addRelationship(matrix, ville, RelTypes.TOWN_NODE);
		
		ArrayList<String> regions=this.regions();
		ArrayList<String> departements=this.departements();
		ArrayList<String> cities=this.cities();
		
		for (String ville:cities){
			searchAllEntities(ville, "ville");
		}
		for (String dep:departements){
			searchAllEntities(dep, "departement");
		}
		for (String reg:regions){
			searchAllEntities(reg, "region");
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
		addEntities(typeEntite);
	}

	public void addEntities(String typeEntite){
		try{
			ResultSet results = query.execSelect();

			while(results.hasNext()) {
				QuerySolution sol = (QuerySolution) results.next();
				String entity=sol.get("?entity").toString();
				String resume=sol.get("?abstract").toString();
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
	
	public void buildRegionsModel(){
		Node regionNode=getRegionNode();
		Traverser regionsTraverser=getRegions(regionNode);
		//String output="";
		for (Path regionPath : regionsTraverser){
			NeoOntology.Region=m.createResource(NeoOntology.getRegion()+regionPath.endNode().getProperty("entity"));
			NeoOntology.Resume=m.createResource(NeoOntology.getRegion()+regionPath.endNode().getProperty("abstract"));
			m.add(NeoOntology.Region,RDF.type,NeoOntology.Region);//TODO propriété à changer
			m.add(NeoOntology.Region,DC.description,NeoOntology.Resume);
			
			/*output+="Region: "+regionPath.endNode().getProperty("entity")+"\n"
					+"Résumé: "+regionPath.endNode().getProperty("abstract")+"\n";*/
		}
		//return output;
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
}
