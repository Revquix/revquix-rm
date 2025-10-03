/**
 * Proprietary License Agreement
 * <p>
 * Copyright (c) 2025 Revquix
 * <p>
 * This software is the confidential and proprietary property of Revquix and is provided under a
 * license, not sold. The application owner is Rohit Parihar and Revquix. Only authorized
 * Revquix administrators are permitted to copy, modify, distribute, or sublicense this software
 * under the terms set forth in this agreement.
 * <p>
 * Restrictions
 *
 * You are expressly prohibited from:
 * 1. Copying, modifying, distributing, or sublicensing this software without the express
 *    written permission of Rohit Parihar or Revquix.
 * 2. Reverse engineering, decompiling, disassembling, or otherwise attempting to derive
 *    the source code of the software.
 * 3. Altering or modifying the terms of this license without prior written approval from
 *    Rohit Parihar and Revquix administrators.
 * <p>
 * Disclaimer of Warranties:
 * This software is provided "as is" without any warranties, express or implied. Revquix makes
 * no representations or warranties regarding the software, including but not limited to any
 * warranties of merchantability, fitness for a particular purpose, or non-infringement.
 * <p>
 * For inquiries regarding licensing, please contact: support@Revquix.com.
 */
package com.revquix.sm.auth.authentication;

import com.revquix.sm.application.constants.ServiceConstants;
import com.revquix.sm.application.exception.InternalServerException;
import com.revquix.sm.auth.properties.AuthenticationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: KeyProvider.java
 */

@Component
@RequiredArgsConstructor
public class KeyProvider {

    private final AuthenticationProperties authenticationProperties;

    public PrivateKey getPrivateKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ServiceConstants.PKCS12);
            AuthenticationProperties.KeyProvider keyProvider = authenticationProperties.getJwt().getKeyProvider();
            try (InputStream keyStoreInputStream = getClass().getResourceAsStream(keyProvider.getKeyStorePath())) {
                keyStore.load(keyStoreInputStream, keyProvider.getKeyStorePassword().toCharArray());
            }
            return (PrivateKey) keyStore.getKey(keyProvider.getKeyAlias(), keyProvider.getKeyStorePassword().toCharArray());
        } catch (Exception exception) {
            throw new InternalServerException(
                    exception.getMessage(),
                    exception.getCause(),
                    exception.getLocalizedMessage(),
                    exception.getClass().getName()
            );
        }
    }

    public PublicKey getPublicKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ServiceConstants.PKCS12);
            AuthenticationProperties.KeyProvider keyProvider = authenticationProperties.getJwt().getKeyProvider();
            try (InputStream keyStoreInputStream = getClass().getResourceAsStream(keyProvider.getKeyStorePath())) {
                keyStore.load(keyStoreInputStream, keyProvider.getKeyStorePassword().toCharArray());
            }
            Certificate certificate = keyStore.getCertificate(keyProvider.getKeyAlias());
            return certificate.getPublicKey();
        } catch (Exception exception) {
            throw new InternalServerException(
                    exception.getMessage(),
                    exception.getCause(),
                    exception.getLocalizedMessage(),
                    exception.getClass().getName()
            );
        }
    }
}
