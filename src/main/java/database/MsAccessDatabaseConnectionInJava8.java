package main.java.database;

import main.java.filePaths.JobSheetFilePath;
import main.java.main.LogWindow;

import java.sql.*;


public class MsAccessDatabaseConnectionInJava8 {
	
	static JobSheetFilePath filePathObject = new JobSheetFilePath(false);

	public static void main(String[] args) {
		
		//String fileName = fileNameFromMsAccess(99999);
		//System.out.println(fileName);

	}

	public static Statement fileNameFromMsAccess() {
		String fileName = "";
		//variables
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		// Step 1. Loading or registering Oracle JDBC driver class
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("Problem in loading or registering MS Access JDBC driver");
			e.printStackTrace();
		}
		
		// Step 2. Opening database connection
		String msAccDB = filePathObject.mDatabaseLocation;
		String dbURL = "jdbc:ucanaccess://" + msAccDB;
		
			try {
				//Step 2A. Create and get connection using DriverManager class
				connection = DriverManager.getConnection(dbURL, "", "");
				
				//Step 2B. Create JDBC Statement;
				statement = connection.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			} 
//			finally {
//				try {
//					// Step 3. Closing database connection
//					if(connection != null) {
//						//cleanup resources, once after processing
//						resultSet.close();
//						statement.close();
//						
//						//and then finally close connection
//						connection.close();
//					}
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
			return statement;
	}

	public String sqlQueryToGetDataFromWorkSheet(int SheetRef, Statement statement, LogWindow logWindow) {
		// TODO Auto-generated method stub
		ResultSet resultSet = null;
		String fileName = "";
		
		//Step 2C. Executing SQL and retrieve data into ResultSet
		try {
			resultSet = statement.executeQuery("SELECT SheetDate, JobID, EmployeeRef, CustomerRef "
					+ "FROM WorkSheets WHERE SheetRef = '"+ SheetRef +"'");
		
			//processing returned data and printing into console
			while(resultSet.next()) {
				loggingAllActivities(resultSet, logWindow);
				fileName = resultSet.getDate("SheetDate") + "_" + resultSet.getInt("JobID")
								+ "_" +  resultSet.getInt("EmployeeRef") + "_" + resultSet.getString("CustomerRef");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
//		finally {
//			try {
//				// Step 3. Closing database connection
//					//cleanup resources, once after processing
//					resultSet.close();
//					statement.close();
//					
//					//and then finally close connection
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		return fileName;
	}

	private void loggingAllActivities(ResultSet resultSet, LogWindow logWindow) throws SQLException {
		System.out.println("SheetDate -- " + resultSet.getDate("SheetDate"));
		System.out.println("JobID -- " + resultSet.getInt("JobID"));
		System.out.println("EmployeeRef -- " + resultSet.getInt("EmployeeRef"));
		System.out.println("CustomerRef -- " + resultSet.getString("CustomerRef"));

		logWindow.showInfoInLog("SheetDate -- " + resultSet.getDate("SheetDate"));
		logWindow.showInfoInLog("JobID -- " + resultSet.getInt("JobID"));
		logWindow.showInfoInLog("EmployeeRef -- " + resultSet.getInt("EmployeeRef"));
		logWindow.showInfoInLog("CustomerRef -- " + resultSet.getString("CustomerRef"));
	}


}
