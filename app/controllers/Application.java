package controllers;

import java.util.*;

import models.GlobalRequest;
import models.SearchForm;
import models.EntityInformation;
import play.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	static Form<SearchForm> formulaire=Form.form(SearchForm.class);
	static List<EntityInformation> infoEntites=null;
	
	public static Result index() {
		GlobalRequest gr=new GlobalRequest();
		List<String> regions=gr.regions();
		List<String> departements=gr.departements();
		List<String> villes=gr.cities();

		List<EntityInformation> infoRegions=null;
		return ok(index.render(regions,departements,villes,infoRegions));
	}

	public static Result affichage(){
		GlobalRequest gr=new GlobalRequest();
		List<String> regions=gr.regions();
		List<String> departements=gr.departements();
		List<String> villes=gr.cities();

		Form<SearchForm> filledForm = formulaire.bindFromRequest();
		if(filledForm.hasErrors()) {
			return ok(index.render(regions,departements,villes,infoEntites));
		}else{
			String region=filledForm.field("regions").value();
			String departement=filledForm.field("departements").value();
			String ville=filledForm.field("villes").value();
			String radio=filledForm.field("radio_zone").value();
			switch(radio){
			case "radio_region":
				infoEntites=gr.regions(region);
				break;
			case "radio_departement":
				infoEntites=gr.departements(departement);
				break;
			case "radio_ville":

				break;
			}
			return ok(index.render(regions,departements,villes,infoEntites));
		}
	}


	public static Result search(){
		return ok(Json.toJson(infoEntites));
	}
}
