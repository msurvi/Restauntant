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
import restaurant.helpers.EncryptMD5;
import restaurant.helpers.JsonBuilderHelper;
import restaurant.helpers.RandomPasswdGen;
import restaurant.helpers.SendEmail;
import restaurant.helpers.UserAuthenticate;

import com.sun.jersey.multipart.FormDataParam;

@Path("/user")
public class SignUp {

	@Path("/signup")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String createAccount(@FormDataParam("emailAddr") String emailAcc, @FormDataParam("userinfo") String userData)
			throws Exception {
		Connection conn = null;
		PreparedStatement query;
		ResultSet rs;
		String passwordMD5 = null, ranPasswd = null;
		String returnString = null;
		try {
			RandomPasswdGen ranPass = new RandomPasswdGen();
			ranPasswd = ranPass.generateRandomString();
			passwordMD5 = EncryptMD5.cryptWithMD5(ranPasswd);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DBHelper.dbHostName,
					DBHelper.userName, DBHelper.passWord);
			query = conn
					.prepareStatement("select * from users where Email = '"
							+ emailAcc + "';");
			rs = query.executeQuery();
			if (rs.next()) {
				returnString = JsonBuilderHelper.jsonBuilder("status",
						"UserExits");
				return returnString;
			}
			JSONObject obj = new JSONObject(userData);
			String firstName = obj.getJSONObject("userdata").getString("First_name");
			String lastName = obj.getJSONObject("userdata").getString("Last_name");
			String address = obj.getJSONObject("userdata").getString("Address");
			String city = obj.getJSONObject("userdata").getString("City");
			String state = obj.getJSONObject("userdata").getString("State");
			String phone = obj.getJSONObject("userdata").getString("Phone");
			
			query = conn
					.prepareStatement("insert into users(Email, password, First_name, Last_name, Address, City, "
							+ "State, Phone, Flag_User, Flag_manager) values('"
							+ emailAcc + "','" + passwordMD5 + "','" + firstName + "','" + lastName + "','" + address 
							+ "','" + city + "','" + state + "'," + phone + ",'0','0');");
			if (query.executeUpdate() == 1) {
				SendEmail.sendMail("smkr1108@gmail.com", "ABCabc12$", emailAcc,
						"New Password", "Your new password is " + ranPasswd);
				returnString = JsonBuilderHelper.jsonBuilder("status",
						"UserCreated");
			}
			return returnString;
		} catch (Exception e) {
			e.printStackTrace();
			return returnString;
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	@Path("/forgotpasswd")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String resetPassword(@FormDataParam("emailAddr") String emailAcc)
			throws Exception {
		Connection conn = null;
		PreparedStatement query;
		ResultSet rs;
		String passwordMD5 = null, ranPasswd = null;
		String returnString = null;
		try {
			RandomPasswdGen ranPass = new RandomPasswdGen();
			ranPasswd = ranPass.generateRandomString();
			passwordMD5 = EncryptMD5.cryptWithMD5(ranPasswd);
			// System.out.println(ranPasswd);
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DBHelper.dbHostName,
					DBHelper.userName, DBHelper.passWord);
			query = conn
					.prepareStatement("select * from users where Email = '"
							+ emailAcc + "';");
			rs = query.executeQuery();
			if (!rs.next()) {
				returnString = JsonBuilderHelper.jsonBuilder("status",
						"UserNotExits");
			}
			query = conn.prepareStatement("update users set password = '"
					+ passwordMD5 + "', Flag_User = 0 where Email = '"
					+ emailAcc + "';");
			if (query.executeUpdate() == 1) {
				SendEmail.sendMail("smkr1108@gmail.com", "ABCabc12$", emailAcc,
						"New Password", "Your new password is " + ranPasswd);
				returnString = JsonBuilderHelper.jsonBuilder("status",
						"PasswordCreated");
			}
			return returnString;
		} catch (Exception e) {
			e.printStackTrace();
			return returnString;
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	@Path("/resetpassword")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String changeDefaultPassword(
			@FormDataParam("emailAddr") String emailAdd,
			@FormDataParam("password") String passWord) throws Exception {
		Connection conn = null;
		PreparedStatement query;
		String returnString = JsonBuilderHelper.jsonBuilder("status",
				"authfailed");
		String md5Password = EncryptMD5.cryptWithMD5(passWord);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DBHelper.dbHostName,
					DBHelper.userName, DBHelper.passWord);
			query = conn.prepareStatement("update users set password = '"
					+ md5Password + "', " + "Flag_User = 1 where Email = '"
					+ emailAdd + "';");
			if (query.executeUpdate() == 1) {
				SendEmail.sendMail("smkr1108@gmail.com", "ABCabc12$", emailAdd,
						"Password changed", "Password updated");
				returnString = JsonBuilderHelper.jsonBuilder("status",
						"success");
			}
			return returnString;
		} catch (Exception e) {
			e.printStackTrace();
			return returnString;
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	@Path("/userlogin")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String login(@FormDataParam("emailAddr") String userName,
			@FormDataParam("password") String password) {
		String returnString = null;
		UserAuthenticate ua = new UserAuthenticate();
		try {
			String userVerified = ua.checkUser(userName, password);
			if (userVerified.equals("1")) {
				returnString = JsonBuilderHelper
						.jsonBuilder(userName, password);
				returnString = JsonBuilderHelper.jsonBuilder("status",
						returnString);
			} else if (userVerified.equals("100")) {
				returnString = JsonBuilderHelper.jsonBuilder("resetPassword",
						password);
				returnString = JsonBuilderHelper.jsonBuilder("status",
						returnString);
			} else {
				returnString = JsonBuilderHelper.jsonBuilder("status",
						"authfailed");
			}
			return returnString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnString;
	}
}
