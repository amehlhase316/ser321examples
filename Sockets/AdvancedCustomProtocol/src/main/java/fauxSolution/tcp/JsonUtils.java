package fauxSolution.tcp;

import org.json.JSONObject;

public class JsonUtils {
  public static JSONObject fromByteArray(byte[] bytes) {
    String jsonString = new String(bytes);
    return new JSONObject(jsonString);
  }
  
  public static byte[] toByteArray(JSONObject object) {
    return object.toString().getBytes();
  }
}
