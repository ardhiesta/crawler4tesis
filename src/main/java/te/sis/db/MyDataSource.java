package te.sis.db;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class MyDataSource {
	private static PoolingDataSource<PoolableConnection> dataSource;
	
	public static PoolingDataSource<PoolableConnection> initDataSource() {
		System.out.println("Setting up data source.");

//		String connectURI = DBConstants.DB_URI;

		//
		// First we load the underlying JDBC driver.
		// You need this if you don't use the jdbc.drivers
		// system property.
		//
		System.out.println("Loading underlying JDBC driver.");
		try {
			Class.forName(DbConstants.DB_DRIVER);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("Done.");

		//
		// First, we'll create a ConnectionFactory that the
		// pool will use to create Connections.
		// We'll use the DriverManagerConnectionFactory,
		// using the connect string passed in the command line
		// arguments.
		//
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
				DbConstants.DB_OPENSHIFT_URI, DbConstants.DB_OPENSHIFT_USERNAME, DbConstants.DB_OPENSHIFT_PASSWORD);

		//
		// Next we'll create the PoolableConnectionFactory, which wraps
		// the "real" Connections created by the ConnectionFactory with
		// the classes that implement the pooling functionality.
		//
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
				connectionFactory, null);

		//
		// Now we'll need a ObjectPool that serves as the
		// actual pool of connections.
		//
		// We'll use a GenericObjectPool instance, although
		// any ObjectPool implementation will suffice.
		//
		ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(
				poolableConnectionFactory);

		// Set the factory's pool property to the owning pool
		poolableConnectionFactory.setPool(connectionPool);

		//
		// Finally, we create the PoolingDriver itself,
		// passing in the object pool we created.
		//
		dataSource = new PoolingDataSource<>(
				connectionPool);

		System.out.println("Done.");
		return dataSource;
	}
	
	 public static PoolingDataSource<PoolableConnection> getInstance() {
		 if (dataSource == null) {
	            dataSource = initDataSource();
	            return dataSource;
	        } else {
	            return dataSource;
	        }
//		 return dataSource;
	 }
}
