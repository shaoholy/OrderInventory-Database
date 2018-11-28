import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.derby.iapi.services.info.ProductGenusNames;

public class Test_SQLProject {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 // the default framework is embedded
		String protocol = "jdbc:derby:";
	    String dbName = "ordersystem";
		String connStr = protocol + dbName+ ";create=true";

		// tables created by this program
		String[] dbTables = {
			"InventoryRecord", "OrderRecord",
			"Orders", "Product", "CUSTOMER"
    	    };
		
		// function created by this program
		String[] functions = {
			"checkAvailable", "checkSKUFunction"
    	    };

		Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derbyclient frameworks
        props.put("user", "user1");
        props.put("password", "user1");
        
        // result set for queries
        ResultSet rs = null;
        
        try (
    			// open data file
    			BufferedReader brCustomer = new BufferedReader(new FileReader(new File("Customer.txt")));
        		BufferedReader brProduct = new BufferedReader(new FileReader(new File("Product.txt")));
//        		BufferedReader brOrders = new BufferedReader(new FileReader(new File("Orders.txt")));
        		BufferedReader brInventoryRecord = new BufferedReader(new FileReader(new File("Inventory.txt")));
//        		BufferedReader brOrderRecord = new BufferedReader(new FileReader(new File("OrderRecord.txt")));
    			
    			// connect to database
    			Connection  conn = DriverManager.getConnection(connStr, props);
    			Statement stmt = conn.createStatement();
    			
    			// insert prepared statements for insert tuples
    			PreparedStatement insertRow_Customer = conn.prepareStatement(
    					"insert into Customer values(?, ?, ?, ?, ?, ?, ?)");
    			PreparedStatement insertRow_Product  = conn.prepareStatement(
    					"insert into Product values(?, ?, ?)");
    			PreparedStatement insertRow_Orders = conn.prepareStatement(
    					"insert into Orders values(?, ?, ?, ?)");
    			PreparedStatement insertRow_InventoryRecord = conn.prepareStatement(
    					"insert into InventoryRecord values(?, ?, ?)");
    			PreparedStatement insertRow_OrderRecord = conn.prepareStatement(
    					"insert into OrderRecord values(?, ?, ?)");
    		) {
    			// connect to the database using URL
                System.out.println("Connected to database " + dbName);
                
            // clear data from tables
            for (String tbl : dbTables) {
    	            try {
    	            		stmt.executeUpdate("delete from " + tbl);
    	            		System.out.println("Truncated table " + tbl);
    	            } catch (SQLException ex) {
    	            		System.out.println("Did not truncate table " + tbl);
    	            }
            }
            
            
                
            String line;
            //insert customer infor
			while ((line = brCustomer.readLine()) != null) {
				String[] data = line.split("\t");
				if (data.length != 7) {
					continue; 
				}
				// get fields from input data
				int customerID = Integer.parseInt(data[0]);
				String customerName = data[1];
				String address = data[2];
				String city = data[3];
				String state = data[4];  
				String country = data[5];
				int zipcode = Integer.parseInt(data[6]);
				
				try {
					insertRow_Customer.setInt(1, customerID);
					insertRow_Customer.setString(2, customerName);
					insertRow_Customer.setString(3, address);
					insertRow_Customer.setString(4, city);
					insertRow_Customer.setString(5, state);
					insertRow_Customer.setString(6, country);
					insertRow_Customer.setInt(7, zipcode);
					insertRow_Customer.execute();
				} catch (SQLException ex) {
					// already exists
					System.err.printf("Unable to insert Customer %s", customerName);
				}
			}
			
			//insert product infor
			while ((line = brProduct.readLine()) != null) {
				String[] data = line.split("\t");
				
				if (data.length != 3) {
					continue; 
				}
				//System.out.println(line);
				// get fields from input data
				String productName = data[0];
				String des = data[1];
				String SKU = data[2];

				
				try {
					insertRow_Product.setString(1, productName);
					insertRow_Product.setString(2, des);
					insertRow_Product.setString(3, SKU);
					insertRow_Product.execute();
				} catch (SQLException ex) {
					// already exists
					System.err.printf("Unable to insert SKU %s", SKU);
				}
			}
			
			//insert inventory infor
			while ((line = brInventoryRecord.readLine()) != null) {
				String[] data = line.split("\t");
				
				if (data.length != 3) {
					continue; 
				}
				//System.out.println(line);
				// get fields from input data
				int number = Integer.parseInt(data[0]);
				double price = Double.parseDouble(data[1]);
				String SKU = data[2];

				
				try {
					insertRow_InventoryRecord.setInt(1, number);
					insertRow_InventoryRecord.setDouble(2, price);
					insertRow_InventoryRecord.setString(3, SKU);
					insertRow_InventoryRecord.execute();
				} catch (SQLException ex) {
					// already exists
					System.err.printf("Unable to insert SKU %s", SKU);
				}
			}
			//insert an order
			try {
				insertRow_Orders.setInt(1, 1);
				insertRow_Orders.setInt(2, 2);
				insertRow_Orders.setDate(3, Date.valueOf("2018-10-10"));
				insertRow_Orders.setDate(4, Date.valueOf("2018-10-21"));
				insertRow_Orders.execute();
			} catch (SQLException ex) {
				// already exists
				System.err.println("Unable to insert Order");
			}
			
			
			
			// print number of rows in tables
			//for (String tbl : dbTables) {
				rs = stmt.executeQuery("select * from Customer");
				while (rs.next()) {
	        			String name = rs.getString("Name");
	        			System.out.printf("%-16s", name);
	        			System.out.println();
	            }
				
				rs = stmt.executeQuery("select * from Product");
				while (rs.next()) {
	        			String name = rs.getString("Name");
	        			System.out.printf("%-16s", name);
	        			System.out.println();
	            }
				
				rs = stmt.executeQuery("select * from InventoryRecord");
				while (rs.next()) {
	        			String SKU = rs.getString("SKU");
	        			int num = rs.getInt("AvailableNum");
	        			System.out.printf("%-16s", SKU);
	        			System.out.println(num);
	            }
				
				rs = stmt.executeQuery("select * from Orders");
				while (rs.next()) {
	        			Date orderDate = rs.getDate(3);
	        			System.out.println(orderDate.toString());
	            }
				
			//}
				
			//insert an orderRecord within inventory availability 
			try {
				insertRow_OrderRecord.setString(1, "AA-111112-A1");
				insertRow_OrderRecord.setInt(2, 1);
				insertRow_OrderRecord.setInt(3, 5);
				insertRow_OrderRecord.execute();
			} catch (SQLException ex) {
				// already exists
				ex.printStackTrace();
				System.err.println("Unable to insert Record");
			}
    			
    			//print out the inventory after orderRecord, availableNum decremented to 5. 
    			rs = stmt.executeQuery("select * from InventoryRecord");
			while (rs.next()) {
        			String SKU = rs.getString("SKU");
        			int num = rs.getInt("AvailableNum");
        			System.out.printf("%-16s", SKU);
        			System.out.println(num);
            }
			
			//try to make an order record beyond inventory availability, meant to fail
			try {
				insertRow_OrderRecord.setString(1, "AA-111112-A1");
				insertRow_OrderRecord.setInt(2, 1);
				insertRow_OrderRecord.setInt(3, 20);
				insertRow_OrderRecord.execute();
			} catch (SQLException ex) {
				// already exists
				ex.printStackTrace();
				System.err.println("Unable to insert Record");
			}
			
			//print out the inventory after a failed orderRecord insert, availableNum remained as 5. 
			rs = stmt.executeQuery("select * from InventoryRecord");
			while (rs.next()) {
        			String SKU = rs.getString("SKU");
        			int num = rs.getInt("AvailableNum");
        			System.out.printf("%-16s", SKU);
        			System.out.println(num);
            }
			
			rs.close();

    		} catch (IOException e) {
    			e.printStackTrace();
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
	}

}
