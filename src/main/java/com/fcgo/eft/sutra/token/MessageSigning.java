package com.fcgo.eft.sutra.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;

@Component
public class MessageSigning {
    @Value("${nchl.npi.pfx.path}")
    private String pfxCertificate;
    @Value("${nchl.npi.pfx.password}")
    private String password;

    public String getHashValue(String str) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(Files.newInputStream(Paths.get(pfxCertificate)), password.toCharArray());
            String alias = keyStore.aliases().nextElement();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyStore.getKey(alias, password.toCharArray());
            byte[] signature = sign(privateKey, str.getBytes());
            return Base64.getEncoder().encodeToString(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] sign(PrivateKey pk, byte[] data) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(pk);
        signature.update(data);
        return signature.sign();
    }
}
