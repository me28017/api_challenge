package com.disney.studios.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.disney.studios.model.DogException;
import com.disney.studios.model.DogPicture;

public class DogDAO
{
	private static DataSource dataSource;

	public static void setDataSource(DataSource ds)
	{
		dataSource = ds;
	}

	public static void createDogBreedTable() throws SQLException
	{
		try (Connection con = dataSource.getConnection(); Statement stmt = con.createStatement())
		{
			String table = "CREATE TABLE DOG_PICS " + "(ID VARCHAR(128) PRIMARY KEY, " + "IMG_URL VARCHAR(512), "
					+ "BREED VARCHAR(128), " + "TIMES_LIKED INT" + ");";

			stmt.execute(table);
		}
	}

	public static void insertDogsToTable(List<String> dogs, String breed) throws SQLException
	{
		String insertTableSQL = "INSERT INTO DOG_PICS" + "(ID, IMG_URL, BREED, TIMES_LIKED) VALUES" + "(?,?,?,?)";

		try (Connection con = dataSource.getConnection();
				PreparedStatement pStmt = con.prepareStatement(insertTableSQL);)
		{
			for (String line : dogs)
			{
				line = line.trim();

				// make sure we have a valid entry
				if (line.length() > 0 && line.startsWith("http"))
				{
					int indexOflastSlash = line.lastIndexOf("/");
					int indexOfLastDot = line.lastIndexOf(".");

					String id = line.substring(indexOflastSlash + 1, indexOfLastDot);
					String url = line;

					// cleanse the breed
					breed = breed.trim().toLowerCase();

					// id alone is not unique for the data set
					String uniqueID = id + "_" + breed;
					uniqueID = uniqueID.toLowerCase();

					pStmt.setString(1, uniqueID);
					pStmt.setString(2, url);
					pStmt.setString(3, breed);
					pStmt.setInt(4, 0);

					// execute insert SQL statement
					pStmt.executeUpdate();
				}
			}
		}
	}

	public static List<DogPicture> getDogsGroupedByBreed() throws DogException
	{
		List<DogPicture> dogPics = new ArrayList<>();

		try (Connection con = dataSource.getConnection(); Statement stmt = con.createStatement())
		{
			ResultSet rs = stmt.executeQuery("select * from DOG_PICS order by breed");

			while (rs.next())
			{
				String id = rs.getString("ID");
				String url = rs.getString("IMG_URL");
				String breed = rs.getString("BREED");
				int timesLiked = rs.getInt("TIMES_LIKED");

				DogPicture dp = new DogPicture(id, url, breed, timesLiked);
				dogPics.add(dp);
			}
		}
		catch (SQLException e)
		{
			throw new DogException(e);
		}
		return dogPics;
	}

	public static List<DogPicture> getDogsForBreed(String selectBreed) throws DogException
	{
		String selectSQL = "select * from DOG_PICS where breed = ?";

		List<DogPicture> dogPics = new ArrayList<>();

		try (Connection con = dataSource.getConnection();
				Statement stmt = con.createStatement();
				PreparedStatement pStmt = con.prepareStatement(selectSQL);)
		{
			
			selectBreed = selectBreed.trim().toLowerCase();
			
			pStmt.setString(1, selectBreed);

			ResultSet rs = pStmt.executeQuery();

			while (rs.next())
			{
				String id = rs.getString("ID");
				String url = rs.getString("IMG_URL");
				String breed = rs.getString("BREED");
				int timesLiked = rs.getInt("TIMES_LIKED");

				DogPicture dp = new DogPicture(id, url, breed, timesLiked);
				dogPics.add(dp);
			}
		}
		catch (SQLException e)
		{
			throw new DogException(e);
		}
		return dogPics;
	}

	public static DogPicture getDogPicture(String selectID) throws DogException
	{
		String selectSQL = "select * from DOG_PICS where id = ?";

		DogPicture dp = null;

		try (Connection con = dataSource.getConnection();
				Statement stmt = con.createStatement();
				PreparedStatement pStmt = con.prepareStatement(selectSQL);)
		{
			// make sure we query in lower case
			selectID = selectID.trim().toLowerCase();
			
			pStmt.setString(1, selectID);

			ResultSet rs = pStmt.executeQuery();

			while (rs.next())
			{
				String id = rs.getString("ID");
				String url = rs.getString("IMG_URL");
				String breed = rs.getString("BREED");
				int timesLiked = rs.getInt("TIMES_LIKED");

				dp = new DogPicture(id, url, breed, timesLiked);
			}
		}
		catch (SQLException e)
		{
			throw new DogException(e);
		}
		return dp;
	}

