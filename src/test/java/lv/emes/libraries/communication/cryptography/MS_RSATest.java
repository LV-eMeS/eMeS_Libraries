package lv.emes.libraries.communication.cryptography;

import lv.emes.libraries.tools.MS_BadSetupException;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MS_RSATest {

    private static final PublicKey PUBLIC_KEY = MS_RSA.convertPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCiJLnALgSMYcN9NV7yHzwsQMvgW2GWIjgd6Eb8ZPkE3rRWnoc01ot4jkCkoVpYPf1GglZ2LYLVdtvQLpJjxNPU0us4ekej4EdgftwnpHT1I+SdDZOgwrGiZqPQHLn4ZSbI0MAS2mTf9+MnsZ3drbfSC5+5rF+Hbvox0Kxj+tmJ1QIDAQAB");
    private static final PrivateKey PRIVATE_KEY = MS_RSA.convertPrivateKey("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKIkucAuBIxhw301XvIfPCxAy+BbYZYiOB3oRvxk+QTetFaehzTWi3iOQKShWlg9/UaCVnYtgtV229AukmPE09TS6zh6R6PgR2B+3CekdPUj5J0Nk6DCsaJmo9AcufhlJsjQwBLaZN/34yexnd2tt9ILn7msX4du+jHQrGP62YnVAgMBAAECgYAVbErKlInvTl945JtV3ECkDC+jxg8fuge2E+GFKYYpWY90Pl/Y4FvvIsRxvAvmytHzBfeMh7jYoWqrl+upmPDv4MP1I1G8amDuPQIXAQrjA5sJ8/pY7C8CJ5x2gz//qNiqGvB/PihRKp1OpSe8A+I67rQCUagKvtQ9XQUFQNPcRQJBANW+XncC0pApH0um3NMNpYLRKgRFK3/BThX4CAnuoQCphTSON9Mx+IzW8bx/CEVK5lK0IpLVA6hDYNvprdbrnp8CQQDCMtsoWLjyxb/i3xTb0EOF0Npz7+nRrcT3sV2XFLGg2cXxUrJMXQ2007SCi9zmKBH7Hf9LnOReHW3oLy0NwKcLAkA3gzrLlAsV7+g14L+HfQrYf/R2qXRTuOPL5uBHnBdqsZxr+ufazSathYHBIAkI3hwVrL0x/2r0v5MfaoCAyi8nAkEAvbxwcYAV22Sn8SJiP83AWxrOhN3PhkSQIC0TssvOLMj446oXeBKmlJkPUUkTX2+g+ce6Kgc0/Fsmiealzzz+NwJAa8OGIXwC46l8n3S+kBiTwvxCw/20nwmFXP7VUQjULaG71oRtMvByxeNe297R2rTuGdf2YMVDl5I4Hr77LFgk+g==");
    private static final String TEXT = "Default text that is going to be encrypted.";

    @Test
    public void testKeyGenerationAndConversions() {
        KeyPair keys = MS_RSA.generateKeyPair(MS_RSA.KEY_LENGTH_MINIMUM);
        String publicKey = MS_RSA.keyToBase64String(keys.getPublic());
        String privateKey = MS_RSA.keyToBase64String(keys.getPrivate());
        PublicKey convertedPublicKey = MS_RSA.convertPublicKey(publicKey);
        PrivateKey convertedPrivateKey = MS_RSA.convertPrivateKey(privateKey);

        assertThat(convertedPublicKey.getAlgorithm()).isEqualTo(keys.getPublic().getAlgorithm());
        assertThat(convertedPublicKey.getFormat()).isEqualTo(keys.getPublic().getFormat());
        assertThat(convertedPublicKey.getEncoded()).isEqualTo(keys.getPublic().getEncoded());

        assertThat(convertedPrivateKey.getAlgorithm()).isEqualTo(keys.getPrivate().getAlgorithm());
        assertThat(convertedPrivateKey.getFormat()).isEqualTo(keys.getPrivate().getFormat());
        assertThat(convertedPrivateKey.getEncoded()).isEqualTo(keys.getPrivate().getEncoded());
    }

    @Test
    public void testGenerateReturnsDifferentKeysEveryNextTime() {
        KeyPair keys = MS_RSA.generateKeyPair(MS_RSA.KEY_LENGTH_MINIMUM);
        String publicKey = MS_RSA.keyToBase64String(keys.getPublic());
        String privateKey = MS_RSA.keyToBase64String(keys.getPrivate());

        keys = MS_RSA.generateKeyPair(MS_RSA.KEY_LENGTH_MINIMUM);
        assertThat(MS_RSA.keyToBase64String(keys.getPublic())).isNotEqualTo(publicKey);
        assertThat(MS_RSA.keyToBase64String(keys.getPrivate())).isNotEqualTo(privateKey);
    }

    @Test
    public void testValidEncryption() throws IllegalBlockSizeException {
        String encText = MS_RSA.forEncryption(PUBLIC_KEY).encrypt(TEXT);
        String decrcText = MS_RSA.forDecryption(PRIVATE_KEY).decrypt(encText);
        assertThat(decrcText).isEqualTo(TEXT);
    }

    @Test
    public void testInvalidKey() throws Exception {
        String encText = MS_RSA.forEncryption(PUBLIC_KEY).encrypt(TEXT);
        PrivateKey wrongKeyForDecryption = MS_RSA.generateKeyPair(MS_RSA.KEY_LENGTH_MINIMUM).getPrivate();
        assertThatThrownBy(() -> MS_RSA.forDecryption(wrongKeyForDecryption).decrypt(encText))
                .isInstanceOf(MS_BadSetupException.class)
                .hasCauseExactlyInstanceOf(BadPaddingException.class)
                .extracting("cause").element(0)
                .hasFieldOrPropertyWithValue("message", "Decryption error");
    }

    @Test
    public void testIdenticalTextsNotEqualWhenEncrypted() throws IllegalBlockSizeException {
        String encText = MS_RSA.forEncryption(PUBLIC_KEY).encrypt(TEXT);
        String encText2 = MS_RSA.forEncryption(PUBLIC_KEY).encrypt(TEXT);
        assertThat(encText2).isNotEqualTo(encText);
        assertThat(MS_RSA.forDecryption(PRIVATE_KEY).decrypt(encText)).isEqualTo(TEXT);
        assertThat(MS_RSA.forDecryption(PRIVATE_KEY).decrypt(encText2)).isEqualTo(TEXT);
    }

    @Test
    public void testSigningAndVerification() throws IllegalBlockSizeException {
        String signature = MS_RSA.forEncryption(PRIVATE_KEY).encrypt(TEXT);
        assertThat(MS_RSA.forDecryption(PUBLIC_KEY).decrypt(signature)).isEqualTo(TEXT);

        // Test that it's still possible to act opposite way
        assertThat(MS_RSA.forDecryption(PRIVATE_KEY).decrypt(MS_RSA.forEncryption(PUBLIC_KEY).encrypt(TEXT))).isEqualTo(TEXT);
    }
}