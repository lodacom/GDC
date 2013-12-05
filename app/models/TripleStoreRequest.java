package models;

import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB;

import java.io.File;
import java.util.*;

import com.hp.hpl.jena.util.FileManager;

public class TripleStoreRequest
{
	public static final String rdf_file_3 = "public/ressources/geonames_v3.rdf";
	public static final String repertoire = "public/ressources/Geonames/Departements";
	public static final String directory = "public/ressources/GeoDatabase" ;
	public Model tsr;
	
    public TripleStoreRequest()
    {
        // Make a TDB-back Jena model in the named directory.
        Dataset ds = TDBFactory.createDataset(directory) ;
        Model reg = ds.getNamedModel( "gazeetter" ); 
        FileManager.get().readModel( reg, rdf_file_3);
        File direct=new File(repertoire);
        
        File[] fichiers=direct.listFiles();
        for (int i=0;i<fichiers.length;i++){
        	//System.out.println("ici");
        	FileManager.get().readModel(reg, fichiers[i].getPath());
        }
        
        Model	model = null;     
        Iterator<String> graphNames = ds.listNames();
        while (graphNames.hasNext()) {
            String graphName = graphNames.next();       
            model = ds.getNamedModel(graphName);
      		System.out.println("Named graph " + graphName + " size: " + model.size());
       	}  	       
        //ds.close();
    }
    
    public void consult(){
        Dataset ds = TDBFactory.createDataset(directory) ;
        Model reg = ds.getNamedModel( "gazeetter" ); 
        Model dm = ds.getDefaultModel();  
        //dm.add(reg);
        tsr=reg;
    }
}