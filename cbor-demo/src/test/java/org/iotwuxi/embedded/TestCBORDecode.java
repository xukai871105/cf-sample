package org.iotwuxi.embedded;

import com.upokecenter.cbor.CBORObject;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;
import org.apache.commons.codec.binary.Hex;

public class TestCBORDecode {

    @Test
    public void testCommon() throws DecoderException {
        final String hexString = "d74401020304";
        CBORObject obj = CBORObject.DecodeFromBytes(Hex.decodeHex(hexString));
        System.out.println(obj.toString());
    }
}
