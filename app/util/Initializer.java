package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import models.NeoRequest;

public class Initializer {

	public void neoInitializer(){
		NeoRequest neoR=new NeoRequest();

		File fich=new File(NeoRequest.neoProp);
		if (fich.length()==0){
			neoR.create();
			ecrire(NeoRequest.neoProp, "neoRoot "+neoR.getNeoNodeId());
		}
	}
	
	public void ecrire(String nomFic, String texte){
		try{
			FileWriter fw = new FileWriter(nomFic, false);
			BufferedWriter output = new BufferedWriter(fw);

			output.write(texte);
			output.flush();
			output.close();
		}
		catch(IOException ioe){
			System.out.print("Erreur : ");
			ioe.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Initializer init=new Initializer();
		init.neoInitializer();
	}

}
