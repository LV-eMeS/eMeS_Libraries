package lv.emes.libraries.communication.cryptography.hybrid;

import lv.emes.libraries.communication.cryptography.MS_RSA;
import org.junit.Test;

import java.security.KeyPair;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AsymmetricKeyPairTest {

    @Test
    public void testBothConstructionWays() {
        KeyPair initialKeyPair = MS_RSA.generateKeyPair(MS_RSA.KEY_LENGTH_MINIMUM);
        String actualPrivateKey = MS_RSA.keyToBase64String(initialKeyPair.getPrivate());
        String actualPublicKey = MS_RSA.keyToBase64String(initialKeyPair.getPublic());

        AsymmetricKeyPair fromKeyPair = AsymmetricKeyPair.fromKeyPair(initialKeyPair);
        AsymmetricKeyPair fromConstantStrings = AsymmetricKeyPair.fromConstantStrings(actualPrivateKey, actualPublicKey);

        assertThat(fromKeyPair.getPrivateKey()).isNotNull().isEqualTo(fromConstantStrings.getPrivateKey());
        assertThat(fromKeyPair.getPublicKey()).isNotNull().isEqualTo(fromConstantStrings.getPublicKey());
        assertThat(fromKeyPair.getKeys()).isNotNull();
        assertThat(fromConstantStrings.getKeys()).isNotNull();
        assertThat(fromKeyPair.getKeys().getPrivate()).isNotNull();
        assertThat(fromConstantStrings.getKeys().getPrivate()).isNotNull();
        assertThat(fromKeyPair.getKeys().getPublic()).isNotNull();
        assertThat(fromConstantStrings.getKeys().getPublic()).isNotNull();
        assertThat(fromKeyPair.getKeys().getPrivate().getEncoded()).isEqualTo(fromConstantStrings.getKeys().getPrivate().getEncoded());
        assertThat(fromKeyPair.toString()).isEqualTo(fromConstantStrings.toString());
    }

    @Test
    public void testToString() {
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKUJZ93JHaPo/6Fb30rI05v5LX5esIfOeGjNhynhnkQhBWDqetXmZRWJ8qfzrA8GmI4HGUzgZf2DwTctY3KgQaAIiZVb9kEjeGe5/rdmCiL8/c1L5TjqduywR0RP3GG3sriecISy/yqAHBhskBWLpoCWFEs2Uu1f1gXnfMTU1rxtAgMBAAECgYAiaA6WZ1pdlLLOkhfAQJQVPWKlqNoGxh3GQ6r9KUNUksnLRbcsJudGEUdcimmBjG97lLFKnLHGo5RBi53jBNQJUOWCZbAbznWV/GovZTTimUyFDGgxavM/MVqFHEyGes+8w99rAF2J7qSTB52sDLr/vsmQOMlvdfptb0jDKWSEwQJBAPgHL1L8qXMwVI/Eucxz5RUlC9OKrIRKM29disR3RYagYpR2x5hgA8vy9qBJVnthOASpdebIdbRJsYpyCsEyXf0CQQCqV1rTqNP0O3m2CW22lgoTjqoIJ1NTCDP8leNP5Nez3mMWSDqugCexgmGXzOOOH9ar4vRLcafHPj7vHBsvsmsxAkArFVRrHehxUdvefVWo5hjM63p6bIQ7FoiIt777EZzKHeIB2AAjv8npC3M7tIGJPQH2DVmllPydLI/idJzZol4hAkEAg2s11cqRDAK+2iTYEmAbkg8lO3krngncIel6IG+Lw2e1xsEfFPYmqZrPtJAMl+AUfTRyCaq8KMnrQArNJrVeYQJBAIxHZC5GJ3LcFlkZgxukXOGLtK3O8Y9j95nsOkLrdpKkWsYFuGtqrjGMjKzp9V3TzNE7iPr0wVrH4FT3AJAlL6s=";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClCWfdyR2j6P+hW99KyNOb+S1+XrCHznhozYcp4Z5EIQVg6nrV5mUVifKn86wPBpiOBxlM4GX9g8E3LWNyoEGgCImVW/ZBI3hnuf63Zgoi/P3NS+U46nbssEdET9xht7K4nnCEsv8qgBwYbJAVi6aAlhRLNlLtX9YF53zE1Na8bQIDAQAB";
        AsymmetricKeyPair asymmetricKeyPair = AsymmetricKeyPair.fromConstantStrings(privateKey, publicKey);
        assertThat(asymmetricKeyPair.toString()).isEqualTo("{\"privateKey\":\"MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKUJZ93JHaPo/6Fb30rI05v5LX5esIfOeGjNhynhnkQhBWDqetXmZRWJ8qfzrA8GmI4HGUzgZf2DwTctY3KgQaAIiZVb9kEjeGe5/rdmCiL8/c1L5TjqduywR0RP3GG3sriecISy/yqAHBhskBWLpoCWFEs2Uu1f1gXnfMTU1rxtAgMBAAECgYAiaA6WZ1pdlLLOkhfAQJQVPWKlqNoGxh3GQ6r9KUNUksnLRbcsJudGEUdcimmBjG97lLFKnLHGo5RBi53jBNQJUOWCZbAbznWV/GovZTTimUyFDGgxavM/MVqFHEyGes+8w99rAF2J7qSTB52sDLr/vsmQOMlvdfptb0jDKWSEwQJBAPgHL1L8qXMwVI/Eucxz5RUlC9OKrIRKM29disR3RYagYpR2x5hgA8vy9qBJVnthOASpdebIdbRJsYpyCsEyXf0CQQCqV1rTqNP0O3m2CW22lgoTjqoIJ1NTCDP8leNP5Nez3mMWSDqugCexgmGXzOOOH9ar4vRLcafHPj7vHBsvsmsxAkArFVRrHehxUdvefVWo5hjM63p6bIQ7FoiIt777EZzKHeIB2AAjv8npC3M7tIGJPQH2DVmllPydLI/idJzZol4hAkEAg2s11cqRDAK+2iTYEmAbkg8lO3krngncIel6IG+Lw2e1xsEfFPYmqZrPtJAMl+AUfTRyCaq8KMnrQArNJrVeYQJBAIxHZC5GJ3LcFlkZgxukXOGLtK3O8Y9j95nsOkLrdpKkWsYFuGtqrjGMjKzp9V3TzNE7iPr0wVrH4FT3AJAlL6s=\",\"publicKey\":\"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClCWfdyR2j6P+hW99KyNOb+S1+XrCHznhozYcp4Z5EIQVg6nrV5mUVifKn86wPBpiOBxlM4GX9g8E3LWNyoEGgCImVW/ZBI3hnuf63Zgoi/P3NS+U46nbssEdET9xht7K4nnCEsv8qgBwYbJAVi6aAlhRLNlLtX9YF53zE1Na8bQIDAQAB\"}");
    }
}