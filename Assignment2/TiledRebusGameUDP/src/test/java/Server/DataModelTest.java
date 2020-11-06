package Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.logging.Logger;

import ser321wk3.CustomProtocol;
import ser321wk3.CustomProtocolHeader;
import ser321wk3.CustomUDPUtilities;
import ser321wk3.Payload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ser321wk3.CustomUDPUtilities.writeCustomProtocolOut;

public class DataModelTest {

    public static final String TEST_BASE = "16";
    public static final String TEST_FORMAT = "json";
    private static final Logger LOGGER = Logger.getLogger(DataModelTest.class.getName());
    private static final String TEST_MESSAGE = "Test Message";
    private static final boolean TEST_WON_GAME = true;
    private static final boolean TEST_GAME_OVER = true;
    private static final boolean TEST_ANSWERED_CORRECTLY = true;
    private static final CustomProtocolHeader.Operation TEST_OPERATION = CustomProtocolHeader.Operation.INITIALIZE;
    private static ObjectMapper mapper;

    @BeforeAll
    public static void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    public void testPayloadNullImage() throws JsonProcessingException {
        // Null image payload
        Payload testPayload = new Payload(null, TEST_MESSAGE, TEST_WON_GAME, TEST_GAME_OVER, TEST_ANSWERED_CORRECTLY);

        byte[] serializedPayload = mapper.writeValueAsBytes(testPayload);

        TypeFactory typeFactory = mapper.getTypeFactory();
        Payload deserializedPayload = mapper.readValue(new String(serializedPayload), typeFactory.constructType(Payload.class));
        assertEquals(testPayload, deserializedPayload);
        assertEquals(TEST_MESSAGE, deserializedPayload.getMessage());
        assertEquals(TEST_ANSWERED_CORRECTLY, deserializedPayload.isAnswerIsCorrect());
        assertEquals(TEST_GAME_OVER, deserializedPayload.isGameOver());
        assertEquals(TEST_WON_GAME, deserializedPayload.isWonGame());
    }

    @Test
    public void testPayloadImageNotNull() throws IOException {
        File image = new File("puzzles/Hamilton.jpg");
        String encodedImage = CustomUDPUtilities.convertImageFileToBase64encodedString(image);
        Payload testPayload = new Payload(encodedImage, TEST_MESSAGE, TEST_WON_GAME, TEST_GAME_OVER, TEST_ANSWERED_CORRECTLY);

        byte[] serializedPayload = mapper.writeValueAsBytes(testPayload);

        TypeFactory typeFactory = mapper.getTypeFactory();
        Payload deserializedPayload = mapper.readValue(new String(serializedPayload), typeFactory.constructType(Payload.class));
        assertEquals(testPayload, deserializedPayload);
        assertEquals(encodedImage, deserializedPayload.getBase64encodedCroppedImage());
    }

    @Test
    public void testCustomProtocolHeader() throws JsonProcessingException {
        CustomProtocolHeader testProtocolHeader = new CustomProtocolHeader(TEST_OPERATION, TEST_BASE, TEST_FORMAT);
        byte[] serializedProtocolHeader = mapper.writeValueAsBytes(testProtocolHeader);

        TypeFactory typeFactory = mapper.getTypeFactory();
        CustomProtocolHeader deserializedProtocolHeader = mapper.readValue(new String(serializedProtocolHeader), typeFactory.constructType(CustomProtocolHeader.class));

        assertEquals(testProtocolHeader, deserializedProtocolHeader);
        assertEquals(TEST_OPERATION, deserializedProtocolHeader.getOperation());
        assertEquals(TEST_BASE, deserializedProtocolHeader.getBase());
        assertEquals(TEST_FORMAT, deserializedProtocolHeader.getFormat());
    }

    @Test
    public void testCustomProtocol() throws IOException {
        final File image = new File("puzzles/Hamilton.jpg");
        final String encodedImage = CustomUDPUtilities.convertImageFileToBase64encodedString(image);
        final Payload testPayload = new Payload(encodedImage, TEST_MESSAGE, TEST_WON_GAME, TEST_GAME_OVER, TEST_ANSWERED_CORRECTLY);
        final CustomProtocolHeader testProtocolHeader = new CustomProtocolHeader(TEST_OPERATION, TEST_BASE, TEST_FORMAT);
        final CustomProtocol testProtocol = new CustomProtocol(testProtocolHeader, testPayload, UUID.randomUUID().toString());

        TypeFactory typeFactory = mapper.getTypeFactory();

        String serializedProtocol = mapper.writeValueAsString(testProtocol);

        CustomProtocol deserializedProtocol = mapper.readValue(serializedProtocol, typeFactory.constructType(CustomProtocol.class));
        assertEquals(testProtocol, deserializedProtocol);
        assertEquals(testProtocolHeader, deserializedProtocol.getHeader());
        assertEquals(testPayload, deserializedProtocol.getPayload());
    }

    @Test
    public void testCustomProtocolToByteArray() throws IOException {
        final File image = new File("puzzles/Hamilton.jpg");
        final String encodedImage = CustomUDPUtilities.convertImageFileToBase64encodedString(image);
        final Payload testPayload = new Payload(encodedImage, TEST_MESSAGE, TEST_WON_GAME, TEST_GAME_OVER, TEST_ANSWERED_CORRECTLY);
        final CustomProtocolHeader testProtocolHeader = new CustomProtocolHeader(TEST_OPERATION, TEST_BASE, TEST_FORMAT);
        final CustomProtocol testProtocol = new CustomProtocol(testProtocolHeader, testPayload, UUID.randomUUID().toString());

        TypeFactory typeFactory = mapper.getTypeFactory();
        ByteBuffer buffer = ByteBuffer.allocate(1_000_000);
        writeCustomProtocolOut(buffer, testProtocol);

        CustomProtocol deserializedProtocol = CustomUDPUtilities.readCustomProtocol(new DataInputStream(new ByteArrayInputStream(buffer.array())));
        assertEquals(testProtocol, deserializedProtocol);
        assertEquals(testProtocolHeader, deserializedProtocol.getHeader());
        assertEquals(testPayload, deserializedProtocol.getPayload());
    }

    @Test
    public void testPayloadImageNotNullToByteArray() throws IOException {
        File image = new File("puzzles/Hamilton.jpg");
        String encodedImage = CustomUDPUtilities.convertImageFileToBase64encodedString(image);
        Payload testPayload = new Payload(encodedImage, TEST_MESSAGE, TEST_WON_GAME, TEST_GAME_OVER, TEST_ANSWERED_CORRECTLY);

        ByteBuffer buffer = ByteBuffer.allocate(1_000_000);
        TypeFactory typeFactory = mapper.getTypeFactory();
        buffer.put(mapper.writeValueAsBytes(testPayload));

        Payload deserializedPayload = mapper.readValue(buffer.array(), typeFactory.constructType(Payload.class));
        assertEquals(testPayload, deserializedPayload);
        assertEquals(encodedImage, deserializedPayload.getBase64encodedCroppedImage());
    }
}
