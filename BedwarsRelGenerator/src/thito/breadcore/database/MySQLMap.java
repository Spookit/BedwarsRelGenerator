package thito.breadcore.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MySQLMap implements Map<String,String>,AutoCloseable {

	private final MySQL d;
	private final String t;
	private final PreparedStatement ALL;
	private final PreparedStatement CREATE;
	private final PreparedStatement DROP;
	private final PreparedStatement COUNT;
	private final PreparedStatement CONTAINS_KEY;
	private final PreparedStatement CONTAINS_VALUE;
	private final PreparedStatement GET;
	private final PreparedStatement PUT;
	private final PreparedStatement REMOVE;
	private final PreparedStatement CLEAR;
	
	public MySQLMap(MySQL db,String tableName) {
		d = db;
		t = tableName;
		ALL = d.createPreparedStatement("SELECT * FROM "+t);
		CREATE = d.createPreparedStatement("CREATE TABLE IF NOT EXISTS "+t+" (K VARCHAR(512), V TEXT, PRIMARY KEY (K));");
		DROP = d.createPreparedStatement("DROP TABLE IF EXISTS "+t);
		COUNT = d.createPreparedStatement("SELECT COUNT(*) FROM "+t);
		CONTAINS_KEY = d.createPreparedStatement("SELECT COUNT(*) FROM "+t+" WHERE K = ?;");
		CONTAINS_VALUE = d.createPreparedStatement("SELECT COUNT(*) FROM "+t+" WHERE V = ?;");
		GET = d.createPreparedStatement("SELECT * FROM "+t+" WHERE K = ?");
		PUT = d.createPreparedStatement("REPLACE INTO "+t+" (K, V) VALUES (?, ?);");
		REMOVE = d.createPreparedStatement("DELETE FROM "+t+" WHERE K = ?");
		CLEAR = d.createPreparedStatement("TRUNCATE TABLE "+t);
		createTable();
	}
	
	@Override
	public void close() throws Exception {
		ALL.close();
		CREATE.close();
		DROP.close();
		COUNT.close();
		CONTAINS_KEY.close();
		CONTAINS_VALUE.close();
		GET.close();
		PUT.close();
		REMOVE.close();
		CLEAR.close();
	}
	
	public void createTable() {
		d.executeUpdate(CREATE);
	}
	
	public void deleteTable() {
		d.executeUpdate(DROP);
	}
	
	@Override
	public int size() {
		ResultSet set = d.executeQuery(COUNT);
		try {
			set.next();
			return set.getInt(1);
		} catch (SQLException t) {
			throw new RuntimeException(t);
		} finally {
			try {
				set.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		try (ResultSet set = d.executeQuery(CONTAINS_KEY, key)) {
			set.next();
			boolean x = set.getInt(1) >= 1;
			return x;
		} catch (SQLException t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public boolean containsValue(Object value) {
		try (ResultSet set = d.executeQuery(CONTAINS_VALUE, value)) {
			set.next();
			boolean x = set.getInt(1) >= 1;
			return x;
		} catch (SQLException t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public String get(Object key) {
		try (ResultSet set = d.executeQuery(GET, key)){
			set.next();
			return set.getString("V");
		} catch (SQLException t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public String put(String key, String value) {
		d.executeUpdate(PUT, key,value);
		return value;
	}

	@Override
	public String remove(Object key) {
		String get = get(key);
		d.executeUpdate(REMOVE, key);
		return get;
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		String values = "";
		for (java.util.Map.Entry<? extends String, ? extends String> x : m.entrySet()) {
			values+=", ('"+x.getKey()+"', '"+x.getValue()+"')";
		}
		if (!values.isEmpty()) {
			values = values.substring(1);
			try {
				Statement st = d.getConnection().createStatement();
				st.executeUpdate("REPLACE INTO "+t+" (K,V) VALUES "+values);
				st.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void clear() {
		d.executeUpdate(CLEAR);
	}

	@Override
	public Set<String> keySet() {
		HashSet<String> o = new HashSet<>();
		try (ResultSet set = d.executeQuery(ALL)) {
			while (set.next()) {
				o.add(set.getString(1));
			}
		} catch (SQLException t) {
			throw new RuntimeException(t);
		} 
		return o;
	}

	@Override
	public Collection<String> values() {
		HashSet<String> o = new HashSet<>();
		try (ResultSet set = d.executeQuery(ALL)) {
			while (set.next()) {
				o.add(set.getString(2));
			}
		} catch (SQLException t) {
			throw new RuntimeException(t);
		} 
		return o;
	}
	
	public String toString() {
		String ts = entrySet().toString();
		return "{"+ts.substring(1,ts.length()-1)+"}";
	}

	@Override
	public Set<Map.Entry<String, String>> entrySet() {
		Set<Map.Entry<String, String>> set = new HashSet<>();
		try (ResultSet x = d.executeQuery(ALL)) {
			while (x.next()) {
				String key = x.getString(1);
				Entry<String,String> s = new Entry<String, String>() {
					String cache = x.getString(2);
					@Override
					public String setValue(String value) {
						put(key,value);
						cache = value;
						return cache;
					}
					
					@Override
					public String getValue() {
						return cache;
					}
					
					@Override
					public String getKey() {
						return key;
					}
					
					public String toString() {
						return getKey()+"="+getValue();
					}
				};
				
				set.add(s);
			}
		} catch (SQLException t) {
			throw new RuntimeException(t);
		}
		return set;
	}
	
	public static void main(String[]args) {
		MySQLMap map = new MySQLMap(new MySQL("db4free.net", 3306, "wkwkwk", "wkwkwkuser", "hidupinisimple"),"pretest");
		map.put("something", "once");
		map.put("something", "twice");
		map.put("another", "what");
		System.out.println(map);
		map.remove("another");
		System.out.println(map);
		System.out.println(map.get("something"));
	}


}
