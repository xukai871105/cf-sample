/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iotwuxi.embedded;

import COSE.*;
import com.upokecenter.cbor.CBORObject;
import org.apache.commons.codec.binary.Hex;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertArrayEquals;

/**
 *
 * @author jimsch
 */
public class Encrypt0MessageTest {
    byte[] rgbKey128 = {'a', 'b', 'c', 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    byte[] rgbKey256 = {'a', 'b', 'c', 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,28, 29, 30, 31, 32};
    byte[] rgbContent = {'T', 'h', 'i', 's', ' ', 'i', 's', ' ', 's', 'o', 'm', 'e', ' ', 'c', 'o', 'n', 't', 'e', 'n', 't'};
    byte[] rgbIV128 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    byte[] rgbIV96 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    
    public Encrypt0MessageTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Test of decrypt method, of class Encrypt0Message.
     */
    @Test
    public void testRoundTrip() throws Exception {
        System.out.println("Round Trip");
        Encrypt0Message msg = new Encrypt0Message();
        msg.addAttribute(HeaderKeys.Algorithm, AlgorithmID.AES_GCM_128.AsCBOR(), Attribute.PROTECTED);
        msg.addAttribute(HeaderKeys.IV, CBORObject.FromObject(rgbIV96), Attribute.PROTECTED);
        msg.SetContent(rgbContent);
        msg.encrypt(rgbKey128);

        byte[] rgbMsg = msg.EncodeToBytes();
        System.out.println(msg.EncodeToCBORObject().toString());
        System.out.println("HEXSTRING[" + rgbMsg.length + "]:" + Hex.encodeHexString(rgbMsg));

        msg = (Encrypt0Message) Message.DecodeFromBytes(rgbMsg, MessageTag.Encrypt0);
        byte[] contentNew = msg.decrypt(rgbKey128);
      
        assertArrayEquals(rgbContent, contentNew);
    }
    

    @Test
    public void roundTripDetached() throws CoseException, IllegalStateException {
        Encrypt0Message msg = new Encrypt0Message(true, false);
        
        msg.addAttribute(HeaderKeys.Algorithm, AlgorithmID.AES_GCM_128.AsCBOR(), Attribute.PROTECTED);
        msg.addAttribute(HeaderKeys.IV, CBORObject.FromObject(rgbIV96), Attribute.UNPROTECTED);
        msg.SetContent(rgbContent);
        msg.encrypt(rgbKey128);
        
        byte[] content = msg.getEncryptedContent();
        
        byte[] rgb = msg.EncodeToBytes();
        
        msg = (Encrypt0Message) Message.DecodeFromBytes(rgb);
        msg.setEncryptedContent(content);
        msg.decrypt(rgbKey128);
        
    }    
    









}
