package restaurant.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import restaurant.helpers.*;

import com.sun.jersey.multipart.FormDataParam;

@Path("/retrieve")
public class ExtractData {

	@Path("/getmenu")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String returnDatabaseStatus(@FormDataParam("emailAddr") String userName,
			@FormDataParam("password") String password) throws Exception {

		PreparedStatement query = null;
		String returnString = null;
		Connection conn = null;
		ResultSet rs;
		JSONObject obj1 = new JSONObject();
		JSONArray json = new JSONArray();

		try {
			UserAuthenticate ua =  new UserAuthenticate();
			returnString = ua.checkUser(userName, password);
			//System.out.println(returnString);
			if(!returnString.equals("1"))
			{
				return returnString;
			}
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DBHelper.dbHostName,
					DBHelper.userName, DBHelper.passWord);

			returnString = null;
			query = conn.prepareStatement("select * from menu");
			rs = query.executeQuery();

			ToJson converter = new ToJson();
			json = converter.toJSONArray(rs);
			query.close();
			obj1.put("ServerResponse", json);
			returnString = obj1.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
		return returnString;
	}
}