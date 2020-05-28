/*
 * JDBC Loader for Sales Digitial Hub Project
 * 
 * Glenn Wiebe, GridGain Systems
 */

package com.gridgain.sales.load;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.IgniteDataStreamerTimeoutException;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;

import com.gridgain.sales.model.Customer;
import com.gridgain.sales.model.Employee;
import com.gridgain.sales.model.Office;
import com.gridgain.sales.model.Order;
import com.gridgain.sales.model.OrderDetail;
import com.gridgain.sales.model.OrderDetailKey;
import com.gridgain.sales.model.Payment;
import com.gridgain.sales.model.PaymentKey;
import com.gridgain.sales.model.Product;
import com.gridgain.sales.model.ProductLine;
import com.gridgain.sales.utility.ParseTypes;

import javax.cache.Cache;
import javax.cache.CacheException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Ignite Client loads data from JDBC Tables to Caches via Ignite DataStreamer
 */
public class LoadCachesFromJdbc {

	/**
	 * Start up a client node using SpringBean config
	 *
	 * @param args Command line arguments, none required.
	 * @throws IgniteException If failed.
	 */
	public static void main(String[] args) throws IgniteException, IOException {
		System.out.println(">>> Stream Loading caches from JDBC SELECT...");

		// for each table set the Load Query and then run it
		String loadQuery = "";
		Connection conn = null;
		Statement loadStmt = null;
		LoadCachesFromJdbc loadCaches = new LoadCachesFromJdbc(); // create static instance

		System.out.println(">>> Start Ignite Client...");
		try (Ignite ignite = Ignition.start("sales-client.xml")) {

			System.out.println(">>> Get Connection to source database...");
			conn = loadCaches.getConnection();

			/**
			 * Office Table
			 */
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> SELECT FROM ProductLine Table, Stream to OfficeCache...");
			loadQuery = "SELECT officeCode, city, phone, addressLine1, addressLine2, state, country, postalCode, territory " +
			            "  FROM offices" ;

			// If we can get the cache, then lets try to get from JDBC and Stream to it.
			try (IgniteDataStreamer<String, Office> streamer = ignite.dataStreamer("OfficeCache")) {

				System.out.println(">>> try to create statement and execute query:"+ loadQuery +"...");
				int n = 0; // record counter
				try {
					loadStmt = conn.createStatement();
					ResultSet rs = loadStmt.executeQuery(loadQuery);

					while (rs.next()) {
						String k = null;
						Office v = null;
						try {
							k = rs.getString("officeCode");
							v = new Office(
								rs.getString("city"),
								rs.getString("phone"),
								rs.getString("addressLine1"),
								rs.getString("addressLine2"),
								rs.getString("state"),
								rs.getString("country"),
								rs.getString("postalCode"),
								rs.getString("territory")
							);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						streamer.addData(k, v);
						n++;
					}
				} catch (SQLException e ) {
					System.out.println("Caught SQLException: " + e);
				} finally {
					System.out.println(">>> Load Office count: " + n);
					if (loadStmt != null) { loadStmt.close(); }
				}

			} catch (IgniteDataStreamerTimeoutException e) {
				System.out.println("IgniteDataStreamerTimeoutException: " + e);
			} catch (CacheException e) {
				System.out.println("CacheException: " + e);
			}

			/**
			 * ProductLine Table
			 */
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> SELECT FROM PRODUCTLINES Table, Stream to ProductLineCache...");
			loadQuery = "SELECT productLine, textDescription, htmlDescription, image " +
			            "  FROM productlines";

			// If we can get the cache, then lets try to get from JDBC and Stream to it.
			try (IgniteDataStreamer<String, ProductLine> streamer = ignite.dataStreamer("ProductLineCache")) {

				System.out.println(">>> try to create statement and execute query:"+ loadQuery +"...");
				int n = 0; // record counter
				try {
					loadStmt = conn.createStatement();
					ResultSet rs = loadStmt.executeQuery(loadQuery);

					while (rs.next()) {
						String k = null;
						ProductLine v = null;
						try {
							k = rs.getString("productLine");
							v = new ProductLine(
								rs.getString("textdescription"),
								rs.getString("htmldescription"),
								rs.getBytes("image")
							);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						streamer.addData(k, v);
						n++;
					}
				} catch (SQLException e ) {
					System.out.println("Caught SQLException: " + e);
				} finally {
					System.out.println(">>> Load ProductLine count: " + n);
					if (loadStmt != null) { loadStmt.close(); }
				}

			} catch (IgniteDataStreamerTimeoutException e) {
				System.out.println("IgniteDataStreamerTimeoutException: " + e);
			} catch (CacheException e) {
				System.out.println("CacheException: " + e);
			}

			/**
			 * Product Table
			 */
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> SELECT FROM PRODUCTS Table, Stream to ProductCache...");
			loadQuery = "SELECT productCode, productName, productLine, productScale, productVendor, productDescription, quantityInStock, buyPrice, MSRP " +
                        "  FROM products" ;

			// If we can get the cache, then lets try to get from JDBC and Stream to it.
			try (IgniteDataStreamer<String, Product> streamer = ignite.dataStreamer("ProductCache")) {

				System.out.println(">>> try to create statement and execute query:"+ loadQuery +"...");
				int n = 0; // record counter
				try {
					loadStmt = conn.createStatement();
					ResultSet rs = loadStmt.executeQuery(loadQuery);

					while (rs.next()) {
						String k = null;
						Product v = null;
						try {
							k = rs.getString("productCode");
							v = new Product(
								rs.getString("productname"),
								rs.getString("productline"),
								rs.getString("productscale"),
								rs.getString("productvendor"),
								rs.getString("productdescription"),
								rs.getShort("quantityinstock"),
								rs.getBigDecimal("buyprice"),
								rs.getBigDecimal("msrp")
							);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						streamer.addData(k, v);
						n++;
					}
				} catch (SQLException e ) {
					System.out.println("Caught SQLException: " + e);
				} finally {
					System.out.println(">>> Load Product count: " + n);
					if (loadStmt != null) { loadStmt.close(); }
				}

			} catch (IgniteDataStreamerTimeoutException e) {
				System.out.println("IgniteDataStreamerTimeoutException: " + e);
			} catch (CacheException e) {
				System.out.println("CacheException: " + e);
			}

			/**
			 * Employee Table
			 */
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> SELECT FROM EMPLOYEES Table, Stream to EmployeeCache...");
			loadQuery = "SELECT employeeNumber, lastName, firstName, extension, email, officeCode, reportsTo, jobTitle" +
                        "  FROM employees" ;

			// If we can get the cache, then lets try to get from JDBC and Stream to it.
			try (IgniteDataStreamer<Integer, Employee> streamer = ignite.dataStreamer("EmployeeCache")) {

				System.out.println(">>> try to create statement and execute query:"+ loadQuery +"...");
				int n = 0; // record counter
				try {
					loadStmt = conn.createStatement();
					ResultSet rs = loadStmt.executeQuery(loadQuery);

					while (rs.next()) {
						Integer k = null;
						Employee v = null;
						try {
							k = rs.getInt("employeeNumber");
							v = new Employee(
								rs.getString("lastName"),
								rs.getString("firstName"),
								rs.getString("extension"),
								rs.getString("email"),
								rs.getString("officeCode"),
								rs.getInt("reportsTo"),
								rs.getString("jobTitle")
							);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						streamer.addData(k, v);
						n++;
					}
				} catch (SQLException e ) {
					System.out.println("Caught SQLException: " + e);
				} finally {
					System.out.println(">>> Load Employee count: " + n);
					if (loadStmt != null) { loadStmt.close(); }
				}

			} catch (IgniteDataStreamerTimeoutException e) {
				System.out.println("IgniteDataStreamerTimeoutException: " + e);
			} catch (CacheException e) {
				System.out.println("CacheException: " + e);
			}

			/**
			 * Customer Table
			 */
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> SELECT FROM CUSTOMERS TABLE Stream to CustomerCache...");
			loadQuery = "SELECT customerNumber, " +
						"       customerName, " +
						"       contactLastName, " +
						"       contactFirstName, " +
						"       phone, " +
						"       addressLine1, " + 
						"       addressLine2, " +
						"       city, " +
						"       state, " +
						"       postalCode, " +
						"       country, " +
						"       creditLimit," +
						"       salesRepEmployeeNumber " +
					    "  FROM customers";

			// If we can get the cache, then lets try to get from JDBC and Stream to it.
			try (IgniteDataStreamer<Integer, Customer> streamer = ignite.dataStreamer("CustomerCache")) {

				System.out.println(">>> try to create statement and execute query:"+ loadQuery +"...");
				int n = 0; // record counter
				try {
					loadStmt = conn.createStatement();
					ResultSet rs = loadStmt.executeQuery(loadQuery);

					while (rs.next()) {
						Integer k = null;
						Customer v = null;
						try {
							k = ParseTypes.parseInteger(rs.getString("customerNumber"));
							v = new Customer(
								rs.getString("customerName"),
								rs.getString("contactLastName"),
								rs.getString("contactFirstName"),
								rs.getString("phone"),
								rs.getString("addressLine1"),
								rs.getString("addressLine2"),
								rs.getString("city"),
								rs.getString("state"),
								rs.getString("postalCode"),
								rs.getString("country"),
								rs.getInt("salesRepEmployeeNumber"),
								rs.getBigDecimal("creditLimit")
							);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						streamer.addData(k, v);
						n++;
					}
				} catch (SQLException e ) {
					System.out.println("Caught SQLException: " + e);
				} finally {
					System.out.println(">>> Load Customer count: " + n);
					if (loadStmt != null) { loadStmt.close(); }
				}

			} catch (IgniteDataStreamerTimeoutException e) {
				System.out.println("IgniteDataStreamerTimeoutException: " + e);
			} catch (CacheException e) {
				System.out.println("CacheException: " + e);
			}


			/**
			 * Order Table
			 */
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> SELECT FROM ORDER Table, Stream to OrderCache...");
			loadQuery = "SELECT orderNumber, orderDate, requiredDate, shippedDate, status, comments, customerNumber" +
			            "  FROM orders";

			// If we can get the cache, then lets try to get from JDBC and Stream to it.
			try (IgniteDataStreamer<Integer, Order> streamer = ignite.dataStreamer("OrderCache")) {

				System.out.println(">>> try to create statement and execute query:"+ loadQuery +"...");
				int n = 0; // record counter
				try {
					loadStmt = conn.createStatement();
					ResultSet rs = loadStmt.executeQuery(loadQuery);

					while (rs.next()) {
						Integer k = null;
						Order v = null;
						try {
							k = rs.getInt("orderNumber");
							v = new Order(
								rs.getDate("orderDate"),
								rs.getDate("requiredDate"),
								rs.getDate("shippedDate"),
								rs.getString("status"),
								rs.getString("comments"),
								rs.getInt("customerNumber")
							);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						streamer.addData(k, v);
						n++;
					}
				} catch (SQLException e ) {
					System.out.println("Caught SQLException: " + e);
				} finally {
					System.out.println(">>> Load Order count: " + n);
					if (loadStmt != null) { loadStmt.close(); }
				}

			} catch (IgniteDataStreamerTimeoutException e) {
				System.out.println("IgniteDataStreamerTimeoutException: " + e);
			} catch (CacheException e) {
				System.out.println("CacheException: " + e);
			}

			 /** Order Detail Table
			 * 
			 */
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> SELECT FROM ORDERDETAIL Table, Stream to OrderCache...");
			loadQuery = "SELECT orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber " +
			            "  FROM orderdetails; ";

			// If we can get the cache, then lets try to get from JDBC and Stream to it.
			try (IgniteDataStreamer<OrderDetailKey, OrderDetail> streamer = ignite.dataStreamer("OrderDetailCache")) {

				System.out.println(">>> try to create statement and execute query:"+ loadQuery +"...");
				int n = 0; // record counter
				try {
					loadStmt = conn.createStatement();
					ResultSet rs = loadStmt.executeQuery(loadQuery);

					while (rs.next()) {
						OrderDetailKey k = null;
						OrderDetail v = null;
						try {
							k = new OrderDetailKey(rs.getInt("orderNumber"), rs.getString("productCode"));
							v = new OrderDetail(
								rs.getInt("quantityOrdered"),
								rs.getBigDecimal("priceEach"),
								rs.getShort("orderLineNumber")
							);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						streamer.addData(k, v);
						n++;
					}
				} catch (SQLException e ) {
					System.out.println("Caught SQLException: " + e);
				} finally {
					System.out.println(">>> Load OrderDetail count: " + n);
					if (loadStmt != null) { loadStmt.close(); }
				}

			} catch (IgniteDataStreamerTimeoutException e) {
				System.out.println("IgniteDataStreamerTimeoutException: " + e);
			} catch (CacheException e) {
				System.out.println("CacheException: " + e);
			}

			 /** 
			 * Payment Table
			 */
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> SELECT FROM PAYMENTS Table, Stream to PaymentCache...");
			loadQuery = "SELECT customerNumber, checkNumber, paymentDate, amount " +
			            "  FROM payments; ";

			// If we can get the cache, then lets try to get from JDBC and Stream to it.
			try (IgniteDataStreamer<PaymentKey, Payment> streamer = ignite.dataStreamer("PaymentCache")) {

				System.out.println(">>> try to create statement and execute query:"+ loadQuery +"...");
				int n = 0; // record counter
				try {
					loadStmt = conn.createStatement();
					ResultSet rs = loadStmt.executeQuery(loadQuery);

					while (rs.next()) {
						PaymentKey k = null;
						Payment v = null;
						try {
							k = new PaymentKey(rs.getInt("customerNumber"), rs.getString("checkNumber"));
							v = new Payment(
								rs.getDate("paymentDate"),
								rs.getBigDecimal("amount")
							);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						streamer.addData(k, v);
						n++;
					}
				} catch (SQLException e ) {
					System.out.println("Caught SQLException: " + e);
				} finally {
					System.out.println(">>> Load Payment count: " + n);
					if (loadStmt != null) { loadStmt.close(); }
				}

			} catch (IgniteDataStreamerTimeoutException e) {
				System.out.println("IgniteDataStreamerTimeoutException: " + e);
			} catch (CacheException e) {
				System.out.println("CacheException: " + e);
			}

		System.out.println(">>> Stream Loading caches - COMPLETE!!!");

	} catch (IgniteException e) {
		System.out.println("IgniteException: " + e);
		System.out.println(">>> Stream Loading: abnormal exit!!!");
	} catch (SQLException e) {
		System.out.println("SQLException: " + e);
		System.out.println(">>> Stream Loading: abnormal exit!!!");
	}
	System.out.println(">>> Stream Loading caches exit.");
}


public Connection getConnection() throws SQLException {

	Connection conn = null;
	//try (InputStream input = new FileInputStream("C:\\code\\GitHub\\demos\\sales\\src\\main\\resources\\sales.properties")) {
	try (InputStream input = LoadCachesFromJdbc.class.getResourceAsStream("/sales.properties")) {

		// load a properties file
		Properties prop = new Properties();
		prop.load(input);

		Properties connectionProps = new Properties();
		connectionProps.put("user", prop.getProperty("dsMySQL_Classicmodels.jdbc.username"));
		connectionProps.put("password", prop.getProperty("dsMySQL_Classicmodels.jdbc.password"));
		conn = DriverManager.getConnection(prop.getProperty("dsMySQL_Classicmodels.jdbc.url"), connectionProps);
		System.out.println("Connected to: " + prop.getProperty("dsMySQL_Classicmodels.jdbc.url") + "...");
	} catch (IOException ex) {
		ex.printStackTrace();
	}
	return conn;
}
}
