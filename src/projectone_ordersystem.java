import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class projectone_ordersystem {
	
	//check if input order quantity is able to be covered by current 
	public static boolean checkAvailable(int a, String SKU) {
		String protocol = "jdbc:derby:";
	    String dbName = "ordersystem";
		String connStr = protocol + dbName + ";create=true";
		Properties props = new Properties(); // connection properties
        props.put("user", "user1");
        props.put("password", "user1");
        try (
    	        // connect to the database using URL
    			Connection conn = DriverManager.getConnection(connStr, props);
    				
    	        // statement is channel for sending commands thru connection 
    	        Statement stmt = conn.createStatement();
    		){
    	        System.out.println("Connected to and created database " + dbName);
    	        return false; 
        } catch (SQLException e) {
			e.printStackTrace();
		}
        return true;
	}
	
	//check SKU String valid or not
	public static boolean checkSKU(String SKU) {
		String[] SKUarr = SKU.split("-"); 
		if (SKUarr.length != 3 || SKUarr[0].length() != 2 || SKUarr[1].length() != 6 || SKUarr[2].length() != 2) {
			return false;
		}
		for (int i = 0; i < 2; i++) {
			char cur = SKUarr[0].charAt(i); 
			if (!Character.isUpperCase(cur)) {
				return false; 
			}
		}
		for (int i = 0; i < 6; i++) {
			char cur = SKUarr[1].charAt(i); 
			if (!Character.isDigit(cur)) {
				return false; 
			}
		}
		for (int i = 0; i < 2; i++) {
			char cur = SKUarr[1].charAt(i); 
			if (!Character.isDigit(cur) && !Character.isUpperCase(cur)) {
				return false; 
			}
		}
		return true; 
	}

	public static void main(String[] args) {
		// the default framework is embedded
	    String protocol = "jdbc:derby:";
	    String dbName = "ordersystem";
		String connStr = protocol + dbName + ";create=true";

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
        
        
        try (
    	        // connect to the database using URL
    			Connection conn = DriverManager.getConnection(connStr, props);
    				
    	        // statement is channel for sending commands thru connection 
    	        Statement stmt = conn.createStatement();
    		){
    	        System.out.println("Connected to and created database " + dbName);
                
    	        //drop trigger
    	        try {
            		stmt.executeUpdate("drop trigger " + " UpdateInventoryByOrder");
            		System.out.println("Dropped trigger " + " UpdateInventoryByOrder");
            } catch (SQLException ex) {
            		System.out.println("Did not drop trigger " + " UpdateInventoryByOrder");
            }
    	        
        		// drop the database tables and recreate them below
            for (String tbl : dbTables) {
    	            try {
    	            		stmt.executeUpdate("drop table " + tbl);
    	            		System.out.println("Dropped table " + tbl);
    	            } catch (SQLException ex) {
    	            		System.out.println("Did not drop table " + tbl);
    	            }
            }
            // drop the database functions and recreate them below
            for (String fn : functions) {
    	            try {
    	            		stmt.executeUpdate("drop function " + fn);
    	            		System.out.println("Dropped function " + fn);
    	            } catch (SQLException ex) {
    	            		System.out.println("Did not drop function " + fn);
    	            }
            }
                
             // create check SKU legitimacy check function
                String checkSKUFunction = 
                "CREATE FUNCTION checkSKUFunction("
                			   + " SKU varchar(16))"
                			   + " RETURNS boolean"
                			   + " PARAMETER STYLE JAVA"
                			   + " LANGUAGE JAVA" 
                			   + " DETERMINISTIC" 
                			   + " NO SQL" 
                			   + " EXTERNAL NAME "
                			   + "	'projectone_ordersystem.checkSKU'";
                stmt.executeUpdate(checkSKUFunction);
                System.out.println("Created checkSKU Function");
                
                // create the Product tableProduct 
                String createTable_Product =
                		  "create table Product("
                		+ "  Name varchar(32) NOT NULL,"
                		+ "  Description varchar(32) NOT NULL,"
                		+ "  SKU varchar(16) NOT NULL CHECK(checkSKUFunction(SKU)),"
                		+ "  PRIMARY KEY (SKU)"
                		+ ")";
                stmt.executeUpdate(createTable_Product);
                System.out.println("Created entity table Product");
                
                // create the Customer table
                String createTable_Customer =
                		  "create table Customer("
    				    + "  CustomerID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
    				    //+ "  CustomerID int NOT NULL, "
                		+ "  Name varchar(32) NOT NULL,"
                		+ "  address varchar(64) NOT NULL,"
                		+ "  city varchar(16) NOT NULL,"
                		+ "  state varchar(16) NOT NULL,"
                		+ "  country varchar(16) NOT NULL,"
                		+ "  zipcode int NOT NULL CHECK (zipcode > 0 AND zipcode < 100000),"
                		+ "  PRIMARY KEY (CustomerID)"
                		+ ")";
                stmt.executeUpdate(createTable_Customer);
                System.out.println("Created entity table Customer");
                
                // create the InventoryRecord table
                String createTable_InventoryRecord =
                		  "create table InventoryRecord("
                		+ "  AvailableNum int NOT NULL CHECK (AvailableNum >= 0),"
                		+ "  Price decimal NOT NULL CHECK (Price >= 0),"
                		+ "  SKU varchar(16) NOT NULL UNIQUE,"
                		+ "  FOREIGN KEY (SKU) REFERENCES Product (SKU) ON DELETE CASCADE"
                		+ ")";
                stmt.executeUpdate(createTable_InventoryRecord);
                System.out.println("Created relation table InventoryRecord");
                
                // create the Order table
                String createTable_Orders =
                		  "create table Orders("
    				    + "  OrderID int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
    				    //+ "  OrderID int NOT NULL,"
                		+ "  CustomerID int NOT NULL,"
                		+ "  OrderDate date NOT NULL,"
                		+ "  ShipmentDate date,"
                		+ "  PRIMARY KEY (OrderID),"
                		+ "  FOREIGN KEY (CustomerID) REFERENCES Customer (CustomerID) ON DELETE CASCADE"
                		+ ")";
                stmt.executeUpdate(createTable_Orders);
                System.out.println("Created entity table Orders");
                
                // create the Author entity table
                String createTable_OrderRecord =
                		  "create table OrderRecord("
                		+ "  ProductSKU varchar(16) NOT NULL,"
                		+ "  OrderID int NOT NULL,"
                		+ "  Number int NOT NULL,"
                		+ "  FOREIGN KEY (OrderID) REFERENCES Orders (OrderID) ON DELETE CASCADE"
                		+ ")";
                stmt.executeUpdate(createTable_OrderRecord);
                System.out.println("Created entity table OrderRecord");
               
                //update inventory when order inserted
                String createTrigger_UpdateInventoryByOrder =
                		  "CREATE TRIGGER UpdateInventoryByOrder"
                		+ "  AFTER INSERT ON OrderRecord"
                		+ "  REFERENCING NEW AS NEW"
                		+ "  FOR EACH ROW MODE DB2SQL"
                		+ "  UPDATE InventoryRecord SET AvailableNum = AvailableNum - NEW.Number WHERE SKU = NEW.ProductSKU";
                stmt.executeUpdate(createTrigger_UpdateInventoryByOrder);
                System.out.println("Created UpdateInventoryByOrder Trigger");
                
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
	}
}
