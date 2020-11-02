package Ser321WK3;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.awaitility.Awaitility;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import Ser321WK3.Client.ClientGui;


public class CustomTCPUtilities {
    public static Payload parsePayload(String payload) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

    public static void setReceivedData(AtomicReference<Payload> receivedDataString, Payload payload) {
        receivedDataString.set(payload);
    }

    public static void waitForData(DataInputStream inputStream, ClientGui gameGui, AtomicReference<Payload> payloadAtomicReference, int timeToWait) throws IOException {
        if (inputStream == null) {
            Awaitility.await().atMost(timeToWait, TimeUnit.SECONDS).until(gameGui::userInputCompleted);
            gameGui.setUserInputCompleted(false);
            payloadAtomicReference.set(new Payload(gameGui.outputPanel.getCurrentInput(), false, false));
            gameGui.outputPanel.setInputText("");
        } else {
            Awaitility.await().atMost(timeToWait, TimeUnit.SECONDS).until(() -> {
                setReceivedData(payloadAtomicReference, readPayload(inputStream));
                return !(payloadAtomicReference.get() == null);
            });
        }
    }

    public static void writePayloadOut(Payload output, OutputStream outputStream) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(output);
    }

    public static Payload readPayload(InputStream inputStream) throws IOException, ClassNotFoundException {
        return ((Payload) (new ObjectInputStream(inputStream)).readObject());
    }
}
