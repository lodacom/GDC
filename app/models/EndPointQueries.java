package models;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.Constraints.Required;

public class EndPointQueries {

	@Required
	public String query;
	public List<ArrayList<String>> response;
	public List<String> headOfArray;
}
