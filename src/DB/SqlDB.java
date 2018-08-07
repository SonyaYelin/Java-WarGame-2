package DB;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlDB implements IDB {

	private static final String SQL_URL = "jdbc:mysql://localhost/";
	private static final String DB_URL = "jdbc:mysql://localhost/WAR";
	private static final String ROOT = "root";

	private static Connection 	connection;
	private static ResultSet 	rs;

	static {
		try {
			Driver driver = new com.mysql.jdbc.Driver();
			DriverManager.registerDriver(driver);

			String url = SQL_URL;
			connection = DriverManager.getConnection(url, ROOT, "");
			
			if ( !checkDBExists(DB_NAME, connection)) {
				createDataBase(DB_NAME);
				connection = DriverManager.getConnection(DB_URL, ROOT, "");
				createTables();
			}
			else
				connection = DriverManager.getConnection(DB_URL, ROOT, "");

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

			String createLaunchers = "CREATE TABLE " + MISSILE_LAUNCHERS + " ("+ ID + " VARCHAR(255) not NULL, "
					+ IS_HIDDEN + " BOOLEAN not NULL, " + IS_DESTRUCTED + " BOOLEAN not NULL, "
					+ LAUNCHED_MISSILES +" INT not NULL," + MISSILE_HITS + " INT not NULL," + "PRIMARY KEY ( " + ID + " ))";
			stmt.executeUpdate(createLaunchers);

			stmt = connection.createStatement();
			String createMissileDestructors = "CREATE TABLE " + MISSILE_DESTRUCTORS + " ("+ ID + " VARCHAR(255) not NULL, "
					+ DESTRUCTED_MISSILES + " INT not NULL, " + "PRIMARY KEY ( " + ID + " ))";
			stmt.executeUpdate(createMissileDestructors);
			
			String createLauncherDestructors = "CREATE TABLE " + LAUNCHER_DESTRUCTORS + " ("+ ID + " VARCHAR(255) not NULL, "
					+ TYPE + " VARCHAR(255) not NULL, " + DESTRUCTED_LAUNCHERS + " INT not NULL, " + "PRIMARY KEY ( " + ID + " ))";
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
			e.printStackTrace();
		}
	}
}
