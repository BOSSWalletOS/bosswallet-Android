package com.bosswallet.app.repository;

public class KeyProviderFactory
{
    public static KeyProvider get() {
        return new KeyProviderJNIImpl();
    }
}
