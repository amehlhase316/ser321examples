package Ser321WK3;

import org.awaitility.Awaitility;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import Ser321WK3.Client.ClientGui;

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

    public static void setReceivedData(AtomicReference<String> receivedDataString, String s) {
        receivedDataString.set(s);
    }

    public static void waitForData(DataInputStream inputStream, ClientGui gameGui, AtomicReference<String> receivedData, int timeToWait) throws IOException {
        if (inputStream == null) {
            Awaitility.await().atMost(timeToWait, TimeUnit.SECONDS).until(gameGui::userInputCompleted);
            gameGui.setUserInputCompleted(false);
            receivedData.set(gameGui.outputPanel.getCurrentInput());
            gameGui.outputPanel.setInputText("");
        } else {
            Awaitility.await().atMost(timeToWait, TimeUnit.SECONDS).until(() -> {
                setReceivedData(receivedData, inputStream.readUTF());
                return !receivedData.get().isEmpty();
            });
        }
    }


}
