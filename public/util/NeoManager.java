package neo;

import java.io.File;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class NeoManager {

	private static volatile NeoManager instance=null;
	public GraphDatabaseService dataBase;
	public final static String path="C:/Users/Lolo/Documents/Neo4j";
	
	private NeoManager(){
		//deleteFileOrDirectory(new File(path));
		dataBase = new GraphDatabaseFactory().newEmbeddedDatabase(path);
		registerShutdownHook();
	}

	public final static NeoManager getInstance(){
		if (NeoManager.instance==null){
			synchronized (NeoManager.class) {
				if (NeoManager.instance==null){
					NeoManager.instance=new NeoManager();
				}
			}
		}
		return NeoManager.instance;
	}

	public void shutdown(){
		dataBase.shutdown();
	}

	public Node addNode(){
		Transaction tr=beginTransaction();
		Node retour=null;
		try{
			System.out.println("Ajout d'un noeud");
			retour=dataBase.createNode();
			tr.success();
		}finally{
			tr.finish();
		}
		return retour;
	}

	public void addProperty(Node node,String name,Object val){
		Transaction tr=beginTransaction();
		try{
			System.out.println("Ajout d'une propriété au noeud: "+node.getId());
			node.setProperty(name, val);
			tr.success();
		}finally{
			tr.finish();
		}
	}

	public void addRelationship(Node node,Node node2,RelationshipType relation){
		Transaction tr=beginTransaction();
		try{
			System.out.println("Ajout d'une relation "+relation.name()+" entre le noeud: "+node.getId()+" et le noeud: "+node2.getId());
			node.createRelationshipTo(node2, relation);
			tr.success();
		}finally{
			tr.finish();
		}
	}

	private Transaction beginTransaction(){
		return dataBase.beginTx();
	}

	private static void deleteFileOrDirectory(final File file){
		if (!file.exists()){
			return;
		}
		if (file.isDirectory()){
			for (File child : file.listFiles()){
				deleteFileOrDirectory( child );
			}
		}else{
			file.delete();
		}
	}

	private void registerShutdownHook(){
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run(){
				shutdown();
			}
		});
	}
}
