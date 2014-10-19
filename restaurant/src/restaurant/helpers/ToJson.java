package restaurant.helpers;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.sql.ResultSet;

public class ToJson {
	public JSONArray toJSONArray(ResultSet rs) throws Exception {
		JSONArray json = new JSONArray();
		String column_name = null;
		try {
			java.sql.ResultSetMetaData rsed = rs.getMetaData();
			while (rs.next()) {
				int numColumns = rsed.getColumnCount();
				// System.out.print(numColumns);
				JSONObject obj = new JSONObject();

				for (int i = 1; i <= numColumns; i++) {
					column_name = rsed.getColumnName(i);

					if (rsed.getColumnType(i) == java.sql.Types.ARRAY) {
						obj.put(column_name, rs.getArray(column_name));
						// System.out.print("ToJson: ARRAY");
					} else if (rsed.getColumnType(i) == java.sql.Types.BIGINT) {
						obj.put(column_name, rs.getInt(column_name));
						// System.out.print("ToJson: BIGINT");
					} else if (rsed.getColumnType(i) == java.sql.Types.BOOLEAN) {
						obj.put(column_name, rs.getBoolean(column_name));
						// System.out.print("ToJson: BOOLEAN");
					} else if (rsed.getColumnType(i) == java.sql.Types.BLOB) {
						obj.put(column_name, rs.getBlob(column_name));
						// System.out.print("ToJson: BLOB");
					} else if (rsed.getColumnType(i) == java.sql.Types.DOUBLE) {
						obj.put(column_name, rs.getDouble(column_name));
						// System.out.print("ToJson: Double");
					} else if (rsed.getColumnType(i) == java.sql.Types.FLOAT) {
						obj.put(column_name, rs.getFloat(column_name));
						// System.out.print("ToJson: Float");
					} else if (rsed.getColumnType(i) == java.sql.Types.INTEGER) {
						obj.put(column_name, rs.getInt(column_name));
						// System.out.print("ToJson: Integer");
					} else if (rsed.getColumnType(i) == java.sql.Types.NVARCHAR) {
						obj.put(column_name, rs.getNString(column_name));
						// System.out.print("ToJson: Nvarchar");
					} else if (rsed.getColumnType(i) == java.sql.Types.VARCHAR) {
						obj.put(column_name, rs.getString(column_name));
						// System.out.print("ToJson: Varchar");
					} else if (rsed.getColumnType(i) == java.sql.Types.TINYINT) {
						obj.put(column_name, rs.getInt(column_name));
						// System.out.print("ToJson: TinyInt");
					} else if (rsed.getColumnType(i) == java.sql.Types.SMALLINT) {
						obj.put(column_name, rs.getInt(column_name));
						// System.out.print("ToJson: SmallInt");
					} else if (rsed.getColumnType(i) == java.sql.Types.DATE) {
						obj.put(column_name, rs.getDate(column_name));
						// System.out.print("ToJson: Date");
					} else if (rsed.getColumnType(i) == java.sql.Types.TIMESTAMP) {
						obj.put(column_name, rs.getTimestamp(column_name));
						// System.out.print("ToJson: Timestamp");
					} else if (rsed.getColumnType(i) == java.sql.Types.NUMERIC) {
						obj.put(column_name, rs.getBigDecimal(column_name));
						// System.out.print("ToJson: Numeric");
					} else {
						obj.put(column_name, rs.getObject(column_name));
						// System.out.print("ToJson: Object" + column_name);
					}

				}// for
				json.put(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print(e);
			System.out.print(column_name);
		}
		return json;
	}
}
