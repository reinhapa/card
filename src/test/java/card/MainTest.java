package card;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MainTest {
    private byte[] data;
    private String dataString;

    @Before
    public void setUp() {
        data = new byte[20];
        for (int idx=0; idx<20; idx++) {
            data[idx] = (byte) idx;
        }
        dataString = "000102030405060708090a0b0c0d0e0f10111213";
    }

    @Test
    public void printHexBinary() {
        assertEquals(dataString, Main.printHexBinary(data));
    }

    @Test
    public void parseHexBinary() {
        assertArrayEquals(data, Main.parseHexBinary(dataString));
    }
}
