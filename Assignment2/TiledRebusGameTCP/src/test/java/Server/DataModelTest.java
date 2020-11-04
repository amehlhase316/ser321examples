package Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import ser321wk3.CustomProtocol;
import ser321wk3.CustomProtocolHeader;
import ser321wk3.CustomTCPUtilities;
import ser321wk3.Payload;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataModelTest {

    private static final Logger LOGGER = Logger.getLogger(DataModelTest.class.getName());
    private static final String TEST_MESSAGE = "Test Message";
    private static final boolean TEST_WON_GAME = true;
    private static final boolean TEST_GAME_OVER = true;
    private static final CustomProtocolHeader.Operation TEST_OPERATION = CustomProtocolHeader.Operation.INITIALIZE;
    private static ObjectMapper mapper;

    @BeforeAll
    public static void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    public void testPayloadNullImage() throws JsonProcessingException {
        // Null image payload
        Payload testPayload = new Payload(null, TEST_MESSAGE, TEST_WON_GAME, TEST_GAME_OVER);

        byte[] serializedPayload = mapper.writeValueAsBytes(testPayload);

        TypeFactory typeFactory = mapper.getTypeFactory();
        Payload deserializedPayload = mapper.readValue(new String(serializedPayload), typeFactory.constructType(Payload.class));
        assertEquals(testPayload, deserializedPayload);
    }

    @Test
    public void testPayloadImageNotNull() throws IOException {
        File image = new File("puzzles/Hamilton_0_0.jpg");
        String encodedImage = CustomTCPUtilities.convertImageFileToBase64encodedString(image);
        Payload testPayload = new Payload(encodedImage, TEST_MESSAGE, TEST_WON_GAME, TEST_GAME_OVER);

        byte[] serializedPayload = mapper.writeValueAsBytes(testPayload);

        TypeFactory typeFactory = mapper.getTypeFactory();
        Payload deserializedPayload = mapper.readValue(new String(serializedPayload), typeFactory.constructType(Payload.class));
        assertEquals(testPayload, deserializedPayload);
    }

    @Test
    public void testCustomProtocolHeader() throws JsonProcessingException {
        CustomProtocolHeader testProtocolHeader = new CustomProtocolHeader(TEST_OPERATION, "16", "JSON");
        byte[] serializedProtocolHeader = mapper.writeValueAsBytes(testProtocolHeader);

        TypeFactory typeFactory = mapper.getTypeFactory();
        CustomProtocolHeader deserializedProtocolHeader = mapper.readValue(new String(serializedProtocolHeader), typeFactory.constructType(CustomProtocolHeader.class));

        assertEquals(testProtocolHeader, deserializedProtocolHeader);
    }

    @Test
    public void testCustomProtocol() throws IOException {
        File image = new File("puzzles/Hamilton_0_0.jpg");
        String encodedImage = CustomTCPUtilities.convertImageFileToBase64encodedString(image);
        Payload testPayload = new Payload(encodedImage, TEST_MESSAGE, TEST_WON_GAME, TEST_GAME_OVER);
        CustomProtocolHeader testProtocolHeader = new CustomProtocolHeader(TEST_OPERATION, "16", "JSON");
        CustomProtocol testProtocol = new CustomProtocol(testProtocolHeader, testPayload);

        byte[] serializedProtocol = mapper.writeValueAsBytes(testProtocol);

        TypeFactory typeFactory = mapper.getTypeFactory();
        CustomProtocol deserializedProtocol = mapper.readValue(new String(serializedProtocol), typeFactory.constructType(CustomProtocol.class));
        assertEquals(testProtocol, deserializedProtocol);
    }
}
