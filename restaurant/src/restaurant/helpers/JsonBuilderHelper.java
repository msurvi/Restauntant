package restaurant.helpers;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class JsonBuilderHelper {
	
	public static String jsonBuilder(String key, String value)
	{
		try
		{
			JSONObject obj = new JSONObject();
			JSONObject obj1 = new JSONObject();
			JSONArray json = new JSONArray();
			String jsonObj;
			obj.put("key",key);
			obj.put("value",value);
			json.put(obj);
			obj1.put("ServerResponse",json);
			jsonObj = obj1.toString();
			return jsonObj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
