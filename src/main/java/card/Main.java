package card;

import java.io.EOFException;
import java.util.List;
import java.util.function.Consumer;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import javax.xml.bind.DatatypeConverter;

/**
 * Requires pcscd -daemon to be running
 */
public class Main {
    static CommandAPDU GET_UID = new CommandAPDU(new byte[]{(byte) 0xff, (byte) 0xca, 0x00, 0x00, 0x00});
    static CommandAPDU MIFARE_READ_KEYS = new CommandAPDU(new byte[]{(byte) 0xff, (byte) 0x82, (byte) 0x20, (byte) 0x01, (byte) 0x06, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff});

    static CommandAPDU authenticateMifareRead(byte readerSlot) {
        return new CommandAPDU(new byte[]{(byte) 0xff, (byte) 0x86, (byte) 0x00, (byte) 0x00, (byte) 0x05, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x60, readerSlot});
    }

    static CommandAPDU readMifareBlock(byte sector, byte block) {
        return new CommandAPDU(0xff, 0xb0, sector, block, 0x10);
    }

    static CommandAPDU writeMifareBlock(byte sector, byte block, byte[] data) {
        return new CommandAPDU(0xff, 0xd6, sector, block, data);
    }

    public static void main(String[] args) {
        try {
            // show the list of available terminals
            TerminalFactory factory = TerminalFactory.getDefault();

            List<CardTerminal> terminals = factory.terminals().list();
            System.out.println("Terminals: " + terminals);

            // get just the first terminal
            byte readerSlot = 0;
            for (CardTerminal terminal : terminals) {
                if (terminal.isCardPresent()) {
                    // establish a connection with the card
                    Card card = terminal.connect("T=1");
                    System.out.println("card: " + card);

                    CardChannel channel = card.getBasicChannel();

                    invokeApdu(channel, GET_UID, Main::readUID);
                    invokeApdu(channel, MIFARE_READ_KEYS, Main::noOp);
                    invokeApdu(channel, authenticateMifareRead(readerSlot), Main::noOp);

                    invokeApdu(channel, readMifareBlock((byte) 0x00, (byte) 0x01), Main::readBlock);

//                    invokeApdu(channel, writeMifareBlock((byte) 0x00, (byte) 0x01, DatatypeConverter.parseHexBinary("01170720011021000000000000000000")), Main::noOp);

                    // disconnect
                    card.disconnect(false);
                }
                readerSlot++;
            }
        } catch (Exception e) {
            // ignore
        }
    }

    static void invokeApdu(CardChannel channel, CommandAPDU apducCommand, Consumer<ResponseAPDU> responseConsumer) throws CardException, EOFException {
        ResponseAPDU responseAPDU = channel.transmit(apducCommand);
        if (responseAPDU.getSW1() == 0x90 && responseAPDU.getSW2() == 0x00) {
            responseConsumer.accept(responseAPDU);
        } else {
            System.out.println("Failed - Response: " + responseAPDU.toString());
        }
    }

    static void invokeApdu(CardChannel channel, String apducCommand, Consumer<ResponseAPDU> responseConsumer) throws CardException, EOFException {
        byte[] apdu = DatatypeConverter.parseHexBinary(apducCommand);
        invokeApdu(channel, new CommandAPDU(apdu), responseConsumer);
    }

    static void readUID(ResponseAPDU responseAPDU) {
        System.out.println("UID: " + readLong(responseAPDU.getData()));
    }

    static void noOp(ResponseAPDU responseAPDU) {
    }

    static void readBlock(ResponseAPDU responseAPDU) {
        System.out.println(DatatypeConverter.printHexBinary(responseAPDU.getData()));
    }

    static long readLong(byte[] data) {
        long value = (((long) (data[3] & 255) << 24) +
                ((data[2] & 255) << 16) +
                ((data[1] & 255) << 8) +
                ((data[0] & 255) << 0));
        return value;
    }
}
