package models;

import com.hp.hpl.jena.rdf.model.Model;
import de.fuberlin.wiwiss.d2rq.jena.ModelD2RQ;

public class OracleRequest {
	public Model d2rq;
	
	public OracleRequest(){
		d2rq=new ModelD2RQ("public/ressources/oracle-mapping.ttl");
	}
}
