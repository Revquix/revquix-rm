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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.revquix.sm.auth.enums.AuthenticationType;
import com.revquix.sm.auth.enums.ClientType;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: AuthIdentity.java
 */

/**
 * AuthIdentity represents the authenticated identity of a user or client in the system.
 * It contains details such as user ID, client ID, username, email, roles, scopes,
 * authentication type, and other relevant information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class AuthIdentity {

    private String userId;
    private String clientId;
    private String username;
    private String email;
    private String mobile;
    private String clientName;
    private List<String> roles;
    private List<String> scopes;
    private List<String> origins;
    private ClientType clientType;
    private Collection<? extends GrantedAuthority> authorities;
    private AuthenticationType authenticationType;
    private String remoteAddress;
    private List<String> providers;
    private boolean hasPassword;
    private Date lastPasswordChange;
}
