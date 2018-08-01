package DB;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SqlDB implements IDB {

	private static Connection connection;
	private static final String DB_URL = "jdbc:mysql://localhost/WAR";
	private static ResultSet rs;

	static {

		try {
			Driver driver = new com.mysql.jdbc.Driver();
			DriverManager.registerDriver(driver);

			String url = "jdbc:mysql://localhost/";
			connection = DriverManager.getConnection(url, "root", "");
			
			if ( !checkDBExists(DB_NAME, connection)) {
				createDataBase(DB_NAME);
				connection = DriverManager.getConnection(DB_URL, "root", "");
				createTables();
			}
			else
				connection = DriverManager.getConnection(DB_URL, "root", "");

		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.getMessage());
				e = e.getNextException();
			}
		}
	}

	public synchronized static void createTables() {
		try {
			Statement stmt = connection.createStatement();

			String createLaunchers = "CREATE TABLE missile_launchers " + "(id VARCHAR(255) not NULL, "
					+ "is_hidden BOOLEAN not NULL, " + "is_destructed BOOLEAN not NULL, "
					+ "launched_missiles INT not NULL," + "missile_hits INT not NULL," + "PRIMARY KEY ( id ))";
			stmt.executeUpdate(createLaunchers);

			stmt = connection.createStatement();
			String createMissileDestructors = "CREATE TABLE missile_destructors " + "(id VARCHAR(255) not NULL, "
					+ "destructed_missiles INT not NULL, " + "PRIMARY KEY ( id ))";
			stmt.executeUpdate(createMissileDestructors);
			
			String createLauncherDestructors = "CREATE TABLE launcher_destructors " + "(id VARCHAR(255) not NULL, "
					+ "type VARCHAR(255) not NULL, " + "destructed_launchers INT not NULL, " + "PRIMARY KEY ( id ))";
			stmt.executeUpdate(createLauncherDestructors);
		
		} catch (SQLException e) {e.printStackTrace();}

	}

	public synchronized static void createDataBase(String dbName) {
		try {
			Statement stmt = connection.createStatement();
			String sql = "CREATE DATABASE " + dbName;
			stmt.executeUpdate(sql);

		} catch (SQLException e) {e.printStackTrace();}

	}

	public synchronized static boolean checkDBExists(String dbName, Connection conn) throws SQLException {
		ResultSet resultSet = conn.getMetaData().getCatalogs();
		while (resultSet.next()) {
			String databaseName = resultSet.getString(1);
			if (databaseName.toLowerCase().equals(dbName.toLowerCase()))
				return true;
		}
		resultSet.close();
		return false;
	}

	// add
	public synchronized void addMissileLuauncher(String id, boolean isHidden) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM " + MISSILE_LAUNCHERS + " where id=?");
			statement.setString(1, id);
			ResultSet r = statement.executeQuery();
			if (r.next()) {
				statement.close();
				return;
			}
			statement = connection.prepareStatement("INSERT INTO " + MISSILE_LAUNCHERS
					+ " (id, is_hidden, is_destructed, launched_missiles, missile_hits) VALUES (?, ? , ?, ?, ?)");

			statement.setString(1, id);
			statement.setBoolean(2, isHidden);
			statement.setBoolean(3, false);
			statement.setInt(4, 0);
			statement.setInt(5, 0);

			statement.executeUpdate();
			statement.close();

		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.getMessage());
				e = e.getNextException();
			}
		}
	}

	public synchronized void addMissileDestructor(String id) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM " + MISSILE_DESTRUCTORS + " where id=?");
			statement.setString(1, id);
			ResultSet r = statement.executeQuery();
			if (r.next()) {
				statement.close();
				return;
			}

			statement = connection.prepareStatement(
					"INSERT INTO " + MISSILE_DESTRUCTORS + " (id, destructed_missiles) VALUES (?, ?)");
			statement.setString(1, id);
			statement.setInt(2, 0);

			statement.executeUpdate();
			statement.close();

		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.getMessage());
				e = e.getNextException();
			}
		}
	}

	public synchronized void addLauncherDestructor(String id, String type) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM " + LAUNCHER_DESTRUCTORS + " where id=?");
			statement.setString(1, id);
			ResultSet r = statement.executeQuery();
			if (r.next()) {
				statement.close();
				return;
			}

			statement = connection.prepareStatement(
					"INSERT INTO " + LAUNCHER_DESTRUCTORS + " (id, type, destructed_launchers) VALUES (?, ?, ?)");
			statement.setString(1, id);
			statement.setString(2, type);
			statement.setInt(3, 0);

			statement.executeUpdate();
			statement.close();

		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.getMessage());
				e = e.getNextException();
			}
		}
	}

	// modify
	public synchronized void addMissileLaunch(String id) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM " + MISSILE_LAUNCHERS + " where id=?");
			statement.setString(1, id);
			ResultSet r = statement.executeQuery();
			r.next();
			int launchedMissiles = r.getInt("launched_missiles") + 1;
			statement.close();

			statement = connection
					.prepareStatement("UPDATE " + MISSILE_LAUNCHERS + " SET launched_missiles=? WHERE id=?");
			statement.setInt(1, launchedMissiles);
			statement.setString(2, id);

			statement.executeUpdate();
			statement.close();
		}

		catch (SQLException e) {

			while (e != null) {
				System.out.println(e.getMessage());
				e = e.getNextException();
			}
		}
	}

	public synchronized void addMissileHit(String id) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM " + MISSILE_LAUNCHERS + " where id=?");
			statement.setString(1, id);
			ResultSet r = statement.executeQuery();
			r.next();
			int missileHits = r.getInt("missile_hits") + 1;
			statement.close();

			statement = connection
					.prepareStatement("UPDATE " + MISSILE_LAUNCHERS + " SET missile_hits = ? WHERE id = ?");
			statement.setInt(1, missileHits);
			statement.setString(2, id);

			statement.executeUpdate();
			statement.close();

		}

		catch (SQLException e) {
			while (e != null) {
				System.out.println(e.getMessage());
				e = e.getNextException();
			}
		}
	}

	public synchronized void addMissileDestruct(String id) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM " + MISSILE_DESTRUCTORS + " where id=?");
			statement.setString(1, id);
			ResultSet r = statement.executeQuery();
			r.next();
			int missileDestructs = r.getInt("destructed_missiles") + 1;
			statement.close();

			statement = connection
					.prepareStatement("UPDATE " + MISSILE_DESTRUCTORS + " SET destructed_missiles=? WHERE id=?");
			statement.setInt(1, missileDestructs);
			statement.setString(2, id);
			statement.executeUpdate();
			statement.close();
		}

		catch (SQLException e) {
			while (e != null) {
				System.out.println(e.getMessage());
				e = e.getNextException();
			}
		}
	}

	public synchronized void addLauncherDestruct(String destructorID, String launcherID) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("SELECT * FROM " + LAUNCHER_DESTRUCTORS + " where id=?");
			statement.setString(1, destructorID);
			ResultSet r = statement.executeQuery();
			r.next();
			int launcherDestructs = r.getInt("destructed_launchers") + 1;
			statement.close();

			statement = connection
					.prepareStatement("UPDATE " + LAUNCHER_DESTRUCTORS + " SET destructed_launchers=? WHERE id=?");
			statement.setInt(1, launcherDestructs);
			statement.setString(2, destructorID);
			statement.executeUpdate();

			statement = connection.prepareStatement("UPDATE " + MISSILE_LAUNCHERS + " SET is_destructed=? WHERE id=?");
			statement.setBoolean(1, true);
			statement.setString(2, launcherID);

			statement.executeUpdate();
			statement.close();
		}

		catch (SQLException e) {
			while (e != null) {
				System.out.println(e.getMessage());
				e = e.getNextException();
			}
		}
	}

	public void closeDB() {
		try {
			if (connection != null) {
				connection.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			System.out.println("Could not close the current connection.");
			e.printStackTrace();
		}
	}

	// public List<String> getAllTablesNames() {
	// List<String> allTables = new ArrayList<String>();
	// try {
	// DatabaseMetaData md = connection.getMetaData();
	// ResultSet rs = md.getTables(null, null, "%", null);
	//
	// while (rs.next()) {
	// allTables.add(rs.getString(3));
	// }
	// } catch (SQLException e) {
	// while (e != null) {
	// e.printStackTrace();
	// e = e.getNextException();
	// }
	// }
	// return allTables;
	// }

	// ??
	// public void getFromDB(String id, String from) {
	// try {
	// ResultSet rs = statement.executeQuery("SELECT * FROM " + from + " WHERE id="
	// + id);
	//
	// } catch (SQLException e) {
	// while (e != null) {
	// System.out.println(e.getMessage());
	// e = e.getNextException();
	// }
	// }
	// }

	// get all table data
	// public Vector<String[]> getQueryData(String tableName, Vector<String>
	// headers) {
	// Vector<String[]> rowsData = new Vector<String[]>();
	// try {
	// PreparedStatement statement = (PreparedStatement)
	// connection.prepareStatement("SELECT * FROM " + tableName);
	// rs = statement.executeQuery();
	// ResultSetMetaData meta = rs.getMetaData();
	// int numOfCols = meta.getColumnCount();
	//
	// // rebuild the headers array with the new column names
	// headers.clear();
	// for (int h = 1; h <= numOfCols; h++) {
	// headers.add(meta.getColumnName(h));
	// }
	//
	// while (rs.next()) {
	// String[] record = new String[numOfCols];
	// for (int i = 0; i < numOfCols; i++) {
	// record[i] = rs.getString(i + 1);
	// }
	// rowsData.addElement(record);
	// }
	// } catch (SQLException e) {
	// while (e != null) {
	// e.printStackTrace();
	// e = e.getNextException();
	// }
	// }
	// return rowsData;
	// }
}
