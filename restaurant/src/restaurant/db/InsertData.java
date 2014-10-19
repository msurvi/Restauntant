package restaurant.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

	@Path("/insertmenumanager")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String insertMenuDB(@FormDataParam("menu") String menu,
			@FormDataParam("emailAddr") String emailAddr,
			@FormDataParam("password") String password) throws Exception {
		JSONObject nMenu = new JSONObject(menu);

		String returnString = null;
		int returnCategoryID = 0, returnInt;
		PreparedStatement query = null;
		Connection conn = null;
		ResultSet rs;
		try {
			UserAuthenticate ua = new UserAuthenticate();
			returnString = ua.checkUser(emailAddr, password);
			if (!returnString.equals("1")) {
				return returnString;
			}

			returnString = null;
			conn = DriverManager.getConnection(DBHelper.dbHostName,
					DBHelper.userName, DBHelper.passWord);
			query = conn.prepareStatement("select * from Category where CategoryName = '"
							+ nMenu.getJSONObject("insertmenu").getString("Category") + "';");
			rs = query.executeQuery();
			if (rs.next()) {
				returnCategoryID = rs.getInt(1);
			}

			if (returnCategoryID == 0) {
				query = conn.prepareStatement("insert into Category(CategoryName) values(?)");
				query.setString(1,nMenu.getJSONObject("insertmenu").getString("Category"));
				returnInt = query.executeUpdate();
				if (returnInt == 0) {
					returnString = JsonBuilderHelper.jsonBuilder("status","failed");
					return returnString;
				}
				query = conn.prepareStatement("select * from Category where CategoryName = '"
						+ nMenu.getJSONObject("insertmenu").getString("Category") + "';");
				rs = query.executeQuery();
				if (rs.next()) {
					returnCategoryID = rs.getInt(1);
				}
			}

			query = conn.prepareStatement("insert into menu(Mname, categoryID, Price, Active_Flag) values(?, ?, ?, '0')");
			query.setString(1,nMenu.getJSONObject("insertmenu").getString("Mname"));
			query.setInt(2,returnCategoryID);
			query.setString(3,nMenu.getJSONObject("insertmenu").getString("Price"));

			returnInt = query.executeUpdate();
			if (returnInt == 1) {
				returnString = JsonBuilderHelper.jsonBuilder("status","success");
			} else {
				returnString = JsonBuilderHelper.jsonBuilder("status", "failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
		return returnString;
	}
	
	
	@Path("/updatemenumanager")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String UpdateMenuDB(@FormDataParam("menu") String menu,@FormDataParam("emailAddr") String emailAddr,
			@FormDataParam("password") String password) throws Exception {
		String returnString = null;
		PreparedStatement query = null;
		Connection conn = null;
		try 
		{
			UserAuthenticate ua = new UserAuthenticate();
			returnString = ua.checkUser(emailAddr, password);
			if (!returnString.equals("1")) 
			{
				return returnString;
			}
			conn = DriverManager.getConnection(DBHelper.dbHostName,
					DBHelper.userName, DBHelper.passWord);
			query = conn.prepareStatement("update menu set Active_Flag = '1' where Mname = ?");
			query.setString(1,menu);
			if (query.executeUpdate() == 1) {
				returnString = JsonBuilderHelper.jsonBuilder("status","success");
			} else {
				returnString = JsonBuilderHelper.jsonBuilder("status", "failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
		return returnString;
	}
}
