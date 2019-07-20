package org.iotwuxi.embedded;

import com.upokecenter.cbor.CBORObject;
import com.upokecenter.numbers.EDecimal;
import org.joda.time.DateTime;
import org.junit.Test;
import org.lasinger.tools.hexdump.Hexdump;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TestCBOR {
    private void printCborHexString(CBORObject obj) {
        byte[] bytes = obj.EncodeToBytes();
        String hexString = Hexdump.hexdump(bytes);
        System.out.println(obj.toString());
        System.out.println(hexString);
    }

    @Test
    public void testInt() {
        CBORObject obj = CBORObject.FromObject(1);
        printCborHexString(obj);
    }

    @Test
    public void testInt100() {
        CBORObject obj = CBORObject.FromObject(100);
        printCborHexString(obj);
    }

    @Test
    public void testIntNegative100() {
        CBORObject obj = CBORObject.FromObject(-100);
        printCborHexString(obj);
    }

    @Test
    public void testByteArray() {
        int length = 500;
        byte[] testByte = new byte[length];
        for (int i = 0; i < length; i++) {
            testByte[i] = 0x30;
        }
        CBORObject obj = CBORObject.FromObject(testByte);
        printCborHexString(obj);
    }

    @Test
    public void testString() {
        CBORObject obj = CBORObject.FromObject("IETF");
        printCborHexString(obj);
    }

    @Test
    public void testLargeString() {
        int length = 24;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append("0");
        }

        CBORObject obj = CBORObject.FromObject(builder.toString());
        printCborHexString(obj);
    }

    @Test
    public void testArray() {
        CBORObject obj = CBORObject.NewArray();

        obj.Add(CBORObject.FromObject(1));
        obj.Add(CBORObject.FromObject(2));
        obj.Add(CBORObject.FromObject(3));
        printCborHexString(obj);
    }

    @Test
    public void testArray24() {
        CBORObject obj = CBORObject.NewArray();

        obj.Add(CBORObject.FromObject(500));
        obj.Add(CBORObject.FromObject(501));
        obj.Add(CBORObject.FromObject(502));
        printCborHexString(obj);
    }

    /**
     * 嵌套数组 [1, [2,3], [4,5]]
     */
    @Test
    public void testMultiArray() {
        CBORObject obj = CBORObject.NewArray();
        obj.Add(CBORObject.FromObject(1));

        CBORObject subArray1 = CBORObject.NewArray();
        subArray1.Add(CBORObject.FromObject(2));
        subArray1.Add(CBORObject.FromObject(3));
        obj.Add(subArray1);

        CBORObject subArray2 = CBORObject.NewArray();
        subArray2.Add(CBORObject.FromObject(4));
        subArray2.Add(CBORObject.FromObject(5));
        obj.Add(subArray2);

        printCborHexString(obj);
    }

    @Test
    public void testLargeArray() {
        CBORObject obj = CBORObject.NewArray();

        int length = 25;
        for (int i = 0; i < length; i++) {
            int temp = i + 100;
            obj.Add(CBORObject.FromObject(temp));
        }

        printCborHexString(obj);
    }

    @Test
    public void testMap() {
        CBORObject obj = CBORObject.NewMap();

        obj.set(1, CBORObject.FromObject(2));
        obj.set(3, CBORObject.FromObject(4));

        printCborHexString(obj);
    }

    @Test
    public void testJavaMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);

        CBORObject obj = CBORObject.FromObject(map);

        printCborHexString(obj);
    }

    @Test
    public void testTrue() {
        CBORObject obj = CBORObject.FromObject(true);

        printCborHexString(obj);
    }

    @Test
    public void testBigDecimal() {
        String decimalString = BigDecimal.valueOf(273.15).toString();
        CBORObject obj = CBORObject.FromObject(EDecimal.FromString(decimalString));

        printCborHexString(obj);
    }

    @Test
    public void testDateTime() {
        DateTime dt = new DateTime(2013, 3, 21, 20, 04, 0);
        CBORObject obj = CBORObject.FromObject(dt.toDate());

        printCborHexString(obj);
    }

    @Test
    public void testCBORTag() {
        byte[] array = new byte[] {0x01, 0x02, 0x03, 0x04};
        CBORObject obj = CBORObject.FromObjectAndTag(array, 23);

        printCborHexString(obj);
    }

}
