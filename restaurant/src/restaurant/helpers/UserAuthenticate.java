package restaurant.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class UserAuthenticate {
	
	String returnString;
	
	public String checkUser(String userName, String password) throws Exception
	{
		Connection conn = null;
		PreparedStatement query;
		ResultSet rs;
		String passwordMD5 = null;
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			passwordMD5 = EncryptMD5.cryptWithMD5(password);
			conn = DriverManager.getConnection(DBHelper.dbHostName, DBHelper.userName, DBHelper.passWord);
			query = conn.prepareStatement("select * from user where username = '" + userName + "' and password = '" + passwordMD5 +"';");
			rs = query.executeQuery();
			returnString = JsonBuilderHelper.jsonBuilder("status", "authfailed");
			ResultSetMetaData rsmd = rs.getMetaData();
			if(!rs.next())
			{
				return returnString;
			}
			int flagNewUser = rs.getInt(rsmd.getColumnName(4));
			if(flagNewUser == 1)
			{
				return "1"; 
			}
			return "100";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return returnString;
		}
		finally
		{
			if(conn!=null)
				conn.close();
		}
	}
}
