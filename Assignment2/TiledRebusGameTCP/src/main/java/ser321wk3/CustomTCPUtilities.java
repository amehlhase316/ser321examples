package ser321wk3;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.apache.commons.io.FileUtils;
import org.awaitility.Awaitility;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import ser321wk3.client.ClientGui;

public class CustomTCPUtilities {

    private static final Thread DUMMY_HOOK = new Thread();

    private CustomTCPUtilities() {
        throw new IllegalStateException("This is a Utility Class and should not be instantiated.");
    }

    public static int parseInt(String userInput) {
        try {
            return Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            /*IGNORED*/
        }
        return 0;
    }

    public static void setReceivedData(AtomicReference<CustomProtocol> receivedDataString, CustomProtocol protocol) {
        receivedDataString.set(protocol);
    }

    public static void waitForData(DataInputStream inputStream, ClientGui gameGui, AtomicReference<CustomProtocol> protocolAtomicReference, int timeToWait) {
        if (inputStream == null) {
            Awaitility.await().atMost(timeToWait, TimeUnit.SECONDS).until(gameGui::userInputCompleted);
            gameGui.setUserInputCompleted(false);
            Payload responseToServer = new Payload(null, gameGui.outputPanel.getCurrentInput(), false, false);
            CustomProtocolHeader header;
            if (gameGui.solve()) {
                header = new CustomProtocolHeader(CustomProtocolHeader.Operation.SOLVE, "16", "json");
            } else {
                header = new CustomProtocolHeader(CustomProtocolHeader.Operation.ANSWER, "16", "json");
            }
            setReceivedData(protocolAtomicReference, new CustomProtocol(header, responseToServer));
            gameGui.outputPanel.setInputText("");
        } else {
            Awaitility.await().atMost(timeToWait, TimeUnit.SECONDS).until(() -> {
                setReceivedData(protocolAtomicReference, readCustomProtocol(inputStream));
                return protocolAtomicReference.get() != null;
            });
        }
    }

    public static boolean jvmIsShuttingDown() {
        try {
            Runtime.getRuntime().addShutdownHook(DUMMY_HOOK);
            Runtime.getRuntime().removeShutdownHook(DUMMY_HOOK);
        } catch (IllegalStateException e) {
            return true;
        }
        return false;
    }

    public static BufferedImage convertFileToImage(File fileToConvert) throws IOException {
        return ImageIO.read(fileToConvert);
    }

    public static String convertImageFileToBase64encodedString(File imageFile) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(imageFile);
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public static BufferedImage convertBase64encodedStringToBufferedImage(String encodedImage) throws IOException {
        byte[] decodedImageBytes = Base64.getDecoder().decode(encodedImage);
        return ImageIO.read(new ByteArrayInputStream(decodedImageBytes));
    }

    public static void writeCustomProtocolOut(DataOutputStream outputStream, CustomProtocol protocol) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        outputStream.writeUTF(mapper.writeValueAsString(protocol));
        outputStream.flush();
    }

    public static CustomProtocol readCustomProtocol(DataInputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeFactory typeFactory = mapper.getTypeFactory();
        return mapper.readValue(inputStream.readUTF(), typeFactory.constructType(CustomProtocol.class));
    }
}
