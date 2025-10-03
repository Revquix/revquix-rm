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
import com.revquix.sm.application.constants.ModelConstants;
import com.revquix.sm.auth.enums.ClientStatus;
import com.revquix.sm.auth.enums.ClientType;
import com.revquix.sm.auth.enums.EnvironmentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: ClientAuth.java
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(
        name = ModelConstants.CLIENT_TABLE,
        schema = ModelConstants.AUTH_SCHEMA,
        indexes = {
                @Index(
                        name = "idx_clientauth_clientid",
                        columnList = "clientId"
                )
        }
)
public class ClientAuth {

    @Id
    private String clientId;


    private String clientName;

    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    @Enumerated(EnumType.STRING)
    private EnvironmentType environmentType;

    private String clientSecret;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> origins;

    @Enumerated(EnumType.STRING)
    private ClientStatus clientStatus;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateGenerated;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            schema = ModelConstants.AUTH_SCHEMA,
            joinColumns = @JoinColumn(name = "ClientAuth", referencedColumnName = "clientId"),
            inverseJoinColumns = @JoinColumn(name = "Scope", referencedColumnName = "scopeId")
    )
    @Builder.Default
    private List<Scope> scopes = new ArrayList<>();
}