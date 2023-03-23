package card;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HexFormat;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class MainTest {
  private byte[] data;
  private String dataString;

  @BeforeEach
  void setUp() {
    data = new byte[20];
    for (int idx = 0; idx < 20; idx++) {
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

  @Test
  @Disabled
  void readRfid() throws CardException {
    // show the list of available terminals
    TerminalFactory factory = TerminalFactory.getDefault();
    List<CardTerminal> terminals = factory.terminals().list();
    System.out.println("Terminals: " + terminals);
    // get the first terminal
    CardTerminal terminal = terminals.get(1);
    // establish a connection with the card
    Card card = terminal.connect("*");
    System.out.println("card: " + card);

    HexFormat hexFormat = HexFormat.ofDelimiter(":");
    System.out.println("atr:" + hexFormat.formatHex(card.getATR().getBytes()));

    CardChannel channel = card.getBasicChannel();
    ResponseAPDU response = channel.transmit(Commands.READ);
    System.out.println("data: " + hexFormat.formatHex(response.getData()));

    byte[] bytes = response.getBytes();
    System.out.println("result: " + hexFormat.formatHex(bytes, bytes.length - 2, bytes.length));

    // disconnect
    card.disconnect(false);
  }
}
