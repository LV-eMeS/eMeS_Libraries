package lv.emes.libraries.communication.cryptography.hybrid;

import lv.emes.libraries.tools.MS_BadSetupException;

/**
 * {@link MS_Cipher} builder.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.4.2.
 */
public final class MS_CipherBuilder {

    private final MS_Cipher template;

    private MS_CipherBuilder() {
        template = new MS_Cipher();
    }

    public static MS_CipherBuilder aBuilder() {
        return new MS_CipherBuilder();
    }

    public MS_CipherBuilder withAsymmetricEncryptionKeys(AsymmetricKeyPair encryptionKeys) {
        template.setAsymmetricEncryptionKeys(encryptionKeys);
        return this;
    }

    public MS_CipherBuilder withSignatureKeys(AsymmetricKeyPair signatureKeys) {
        template.setSignatureKeys(signatureKeys);
        return this;
    }

    public MS_CipherBuilder withSymmetricKeyAttributes(SymmetricKeyAttributes symmetricKeyAttributes) {
        template.setSymmetricKeyAttributes(symmetricKeyAttributes);
        return this;
    }

    public MS_CipherBuilder withHashingAttributes(HashingAttributes hashingAttributes) {
        template.setHashingAttributes(hashingAttributes);
        return this;
    }

    private void verifyAsymmetricKeyPairMandatoryFields(AsymmetricKeyPair keys) {
        if (keys != null) {
            if (keys.getKeys() == null && (keys.getPrivateKey() == null && keys.getPublicKey() == null))
                throw new MS_BadSetupException("Both private or public keys or at least one of them must be provided either as constant strings or KeyPair object");
        }
    }

    public MS_Cipher build() throws MS_BadSetupException {
        AsymmetricKeyPair encryptionKeys = template.getAsymmetricEncryptionKeys();
        AsymmetricKeyPair signatureKeys = template.getSignatureKeys();
        SymmetricKeyAttributes symmetricKeyAttributes = template.getSymmetricKeyAttributes();

        if (encryptionKeys == null && signatureKeys == null
                && symmetricKeyAttributes == null && template.getHashingAttributes() == null)
            throw new MS_BadSetupException("Nothing to build - at least one of Cipher attributes must be present");

        if (symmetricKeyAttributes != null && symmetricKeyAttributes.getSecretKey() == null) {
            throw new MS_BadSetupException("Symmetric key must be provided"); // However HMAC key is optional
        }

        verifyAsymmetricKeyPairMandatoryFields(encryptionKeys);
        verifyAsymmetricKeyPairMandatoryFields(signatureKeys);

        template.init();

        return template;
    }
}
