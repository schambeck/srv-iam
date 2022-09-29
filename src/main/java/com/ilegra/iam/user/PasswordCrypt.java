package com.ilegra.iam.user;

import lombok.SneakyThrows;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.SecureRandom;

public class PasswordCrypt {

  @SneakyThrows
  public static String generateStrongPasswordHash(String password, byte[] salt) {
    int iterations = 1000;
    char[] chars = password.toCharArray();

    PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    byte[] hash = skf.generateSecret(spec).getEncoded();
    return iterations + ":" + toHex(salt) + ":" + toHex(hash);
  }

  @SneakyThrows
  public static byte[] getSalt() {
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    byte[] salt = new byte[16];
    sr.nextBytes(salt);
    return salt;
  }

  private static String toHex(byte[] array) {
    BigInteger bi = new BigInteger(1, array);
    String hex = bi.toString(16);

    int paddingLength = (array.length * 2) - hex.length();
    if (paddingLength > 0) {
      return String.format("%0" + paddingLength + "d", 0) + hex;
    } else {
      return hex;
    }
  }

}