	public static void createClientTrackingTable() throws SQLException
	{
		try (Connection con = dataSource.getConnection(); 
			 Statement stmt = con.createStatement())
		{
			String table = "CREATE TABLE CLIENT_TRACKING " + 
							"(CLIENT_ID VARCHAR(256), " + 
							"VOTED_FOR_DOG_ID VARCHAR(128),"
							+ "primary key (CLIENT_ID, VOTED_FOR_DOG_ID));"; 

			stmt.execute(table);
		}
	}

	public static void handleUpVote(String remoteAddr, String selectID) throws DogException
	{
		String selectSQL = "select * from CLIENT_TRACKING where CLIENT_ID = ? AND VOTED_FOR_DOG_ID = ?";

		// make sure this client has not voted yet
		try (Connection con = dataSource.getConnection();
				Statement stmt = con.createStatement();
				PreparedStatement pStmt = con.prepareStatement(selectSQL);)
		{
			selectID = selectID.trim().toLowerCase();

			pStmt.setString(1, remoteAddr);
			pStmt.setString(2, selectID);

			ResultSet rs = pStmt.executeQuery();
			
			if (rs != null && rs.next())
			{
				throw new DogException("You have already cast a vote for this dog");
			}
			else // they did not vote yet
			{
				// do this as one transaction
				con.setAutoCommit(false);
				
				// mark that they voted
				setVoted(con, remoteAddr, selectID);
				
				// increase the liked count by 1
				modifyLikeCount(con, true, selectID);

				// commit the transaction
				con.commit();
			}
		}
		catch (SQLException e)
		{
			throw new DogException(e);
		}
	}

	public static void handleDownVote(String remoteAddr, String selectID) throws DogException
	{
		String selectSQL = "select * from CLIENT_TRACKING where CLIENT_ID = ? AND VOTED_FOR_DOG_ID = ?";

		// make sure this client has not voted yet
		try (Connection con = dataSource.getConnection();
				Statement stmt = con.createStatement();
				PreparedStatement pStmt = con.prepareStatement(selectSQL);)
		{
			selectID = selectID.trim().toLowerCase();

			pStmt.setString(1, remoteAddr);
			pStmt.setString(2, selectID);

			ResultSet rs = pStmt.executeQuery();
			
			if (rs != null && rs.next())
			{
				throw new DogException("You have already cast a vote for this dog");
			}
			else // they did not vote yet
			{
				// do this as one transaction
				con.setAutoCommit(false);
				
				// mark that they voted
				setVoted(con, remoteAddr, selectID);

				// decrease the liked count by 1
				modifyLikeCount(con, false, selectID);

				// commit the transaction
				con.commit();
			}
		}
		catch (SQLException e)
		{
			throw new DogException(e);
		}
	}
	
	private static void modifyLikeCount(Connection con, boolean voteUp, String updateID) throws SQLException
	{
		String sql = "UPDATE DOG_PICS SET TIMES_LIKED = ";
		
		if (voteUp)
		{
			sql += "TIMES_LIKED + 1 ";
		}
		else
		{
			sql += "TIMES_LIKED - 1 ";
		}

		sql += " where id = ?";

		// lock for concurrent updates to same ID
		try (PreparedStatement pStmt = con.prepareStatement(sql, ResultSet.CONCUR_UPDATABLE))
		{
			pStmt.setString(1, updateID);
			System.out.println(updateID);
			
			// execute insert SQL statement
			System.out.println(pStmt.executeUpdate());
		}
	}

	private static void setVoted(Connection con, String clientaID, String votedForID) throws SQLException
	{
		String insertTableSQL = "INSERT INTO CLIENT_TRACKING" + "(CLIENT_ID, VOTED_FOR_DOG_ID) VALUES" + "(?,?)";
	
		try (PreparedStatement pStmt = con.prepareStatement(insertTableSQL))
		{
			pStmt.setString(1, clientaID);
			pStmt.setString(2, votedForID);

			// execute insert SQL statement
			pStmt.executeUpdate();
		}
	}
}
