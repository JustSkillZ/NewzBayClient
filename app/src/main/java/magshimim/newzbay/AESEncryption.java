package magshimim.newzbay;

import android.util.Base64;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

//import javax.xml.bind.DatatypeConverter;

public class AESEncryption
{

   private String encryptionKey;

    public AESEncryption()
    {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // for example
            SecretKey secretKey = keyGen.generateKey();
            this.encryptionKey = secretKeyToString(secretKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public String encrypt(String plainText) throws Exception
    {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public String decrypt(String encrypted) throws Exception
    {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        byte[] plainBytes = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));

        return new String(plainBytes);
    }

    private Cipher getCipher(int cipherMode)
            throws Exception
    {
        String encryptionAlgorithm = "AES";
        SecretKeySpec keySpecification = new SecretKeySpec(
                encryptionKey.getBytes("UTF-8"), encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(cipherMode, keySpecification);

        return cipher;
    }


    private String secretKeyToString(SecretKey secretKey)
    {
        // create new key
        // SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        // get base64 encoded version of the key
        String encodedKey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
        return encodedKey;
    }

    private SecretKey stringToSecretKey(String secretKeyStr)
    {
        byte[] decodedKey = Base64.decode(secretKeyStr, Base64.DEFAULT);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return secretKey;
    }
    public String getAesKey()
    {
        return encryptionKey;
    }


}