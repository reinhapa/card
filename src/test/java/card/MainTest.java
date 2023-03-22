package card;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainTest {
    private byte[] data;
    private String dataString;

    @BeforeEach
    void setUp() {
        data = new byte[20];
        for (int idx=0; idx<20; idx++) {
            data[idx] = (byte) idx;
        }
        dataString = "000102030405060708090a0b0c0d0e0f10111213";
    }

    @Test
    void printHexBinary() {
        assertEquals(dataString, Main.printHexBinary(data));
    }

    @Test
    void parseHexBinary() {
        assertArrayEquals(data, Main.parseHexBinary(dataString));
    }
}
