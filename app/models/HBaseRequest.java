package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class HBaseRequest {

	private Model m;
	
	public HBaseRequest(){
		
	}
	
	public String conversion(String entity){
		String retour=entity;
		retour=retour.replaceAll("Ã¨", "è");
		retour=retour.replaceAll("Ã´", "ô");
		retour=retour.replaceAll("Ã©", "é");
		return retour;
	}
	
	@SuppressWarnings("deprecation")
	public Model regionFilter(String region){
		m=ModelFactory.createDefaultModel();
		
		Configuration conf = HBaseConfiguration.create();

		HTable table =null;
		try {
			table = new HTable(conf, "region");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Filter> filters = new ArrayList<Filter>();

		Filter famFilter = new FamilyFilter(CompareFilter.CompareOp.EQUAL,
				new BinaryComparator(Bytes.toBytes("region")));
		filters.add(famFilter);

		Filter colFilter = new QualifierFilter(CompareFilter.CompareOp.EQUAL,
				new BinaryComparator(Bytes.toBytes("2009")));
		filters.add(colFilter);
		
		FilterList fl = new FilterList( FilterList.Operator.MUST_PASS_ALL, filters);


	    Scan scan = new Scan();
	    scan.setFilter(fl);
	    try{
		    ResultScanner scanner = table.getScanner(scan);
		    for (Result result : scanner) {
		        for (KeyValue kv : result.raw()) {
		        	String recup=conversion(Bytes.toString(kv.getRow()));
		        	if (recup.equals(region)){
		        		System.out.println("Key: " + Bytes.toString(kv.getRow())  + ", Value: " +Bytes.toString(kv.getValue()));
		        		HBaseOntology.Region=m.createResource(Bytes.toString(kv.getRow()));
		        		HBaseOntology.RSA=m.createResource(Bytes.toString(kv.getValue()));
		        		m.add(HBaseOntology.Region, HBaseOntology.RegionProp, HBaseOntology.Region);
		        		m.add(HBaseOntology.Region,HBaseOntology.RSA_2009Prop,HBaseOntology.RSA);
		        		break;
		        	}
		        }
		    }   
		    scanner.close();
	    }catch(IOException io){
	    	
	    }
	    
	    List<Filter> filters2 = new ArrayList<Filter>();

		Filter famFilter2 = new FamilyFilter(CompareFilter.CompareOp.EQUAL,
				new BinaryComparator(Bytes.toBytes("region")));
		filters2.add(famFilter2);

		Filter colFilter2 = new QualifierFilter(CompareFilter.CompareOp.EQUAL,
				new BinaryComparator(Bytes.toBytes("2010")));
		filters2.add(colFilter2);
		
		FilterList fl2 = new FilterList( FilterList.Operator.MUST_PASS_ALL, filters2);


	    Scan scan2 = new Scan();
	    scan2.setFilter(fl2);
	    try{
		    ResultScanner scanner = table.getScanner(scan2);
		    for (Result result : scanner) {
		        for (KeyValue kv : result.raw()) {
		        	String recup=conversion(Bytes.toString(kv.getRow()));
		        	if (recup.equals(region)){
		        		System.out.println("Key: " + Bytes.toString(kv.getRow())  + ", Value: " +Bytes.toString(kv.getValue()));
		        		HBaseOntology.Region=m.createResource(Bytes.toString(kv.getRow()));
		        		HBaseOntology.RSA=m.createResource(Bytes.toString(kv.getValue()));
		        		m.add(HBaseOntology.Region,HBaseOntology.RSA_2010Prop,HBaseOntology.RSA);
		        		break;
		        	}
		        }
		    }   
		    scanner.close();
	    }catch(IOException io){
	    	
	    }
	    return m;
	}
	
	@SuppressWarnings("deprecation")
	public Model departementFilter(String departement){
		m=ModelFactory.createDefaultModel();
		
		Configuration conf = HBaseConfiguration.create();

		HTable table =null;
		try {
			table = new HTable(conf, "departement");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Filter> filters = new ArrayList<Filter>();

		Filter famFilter = new FamilyFilter(CompareFilter.CompareOp.EQUAL,
				new BinaryComparator(Bytes.toBytes("departement")));
		filters.add(famFilter);

		Filter colFilter = new QualifierFilter(CompareFilter.CompareOp.EQUAL,
				new BinaryComparator(Bytes.toBytes("2009")));
		filters.add(colFilter);
		
		FilterList fl = new FilterList( FilterList.Operator.MUST_PASS_ALL, filters);


	    Scan scan = new Scan();
	    scan.setFilter(fl);
	    try{
		    ResultScanner scanner = table.getScanner(scan);
		    for (Result result : scanner) {
		        for (KeyValue kv : result.raw()) {
		        	String recup=conversion(Bytes.toString(kv.getRow()));
		        	if (recup.equals(departement)){
		        		System.out.println("Key: " + Bytes.toString(kv.getRow())  + ", Value: " +Bytes.toString(kv.getValue()));
		        		HBaseOntology.Departement=m.createResource(Bytes.toString(kv.getRow()));
		        		HBaseOntology.RSA=m.createResource(Bytes.toString(kv.getValue()));
		        		m.add(HBaseOntology.Departement, HBaseOntology.DepartementProp, HBaseOntology.Departement);
		        		m.add(HBaseOntology.Departement,HBaseOntology.RSA_2009Prop,HBaseOntology.RSA);
		        		break;
		        	}
		        }
		    }   
		    scanner.close();
	    }catch(IOException io){
	    	
	    }
	    
	    List<Filter> filters2 = new ArrayList<Filter>();

		Filter famFilter2 = new FamilyFilter(CompareFilter.CompareOp.EQUAL,
				new BinaryComparator(Bytes.toBytes("departement")));
		filters2.add(famFilter2);

		Filter colFilter2 = new QualifierFilter(CompareFilter.CompareOp.EQUAL,
				new BinaryComparator(Bytes.toBytes("2010")));
		filters2.add(colFilter2);
		
		FilterList fl2 = new FilterList( FilterList.Operator.MUST_PASS_ALL, filters2);


	    Scan scan2 = new Scan();
	    scan2.setFilter(fl2);
	    try{
		    ResultScanner scanner = table.getScanner(scan2);
		    for (Result result : scanner) {
		        for (KeyValue kv : result.raw()) {
		        	String recup=conversion(Bytes.toString(kv.getRow()));
		        	if (recup.equals(departement)){
		        		System.out.println("Key: " + Bytes.toString(kv.getRow())  + ", Value: " +Bytes.toString(kv.getValue()));
		        		HBaseOntology.Departement=m.createResource(Bytes.toString(kv.getRow()));
		        		HBaseOntology.RSA=m.createResource(Bytes.toString(kv.getValue()));
		        		m.add(HBaseOntology.Departement,HBaseOntology.RSA_2010Prop,HBaseOntology.RSA);
		        		break;
		        	}
		        }
		    }   
		    scanner.close();
	    }catch(IOException io){
	    	
	    }
	    return m;
	}
}
