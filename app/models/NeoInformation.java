package models;

public class NeoInformation {

	public String resume;
	
	public NeoInformation(String resume){
		this.resume=resume.replaceAll("@fr", "");
	}
}
