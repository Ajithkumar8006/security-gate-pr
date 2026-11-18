package com.stockroom;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

// Temporary CodeQL gate fixture; remove it after testing and do not merge it.
final class TemporaryCodeQlGateFixture {
    private TemporaryCodeQlGateFixture() {
    }

    static Cipher weakCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance("DES");
    }
}
