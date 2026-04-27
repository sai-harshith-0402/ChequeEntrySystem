package com.iispl.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
	private static final String URL = "jdbc:postgresql://localhost:5432/Bank";
	private static final String USER = "postgres";
	private static final String PASS = "password";
	
	public static Connection getConnection() throws SQLException{
		try {
			Class.forName("org.postgresql.Driver");
		}catch(ClassNotFoundException e) {
			throw new SQLException("Driver missing from class path",e);
		}
		
		return DriverManager.getConnection(URL,USER,PASS);
	}
}
