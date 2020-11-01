package Ser321WK3;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

public class CustomTCPUtilities {
    public static Payload parsePayload(String payload) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeFactory typeFactory = mapper.getTypeFactory();
        try {
            return mapper.readValue(payload, typeFactory.constructType(Payload.class));
        } catch (Exception e) {
            System.out.println("Error while parsing payload: " + payload);
            e.printStackTrace();
        }
        return new Payload("", false, false);
    }

    public static int parseInt(String userInput) {
        try {
            return Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            /*IGNORED*/
        }
        return 0;
    }
}
