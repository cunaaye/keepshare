package com.hanhuy.android.keepshare.tests;

import android.content.ComponentName;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import com.hanhuy.android.keepshare.*;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;

/** in order for any of these tests to succeed, the app must have been run
  *  at least once with an account selected/granted access to Google Drive
  */
public class KeyManagerTest extends ActivityUnitTestCase {
    final static Intent ACTIVITY = Intent.makeMainActivity(
            new ComponentName("com.hanhuy.android.keepshare", ".MainActivity"));

    static String localKey = null;
    public KeyManagerTest() {
        super(SetupActivity.class);
    }

    public void testLoadKey() {
        startActivity(ACTIVITY, null, null);
        assertNotNull(getActivity());
        Settings settings = new Settings(getActivity());
        KeyManager km = new KeyManager(getActivity(), settings);
        SecretKey key = km.loadKey().get();
        assertNotNull("Key cannot be null", key);
        assertEquals("improper key length", 32, key.getEncoded().length);
    }

    public void testLocalKey() {
        startActivity(ACTIVITY, null, null);
        assertNotNull(getActivity());
        Settings settings = new Settings(getActivity());
        KeyManager km = new KeyManager(getActivity(), settings);
        km.loadKey();
        if (km.localKey().isLeft()) {
            if (KeyError.NeedPin$.MODULE$ != km.localKey().left().get())
                fail("is left: " + km.localKey().left());
            return;
        }
        SecretKey k = km.localKey().right().get();
        assertNotNull("key cannot be null", k);
        String hexed = KeyManager.hex(k.getEncoded());
        assertNotNull("cannot be null", hexed);
        // repeated test will verify localKey != null clause
        if (localKey != null)
            assertEquals("Keys must match", localKey, hexed);
        else
            localKey = hexed;
    }

    public void testLocalKey2() { // second time must be same
        testLocalKey();
    }
}
