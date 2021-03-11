package lv.emes.libraries.communication.cryptography.hybrid;

import lv.emes.libraries.communication.cryptography.MS_CryptographyUtils;
import lv.emes.libraries.communication.cryptography.MS_Hash;
import lv.emes.libraries.communication.cryptography.MS_RSA;
import lv.emes.libraries.tools.MS_BadSetupException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class MS_CipherTest {

    public static final String A_SECRET_MESSAGE = "This is a secret message: Mācēt enkōdt ŗīōķņģ";

    @Test
    public void testEncryptAndDecryptAsymmetric() throws IllegalBlockSizeException {
        KeyPair keyPair = MS_RSA.generateKeyPair(MS_RSA.KEY_LENGTH_MINIMUM);
        String str = A_SECRET_MESSAGE;
        MS_RSA decryption = MS_RSA.forDecryption(keyPair.getPrivate());

        MS_Cipher cipher = MS_CipherBuilder.aBuilder()
                .withAsymmetricEncryptionKeys(AsymmetricKeyPair.fromKeyPair(keyPair))
                .build();

        String encrypted = cipher.encryptAsymmetric(str);
        String decrypted = cipher.decryptAsymmetric(encrypted);

        assertThat(decrypted).isEqualTo(decryption.decrypt(encrypted)).isEqualTo(str);

        // Now try to use only one of keys with brand new instance
        MS_Cipher cipher2 = MS_CipherBuilder.aBuilder()
                .withAsymmetricEncryptionKeys(AsymmetricKeyPair.fromKeyPair(new KeyPair(null, keyPair.getPrivate())))
                .build();
        String decrypted2 = cipher2.decryptAsymmetric(encrypted);
        assertThat(decrypted2).isEqualTo(decrypted).isEqualTo(str);
    }

    @Test
    public void testSigningAndVerification() throws IllegalBlockSizeException {
        final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKIkucAuBIxhw301XvIfPCxAy+BbYZYiOB3oRvxk+QTetFaehzTWi3iOQKShWlg9/UaCVnYtgtV229AukmPE09TS6zh6R6PgR2B+3CekdPUj5J0Nk6DCsaJmo9AcufhlJsjQwBLaZN/34yexnd2tt9ILn7msX4du+jHQrGP62YnVAgMBAAECgYAVbErKlInvTl945JtV3ECkDC+jxg8fuge2E+GFKYYpWY90Pl/Y4FvvIsRxvAvmytHzBfeMh7jYoWqrl+upmPDv4MP1I1G8amDuPQIXAQrjA5sJ8/pY7C8CJ5x2gz//qNiqGvB/PihRKp1OpSe8A+I67rQCUagKvtQ9XQUFQNPcRQJBANW+XncC0pApH0um3NMNpYLRKgRFK3/BThX4CAnuoQCphTSON9Mx+IzW8bx/CEVK5lK0IpLVA6hDYNvprdbrnp8CQQDCMtsoWLjyxb/i3xTb0EOF0Npz7+nRrcT3sV2XFLGg2cXxUrJMXQ2007SCi9zmKBH7Hf9LnOReHW3oLy0NwKcLAkA3gzrLlAsV7+g14L+HfQrYf/R2qXRTuOPL5uBHnBdqsZxr+ufazSathYHBIAkI3hwVrL0x/2r0v5MfaoCAyi8nAkEAvbxwcYAV22Sn8SJiP83AWxrOhN3PhkSQIC0TssvOLMj446oXeBKmlJkPUUkTX2+g+ce6Kgc0/Fsmiealzzz+NwJAa8OGIXwC46l8n3S+kBiTwvxCw/20nwmFXP7VUQjULaG71oRtMvByxeNe297R2rTuGdf2YMVDl5I4Hr77LFgk+g==";
        final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCiJLnALgSMYcN9NV7yHzwsQMvgW2GWIjgd6Eb8ZPkE3rRWnoc01ot4jkCkoVpYPf1GglZ2LYLVdtvQLpJjxNPU0us4ekej4EdgftwnpHT1I+SdDZOgwrGiZqPQHLn4ZSbI0MAS2mTf9+MnsZ3drbfSC5+5rF+Hbvox0Kxj+tmJ1QIDAQAB";
        MS_RSA decryption = MS_RSA.forDecryption(MS_RSA.convertPublicKey(PUBLIC_KEY));

        MS_Cipher cipher = MS_CipherBuilder.aBuilder()
                .withSignatureKeys(AsymmetricKeyPair.fromConstantStrings(PRIVATE_KEY, PUBLIC_KEY))
                .build();

        String signed = cipher.sign(A_SECRET_MESSAGE);
        String verified = cipher.verifySignature(signed);

        assertThat(verified).isEqualTo(decryption.decrypt(signed)).isEqualTo(A_SECRET_MESSAGE);

        // Now try to use only one of keys with brand new instance
        MS_Cipher cipher2 = MS_CipherBuilder.aBuilder()
                .withSignatureKeys(AsymmetricKeyPair.fromConstantStrings(null, PUBLIC_KEY))
                .build();
        String verified2 = cipher2.verifySignature(signed);
        assertThat(verified2).isEqualTo(verified).isEqualTo(A_SECRET_MESSAGE);
    }

    private final static String DEFAULT_HMAC_KEY = "Join all supported cryptographic operations into one class MS_Cipher";

    @Test
    public void testEncryptAndDecryptSymmetricWithoutHmacKey() throws GeneralSecurityException {
        String key = RandomStringUtils.random(10);

        MS_Cipher cipher = MS_CipherBuilder.aBuilder()
                .withSymmetricKeyAttributes(new SymmetricKeyAttributes().withSecretKey(key))
                .build();

        String encrypted = cipher.encryptSymmetric(A_SECRET_MESSAGE);
        String decrypted = cipher.decryptSymmetric(encrypted);
        assertThat(decrypted).isEqualTo(A_SECRET_MESSAGE);
        // Test that it's possible to decrypt this string by using exact HMAC key that MS_Cipher is using by default
        assertThat(MS_CryptographyUtils.decrypt(encrypted, key, DEFAULT_HMAC_KEY)).isEqualTo(A_SECRET_MESSAGE);
    }

    @Test
    public void testEncryptAndDecryptSymmetricWithHmacKey() throws GeneralSecurityException {
        String key = RandomStringUtils.random(10);
        String hmac = RandomStringUtils.random(10);

        MS_Cipher cipher = MS_CipherBuilder.aBuilder()
                .withSymmetricKeyAttributes(new SymmetricKeyAttributes().withSecretKey(key).withHmacKey(hmac))
                .build();

        String encrypted = cipher.encryptSymmetric(A_SECRET_MESSAGE);
        String decrypted = cipher.decryptSymmetric(encrypted);
        assertThat(decrypted).isEqualTo(A_SECRET_MESSAGE);
        assertThat(MS_CryptographyUtils.decrypt(encrypted, key, hmac)).isEqualTo(A_SECRET_MESSAGE);
    }

    @Test
    public void testHashing() {
        String salt = RandomStringUtils.random(10);

        HashingAttributes hashingAttributes = new HashingAttributes()
                .withSalt(salt)
                .withIterations(MS_Hash.KEY_LENGTH_MINIMUM * 10)
                .withKeyLength(MS_Hash.KEY_LENGTH_MINIMUM)
                .withEncodeInBase64(true);
        MS_Cipher cipher = MS_CipherBuilder.aBuilder()
                .withHashingAttributes(hashingAttributes)
                .build();

        assertThat(cipher.hash(A_SECRET_MESSAGE))
                .isEqualTo(MS_Hash.getHash(A_SECRET_MESSAGE, hashingAttributes.getSalt(), hashingAttributes.getKeyLength()));
    }

    @Test
    public void testHashingDefaultsExceptThatSaltIsSet() {
        String salt = RandomStringUtils.random(10);

        MS_Cipher cipher = MS_CipherBuilder.aBuilder().withHashingAttributes(new HashingAttributes().withSalt(salt)).build();

        MS_Hash hash = new MS_Hash(salt, null, null, false);
        assertThat(cipher.hash(A_SECRET_MESSAGE)).isEqualTo(hash.hash(A_SECRET_MESSAGE));
    }

    @Test
    public void testLargeDataEncryptionAndDecryption() {
        KeyPair keyPair = MS_RSA.generateKeyPair(MS_RSA.KEY_LENGTH_MINIMUM);

        // Sender received public key for encryption
        final String PUBLIC_KEY = MS_RSA.keyToBase64String(keyPair.getPublic());
        MS_Cipher sender = MS_CipherBuilder.aBuilder()
                .withAsymmetricEncryptionKeys(AsymmetricKeyPair.fromConstantStrings(null, PUBLIC_KEY))
                .build();
        Pair<String, String> encryptedDataActual = sender.encryptHybrid(A_SECRET_MESSAGE);

        // Receiver's part.
        // He has the private key and he will use it to decrypt symmetric key that will be also used to decrypt plain text
        final String PRIVATE_KEY = MS_RSA.keyToBase64String(keyPair.getPrivate());
        MS_Cipher receiver = MS_CipherBuilder.aBuilder()
                .withAsymmetricEncryptionKeys(AsymmetricKeyPair.fromConstantStrings(PRIVATE_KEY, null))
                .build();

        assertThat(receiver.decryptHybrid(encryptedDataActual.getKey(), encryptedDataActual.getValue())).isEqualTo(A_SECRET_MESSAGE);
        assertThat(receiver.decryptHybrid(encryptedDataActual)).isEqualTo(A_SECRET_MESSAGE);

        assertThatThrownBy(() -> {
            final String PRIVATE_KEY_WRONG = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKIkucAuBIxhw301XvIfPCxAy+BbYZYiOB3oRvxk+QTetFaehzTWi3iOQKShWlg9/UaCVnYtgtV229AukmPE09TS6zh6R6PgR2B+3CekdPUj5J0Nk6DCsaJmo9AcufhlJsjQwBLaZN/34yexnd2tt9ILn7msX4du+jHQrGP62YnVAgMBAAECgYAVbErKlInvTl945JtV3ECkDC+jxg8fuge2E+GFKYYpWY90Pl/Y4FvvIsRxvAvmytHzBfeMh7jYoWqrl+upmPDv4MP1I1G8amDuPQIXAQrjA5sJ8/pY7C8CJ5x2gz//qNiqGvB/PihRKp1OpSe8A+I67rQCUagKvtQ9XQUFQNPcRQJBANW+XncC0pApH0um3NMNpYLRKgRFK3/BThX4CAnuoQCphTSON9Mx+IzW8bx/CEVK5lK0IpLVA6hDYNvprdbrnp8CQQDCMtsoWLjyxb/i3xTb0EOF0Npz7+nRrcT3sV2XFLGg2cXxUrJMXQ2007SCi9zmKBH7Hf9LnOReHW3oLy0NwKcLAkA3gzrLlAsV7+g14L+HfQrYf/R2qXRTuOPL5uBHnBdqsZxr+ufazSathYHBIAkI3hwVrL0x/2r0v5MfaoCAyi8nAkEAvbxwcYAV22Sn8SJiP83AWxrOhN3PhkSQIC0TssvOLMj446oXeBKmlJkPUUkTX2+g+ce6Kgc0/Fsmiealzzz+NwJAa8OGIXwC46l8n3S+kBiTwvxCw/20nwmFXP7VUQjULaG71oRtMvByxeNe297R2rTuGdf2YMVDl5I4Hr77LFgk+g==";
            MS_Cipher receiverWrongKey = MS_CipherBuilder.aBuilder()
                    .withAsymmetricEncryptionKeys(AsymmetricKeyPair.fromConstantStrings(PRIVATE_KEY_WRONG, null))
                    .build();
            receiverWrongKey.decryptHybrid(encryptedDataActual);
        })
                .isInstanceOf(MS_BadSetupException.class)
                .hasMessage("Wrong private key provided for asymmetric decryption of given symmetric key")
                .hasCauseExactlyInstanceOf(BadPaddingException.class)
                .extracting("cause").element(0)
                .hasFieldOrPropertyWithValue("message", "Decryption error")
        ;

        assertThatThrownBy(() -> receiver.decryptHybrid(encryptedDataActual.getKey(), "This is not plain text we expected"))
                .isInstanceOf(MS_BadSetupException.class)
                .hasMessage("Wrong encrypted text provided")
                .hasCauseExactlyInstanceOf(IllegalArgumentException.class)
                .extracting("cause").element(0)
                .hasFieldOrPropertyWithValue("message", "Illegal base64 character 20")
        ;

        assertThatThrownBy(() -> receiver.decryptHybrid("This is not symmetric key we expected", encryptedDataActual.getValue()))
                .isInstanceOf(MS_BadSetupException.class)
                .hasMessage("Wrong encrypted text provided")
                .hasCauseExactlyInstanceOf(IllegalArgumentException.class)
                .extracting("cause").element(0)
                .hasFieldOrPropertyWithValue("message", "Illegal base64 character 20")
        ;
    }
}