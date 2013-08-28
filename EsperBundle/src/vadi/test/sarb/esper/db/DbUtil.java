package vadi.test.sarb.esper.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DbUtil {
	java.util.logging.Logger log = java.util.logging.Logger.getLogger("global");
	  Connection conn = null;
	  String url,dbName,driver,userName,password;
	    
	  
	 public DbUtil()  {
		super();
		
		  //url = "jdbc:mysql://localhost:3306/";
		  url="jdbc:h2:/c://Users/Meku-laptop/RnD/ATS";
		  String dbName = "options";
		//  String driver = "com.mysql.jdbc.Driver";
		 String driver = "org.h2.Driver";
		//  String userName = "root"; 
		 // String password = "";
		  try {
			Class.forName(driver).newInstance();
			conn =  DriverManager.getConnection(url);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  log.info("Connected to the database");
		  
		
	 }

	 public int update(String sql)
	 {
		  return 0;
		 
	 }
	 
	public ArrayList<ArrayList> execute(String sql)
	{
			ArrayList<ArrayList> result = null;
		
		Statement stmt;
		try {
		//	conn =  DriverManager.getConnection(url);
			stmt = conn.createStatement();
			
		boolean bool  = stmt.execute(sql);
		if ( bool )
		{
			ResultSet rs = stmt.getResultSet();
			ResultSetMetaData dat = rs.getMetaData();
			result = new ArrayList<ArrayList>();
			
			int col = dat.getColumnCount();
			int rowC = 0;
			ArrayList hdr = new ArrayList(col);
			for (int i= 1;i<=col;i++){
				//log.info("Col size "+col+" "+i);
				hdr.add(i-1, dat.getColumnLabel(i));
			}
			result.add(0,hdr);
			rowC ++;
			
			while (rs.next())
			{
				ArrayList row = new ArrayList();
				for(int i=1;i<=col;i++)
					row.add(i-1,rs.getString(i));
				result.add(rowC,row);
				rowC++;
			}
		}
		stmt.close();
	//	conn.close();
		
		return result;
	}
	 catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
		 }
	 catch( Throwable e)
	 {
		 log.info("Db issue ");
		 e.printStackTrace();
		 return new ArrayList<ArrayList>();
	 }
	 
	 return result;
	}
}

