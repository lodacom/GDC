package controllers;

import models.EndPointQueries;
import models.GlobalRequest;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import play.libs.Json;

public class ControlEndPointSparql extends Controller {

	public static Result index(){
		return ok(endpoint_sparql.render());
	}

	public static Result query(String query,String format){
		GlobalRequest gr=new GlobalRequest();			
		if(format.equals("auto") || format.equals("text/html")){
			EndPointQueries epq=gr.userQueriesFromEndPoint(query);
			return ok(endpoint_response.render(epq));
		}
		if(format.contains("json")){
			EndPointQueries epq=gr.userQueriesFromEndPoint(query);
			return ok(Json.toJson(epq.response));
		}
		/*if(format.contains("rdf+xml")){
			String rdf_response=gr.userQueriesResponseFromRDF(query);
			return ok(rdf_response);
		}*/
		return ok(endpoint_sparql.render());

	}
}
