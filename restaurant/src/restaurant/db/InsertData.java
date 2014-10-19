package restaurant.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONObject;

import restaurant.helpers.DBHelper;
import restaurant.helpers.JsonBuilderHelper;
import restaurant.helpers.UserAuthenticate;

import com.sun.jersey.multipart.FormDataParam;

@Path("/insert")
public class InsertData {
	
	@Path("/insertmenu")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String insertDataDB(
			@FormDataParam("menu") String menu,
			@FormDataParam("emailAddr") String emailAddr,
			@FormDataParam("password") String password) throws Exception
	{
		JSONObject nMenu = new JSONObject(menu);
		
		String returnString = null;
		PreparedStatement query = null;
		Connection conn = null;
		try
		{
			UserAuthenticate ua =  new UserAuthenticate();
			returnString = ua.checkUser(emailAddr, password);
			if(!returnString.equals("1"))
			{
				return returnString;
			}
			
			returnString = null;
			conn = DriverManager.getConnection(DBHelper.dbHostName, DBHelper.userName, DBHelper.passWord);
			query = conn.prepareStatement("insert into menu(name, category) values(?, ?)");
			query.setString(1, nMenu.getString("name"));
			query.setString(2, nMenu.getString("category"));
			
			int b = query.executeUpdate();
			if(b == 1)
			{
				returnString = JsonBuilderHelper.jsonBuilder("status", "success");
			}
			else
			{
				returnString = JsonBuilderHelper.jsonBuilder("status", "failed");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn != null) conn.close();
		}
		
		return returnString;
	}

}
