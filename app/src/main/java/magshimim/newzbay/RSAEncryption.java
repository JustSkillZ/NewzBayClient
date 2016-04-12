package magshimim.newzbay;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class RSAEncryption {

    private PublicKey publicKey;


    public RSAEncryption(String publicKeyString)
    {
        stringToPublicKey(publicKeyString);
    }

    public PublicKey stringToPublicKey(String publicKeyString)
    {
        /*PublicKey key = null;
        byte[] keyBytes = new byte[0];
        try {
            keyBytes = Base64.decode(publicKeyString.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            key = keyFactory.generatePublic(spec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return key;*/
        RSAPublicKey key = null;
        String modules = publicKeyString.substring(publicKeyString.indexOf("modulus: ") + ("modulus: ").length(), publicKeyString.indexOf("public exponent: ") - 2);
        //modules = modules.substring(0, );
        String exponet = publicKeyString.substring(publicKeyString.indexOf("public exponent: ") + "public exponent: ".length());


        BigInteger modulus = new BigInteger(modules, 16);
        BigInteger pubExp = new BigInteger(exponet, 16);

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, pubExp);
            publicKey = key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return key;
    }

    /**
     * Encrypt the plain text using public key.
     text: original plain text
     key:The public key
     return Encrypted text
     */
    public String encrypt(String text) {
        byte[] cipherText = null;
        String encrypString = "";
        try {
            // get an RSA cipher object and print the provider
            Cipher cipher = Cipher.getInstance("RSA");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
            cipherText = cipher.doFinal(text.getBytes());
            encrypString = Base64.encodeToString(cipherText,Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypString;
    }
}
