package com.stockroom;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Temporary CodeQL gate fixture; remove it after testing and do not merge it.
final class TemporaryCodeQlGateFixture {
    private TemporaryCodeQlGateFixture() {
    }

    static byte[] weakDigest(byte[] value) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5").digest(value);
    }
}
