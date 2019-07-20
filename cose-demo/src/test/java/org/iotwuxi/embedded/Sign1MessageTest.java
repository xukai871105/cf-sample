package org.iotwuxi.embedded;

import COSE.*;
import org.apache.commons.codec.binary.Hex;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sign1MessageTest {
    private static final Logger logger = LoggerFactory.getLogger(Sign1MessageTest.class);
    static OneKey signingKey;
    static OneKey sign2Key;
    static OneKey sign3Key;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws CoseException {
        signingKey = OneKey.generateKey(AlgorithmID.ECDSA_256);
        sign2Key = OneKey.generateKey(AlgorithmID.ECDSA_512);
        sign3Key = OneKey.generateKey(AlgorithmID.ECDSA_384);
    }

    @Test
    public void testSignAMessage() throws CoseException {
        byte[] result = SignAMessage("This is lots of content");
        assert( VerifyAMessage(result, signingKey) );
        assert( !VerifyAMessage(result, sign2Key));
    }

    public static byte[] SignAMessage(String ContentToSign) throws CoseException {

        //  Create the signed message
        Sign1Message msg = new Sign1Message();

        //  Add the content to the message
        msg.SetContent(ContentToSign);
        msg.addAttribute(HeaderKeys.Algorithm, signingKey.get(KeyKeys.Algorithm), Attribute.PROTECTED);

        //  Force the message to be signed
        msg.sign(signingKey);

        //  Now serialize out the message
        byte[] signBytes =  msg.EncodeToBytes();
        System.out.println(msg.EncodeToCBORObject().toString());
       logger.info("{} Hex: {}", signBytes.length, Hex.encodeHexString(signBytes));
        return signBytes;
    }

    public static boolean VerifyAMessage(byte[] message, OneKey key) {
        boolean result;

        try {
            Sign1Message msg = (Sign1Message) Message.DecodeFromBytes(message);

            result = msg.validate(key);
        } catch (CoseException e) {
            return false;
        }

        return result;
    }

}
