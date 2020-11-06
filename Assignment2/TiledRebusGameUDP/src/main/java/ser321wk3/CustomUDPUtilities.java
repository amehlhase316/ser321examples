package ser321wk3;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.apache.commons.io.FileUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.zip.DeflaterInputStream;
import java.util.zip.InflaterInputStream;

import javax.imageio.ImageIO;

public class CustomUDPUtilities {

    public static final int BYTE_ARRAY_SIZE = 50_000;

    private CustomUDPUtilities() {
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

    public static String convertImageFileToBase64encodedString(File imageFile) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(imageFile);
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public static BufferedImage convertBase64encodedStringToBufferedImage(String encodedImage) throws IOException {
        byte[] decodedImageBytes = Base64.getDecoder().decode(encodedImage);
        return ImageIO.read(new ByteArrayInputStream(decodedImageBytes));
    }

    public static void writeCustomProtocolOut(ByteBuffer byteBuffer, CustomProtocol protocol) throws IOException {
        byteBuffer.clear();
        ObjectMapper mapper = new ObjectMapper();
        byteBuffer.put(new DeflaterInputStream(new ByteArrayInputStream(mapper.writeValueAsBytes(protocol))).readAllBytes());
    }

    public static CustomProtocol readCustomProtocol(DataInputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeFactory typeFactory = mapper.getTypeFactory();
        return mapper.readValue(new InflaterInputStream(inputStream).readAllBytes(), typeFactory.constructType(CustomProtocol.class));
    }
}
