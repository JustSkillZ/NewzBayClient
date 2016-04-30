package magshimim.newzbay;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryption
{

   private String encryptionKey;

    public AESEncryption()
    {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            this.encryptionKey = secretKeyToString(secretKey);
            this.encryptionKey = this.encryptionKey.substring(0,encryptionKey.length() - 1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function encrypt the text that it get according to the key of the user.
     * @param plainText encrypted text.
     * @return decrypted text.
     */
    public String encrypt(String plainText) throws Exception
    {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    /**
     * This function decrypt the text that it get according to the key of the user.
     * @param encrypted encrypted text.
     * @return decrypted text.
     */
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

    /**
     * This function convert the secret key to string.
     * @param secretKey encrypted text.
     * @return the AES Key as String.
     */
    private String secretKeyToString(SecretKey secretKey)
    {
        String encodedKey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
        return encodedKey;
    }

    public String getAesKey()
    {
        return encryptionKey;
    }
}