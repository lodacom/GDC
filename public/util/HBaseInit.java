package hbase;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import hbase.HBaseManager;

public class HBaseInit {

	public static final String region="Donnees_Region_RSA.csv";
	public static final String departement="Donnees_Departement_RSA.csv";
	public String chaine="";
	public Map<String,String[]> map;

	public HBaseInit(){
		map=new HashMap<String,String[]>();
	}

	public void createTables(){
		HBaseManager.deleteTables("region");
		HBaseManager.deleteTables("departement");
		String[] tabRegion={"region"};
		String[] tabDep={"departement"};
		HBaseManager.createTable("region",tabRegion);
		HBaseManager.createTable("departement",tabDep);
	}
	
	public void lireFichierRegion(){
		try{
			InputStream ips=new FileInputStream(region); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			int i=1;
			while ((ligne=br.readLine())!=null){
				chaine=ligne;
				if (i>=5 && i!=28){
					String[] columns=chaine.split(";");
					if (columns.length!=0){
						String entity=columns[0];
						entity=entity.replaceAll("R[0-9]+ ", "");
						String rsa_2009=columns[1];
						String rsa_2010=columns[5];
						String[] tab={rsa_2009,rsa_2010};
						map.put(entity, tab);
					}
				}
				i++;
			}
			br.close(); 
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}
		fillHBaseRegion();
	}

	public void lireFichierDepartement(){
		try{
			InputStream ips=new FileInputStream(departement); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			int i=1;
			while ((ligne=br.readLine())!=null){
				chaine=ligne;
				if (i>=6 && i<102){
					String[] columns=chaine.split(";");
					if (columns.length!=0){
						String entity=columns[0];
						entity=entity.replaceAll("D[0-9]+ |D[0-9][A-Z] ", "");
						String rsa_2009=columns[1];
						String rsa_2010=columns[5];
						String[] tab={rsa_2009,rsa_2010};
						map.put(entity, tab);
					}
				}
				i++;
			}
			br.close(); 
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}
		fillHBaseDepartement();
	}
	
	public void fillHBaseRegion(){
		Set<Entry<String, String[]>> recup=map.entrySet();
		Iterator<Entry<String, String[]>> iter=recup.iterator();
		while(iter.hasNext()){
			Entry<String, String[]> ent=iter.next();
			System.out.println(ent.getKey()+" "+ent.getValue()[0]+" "+ent.getValue()[1]);
			HBaseManager.addRecord("region", ent.getKey(), "region", "2009", ent.getValue()[0]);
			HBaseManager.addRecord("region", ent.getKey(), "region", "2010", ent.getValue()[1]);
		}
	}

	public void fillHBaseDepartement(){
		Set<Entry<String, String[]>> recup=map.entrySet();
		Iterator<Entry<String, String[]>> iter=recup.iterator();
		while(iter.hasNext()){
			Entry<String, String[]> ent=iter.next();
			System.out.println(ent.getKey()+" "+ent.getValue()[0]+" "+ent.getValue()[1]);
			HBaseManager.addRecord("departement", ent.getKey(), "departement", "2009", ent.getValue()[0]);
			HBaseManager.addRecord("departement", ent.getKey(), "departement", "2010", ent.getValue()[1]);
		}
	}
	
	public static void main(String[] args){
		HBaseInit init=new HBaseInit();
		init.createTables();
		init.lireFichierRegion();
		init.lireFichierDepartement();
	}
}
