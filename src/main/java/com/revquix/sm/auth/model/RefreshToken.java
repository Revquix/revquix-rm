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
package com.revquix.sm.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.revquix.sm.application.constants.ModelConstants;
import com.revquix.sm.auth.enums.AuthenticationType;
import com.revquix.sm.auth.enums.RefreshTokenStatus;
import com.revquix.sm.auth.enums.UserLoginProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: RefreshToken.java
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Table(
        name = ModelConstants.REFRESH_TOKEN_TABLE,
        schema = ModelConstants.AUTH_SCHEMA
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String refreshTokenId;

    @Column(nullable = false, unique = true)
    private String jti;

    private String clientId;
    private String userId;

    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateGenerated;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @Enumerated(EnumType.STRING)
    private RefreshTokenStatus refreshTokenStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode ssoTokenData;

    @Enumerated(EnumType.STRING)
    private UserLoginProvider userLoginProvider;
}
