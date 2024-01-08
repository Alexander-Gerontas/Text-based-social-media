package com.alg.social_media.utils;

import com.alg.social_media.configuration.Constants;
import javax.inject.Inject;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class PasswordEncoder {
  private final String secretKey;
  private final StandardPBEStringEncryptor standardPBEStringEncryptor;

  @Inject
  public PasswordEncoder(StandardPBEStringEncryptor standardPBEStringEncryptor) {
    this.secretKey = Constants.PASSWORD_SALT;
    this.standardPBEStringEncryptor = standardPBEStringEncryptor;
    this.standardPBEStringEncryptor.setPassword(secretKey);
  }

  public String encryptPassword(String plaintextPassword) {
    return standardPBEStringEncryptor.encrypt(plaintextPassword);
  }

  public String decryptPassword(String encryptedPassword) {
    return standardPBEStringEncryptor.decrypt(encryptedPassword);
  }
}
