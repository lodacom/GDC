package models;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseManager {

	private static Configuration conf = null;

	static {
		conf = HBaseConfiguration.create();
	}

	public static void createTable(String tableName,String[] familys){
		try{ 
			HBaseAdmin admin = new HBaseAdmin(conf);
			if (admin.tableExists(tableName)) {
				System.out.println("table already exists!");
			} else {
				HTableDescriptor tableDesc = new HTableDescriptor(tableName);
				for (int i = 0; i < familys.length; i++) {
					tableDesc.addFamily(new HColumnDescriptor(familys[i]));
				}
				admin.createTable(tableDesc);
				System.out.println("create table " + tableName + " ok.");
			}
		}catch(Exception e){

		}
	}

	public static void addRecord(String tableName, String rowKey,
			String family, String qualifier, String value) {
		try {
			HTable table = new HTable(conf, tableName);
			Put put = new Put(Bytes.toBytes(rowKey));
			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes
					.toBytes(value));
			table.put(put);
			System.out.println("insert recored " + rowKey + " to table "
					+ tableName + " ok.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
