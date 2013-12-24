package models;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.rest.graphdb.RestGraphDatabase;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.DC;

public class NeoRequest {
	
	public final String prefix="http://neo4j.org#";
	public final String neo="http://neo4j.org";
	public final String isLink="isLink";
	public NeoManager neoM;
	public long neoNodeId;
	private Model m;
	public static final String neoProp = "public/ressources/neo.properties";
	public GraphDatabaseService gds;
	
	public static enum RelTypes implements RelationshipType{
		REGION_NODE,
		DEPARTEMENT_NODE,
		TOWN_NODE,
		HAS_REGION,
		HAS_DEPARTEMENT,
		HAS_TOWN
	}

	public NeoRequest(){
		//neoM=NeoManager.getInstance();
		gds = new RestGraphDatabase("http://localhost:7474/db/data");
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
	
	public Model buildRegionsModel(String region){
		Node regionNode=getRegionNode();
		Traverser regionsTraverser=getRegions(regionNode);
		for (Path regionPath : regionsTraverser){
			String reg=regionPath.endNode().getProperty("entity").toString();
			if (reg.matches(region+"@en")){
				System.out.println("La région "+reg+" a été trouvé dans Neo4J");
				NeoOntology.Region=m.createResource(regionPath.endNode().getProperty("entity").toString());
				NeoOntology.Resume=m.createResource(regionPath.endNode().getProperty("abstract").toString());
				m.add(NeoOntology.Region,NeoOntology.RegionProp,NeoOntology.Region);
				m.add(NeoOntology.Region,DC.description,NeoOntology.Resume);
				break;
			}
		}
		return m;
	}
	
	public Model buildDepartementModel(String departement){
		Node departementNode=getDepartementNode();
		Traverser departementsTraverser=getDepartements(departementNode);

		for (Path depPath : departementsTraverser){
			String dep=depPath.endNode().getProperty("entity").toString();
			if (dep.matches(departement+"@en")){
				NeoOntology.Departement=m.createResource(depPath.endNode().getProperty("entity").toString());
				NeoOntology.Resume=m.createResource(depPath.endNode().getProperty("abstract").toString());
				m.add(NeoOntology.Departement,NeoOntology.DepartementProp,NeoOntology.Departement);
				m.add(NeoOntology.Departement,DC.description,NeoOntology.Resume);
				break;
			}
		}
		return m;
	}
	
	public Model buildVillesModel(String ville){
		Node villeNode=getVilleNode();
		Traverser villesTraverser=getVilles(villeNode);
		for (Path villePath : villesTraverser){
			String vil=villePath.endNode().getProperty("entity").toString();
			if (vil.matches(ville+"@en")){
				NeoOntology.Ville=m.createResource(villePath.endNode().getProperty("entity").toString());
				NeoOntology.Resume=m.createResource(villePath.endNode().getProperty("abstract").toString());
				m.add(NeoOntology.Ville,NeoOntology.VilleProp,NeoOntology.Ville);
				m.add(NeoOntology.Ville,DC.description,NeoOntology.Resume);
				break;
			}
		}
		return m;
	}
	
	private Node getRegionNode(){
		return gds.getNodeById(neoNodeId)
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
		return gds.getNodeById(neoNodeId)
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
		return gds.getNodeById(neoNodeId)
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
