/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME.com, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 * Created on 28.01.2014 by thor
 */
package org.knime.core.util.crypto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Testcases for {@link Encrypter}.
 *
 * @author Thorsten Meinl, KNIME.com, Zurich, Switzerland
 */
public class EncrypterTest {
    /**
     * Test whether encryption followed by decryption produce the original value;
     * @throws Exception
     */
    @Test
    public void testRoundtripping() throws Exception {
        IEncrypter enc = new Encrypter("AKeyForTestingTheEncrypter");

        String text = null;
        String encryptedText = enc.encrypt(text);
        assertThat("Unexpected decrypted value", enc.decrypt(encryptedText), is(text));

        text = "";
        encryptedText = enc.encrypt(text);
        assertThat("Unexpected decrypted value", enc.decrypt(encryptedText), is(text));

        text = "äüößjjjhdshvoihgudfhgdfbv";
        encryptedText = enc.encrypt(text);
        assertThat("Unexpected decrypted value", enc.decrypt(encryptedText), is(text));
    }
}
