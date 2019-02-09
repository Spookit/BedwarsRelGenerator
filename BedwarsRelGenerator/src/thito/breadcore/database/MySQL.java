package thito.breadcore.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL implements AutoCloseable {
	
	private final String h,d,u,p;
	private final int po;
	
	private Connection connection;
	

	public MySQL(String host,int port, String db, String user, String pw) {
		h = host;
		po = port;
		d = db;
		u = user;
		p = pw;
	}
	
    public final Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()){
                return connection;
            } else {
                final String clientHost = h;
                final int clientPort = po;
                final String clientDatabase = d;
                final String clientUsername = u;
                final String clientPassword = p;
                final String url = "jdbc:mysql://" + clientHost + ":" + clientPort + "/" + clientDatabase;
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(url, clientUsername, clientPassword);
                return connection;
            }
        } catch (Exception t) {
        	throw new RuntimeException(t);
        }
    }
    
    public final PreparedStatement createPreparedStatement(String query) {
    	try {
			return getConnection().prepareStatement(query);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    }
    
    public final void executeUpdate(PreparedStatement statement,Object... values) {
        try {
        	for (int i = 0;i<values.length;i++) statement.setObject(i+1, values[i]);
            statement.executeUpdate();
        } catch (SQLException exception) {
        	throw new RuntimeException(exception);
        }
    }
    
    public final ResultSet executeQuery(PreparedStatement statement,Object... values) {
        try {  
        	for (int i = 0;i<values.length;i++) statement.setObject(i+1, values[i]);
            final ResultSet set = statement.executeQuery();
            return set;
        } catch (SQLException exception) {
        	throw new RuntimeException();
        }
    }
    
    public void close() throws Exception {
    	connection.close();
    }
}
