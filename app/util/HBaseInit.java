package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HBaseInit {

	public static final String region="public/ressources/HBase/Donnees_Region_RSA.csv";
	public String chaine="";
	public Map<String,String[]> map;
	
	public HBaseInit(){
		map=new HashMap<String,String[]>();
	}
	
	public void lireFichier(){
		try{
			InputStream ips=new FileInputStream(region); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			int i=1;
			while ((ligne=br.readLine())!=null){
				chaine=ligne+"\n";
				if (i>=5 && i!=28){
					String[] columns=chaine.split(";");
					String entity=columns[0];
					entity=entity.replaceAll("R[0-9]+ ", "");
					String rsa_2009=columns[1];
					String rsa_2010=columns[5];
					String[] tab={rsa_2009,rsa_2010};
					map.put(entity, tab);
				}
				i++;
			}
			br.close(); 
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}
		fillHBase();
	}
	
	public void fillHBase(){
		Set<Entry<String, String[]>> recup=map.entrySet();
		Iterator<Entry<String, String[]>> iter=recup.iterator();
		while(iter.hasNext()){
			Entry<String, String[]> ent=iter.next();
			System.out.println(ent.getKey()+" "+ent.getValue()[0]+" "+ent.getValue()[1]);
		}
	}
	
	public static void main(String[] args){
		HBaseInit init=new HBaseInit();
		init.lireFichier();
	}
}
