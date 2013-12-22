package models;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
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
	
	public static enum RelTypes implements RelationshipType{
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
	
	public Model buildDepartementModel(){
		Node departementNode=getDepartementNode();
		Traverser departementsTraverser=getDepartements(departementNode);

		for (Path depPath : departementsTraverser){
			NeoOntology.Departement=m.createResource(depPath.endNode().getProperty("entity").toString());
			NeoOntology.Resume=m.createResource(depPath.endNode().getProperty("abstract").toString());
			m.add(NeoOntology.Departement,NeoOntology.DepartementProp,NeoOntology.Departement);//TODO propriété à changer
			m.add(NeoOntology.Departement,DC.description,NeoOntology.Resume);
		}
		return m;
	}
	
	public Model buildVillesModel(){
		Node villeNode=getVilleNode();
		Traverser villesTraverser=getVilles(villeNode);
		for (Path villePath : villesTraverser){
			NeoOntology.Ville=m.createResource(villePath.endNode().getProperty("entity").toString());
			NeoOntology.Resume=m.createResource(villePath.endNode().getProperty("abstract").toString());
			m.add(NeoOntology.Ville,NeoOntology.VilleProp,NeoOntology.Ville);//TODO propriété à changer
			m.add(NeoOntology.Ville,DC.description,NeoOntology.Resume);
		}
		return m;
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
