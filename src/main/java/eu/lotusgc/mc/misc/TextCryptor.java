package eu.lotusgc.mc.misc;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class TextCryptor {
	
	/*
	 * Class made originally for a former Project "RediCraft" and it's own discord bot for the message logging.
	 * Author: Grubsic
	 */
	
	private static SecretKeySpec secretKey;
	private static byte[] key;

	public static void setKey(char[] myKey){
        try{
            MessageDigest sha;
            key = new String(myKey).getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
}

	public static String encrypt(String strToEncrypt, char[] secret){
		try{
			setKey(secret);
			Cipher cipher = null;
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
		}
		catch(NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException e){
			e.printStackTrace();
		}
		return null;
	}

	public static String decrypt(String strToDecrypt, char[] secret){
		try{
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		}
		catch(Exception e){
			System.out.println("Error while decrypting: " + e);
		}
		return null;
	}

}
