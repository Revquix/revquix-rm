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
import com.revquix.sm.application.annotation.impl.RevquixIdGeneratorListener;
import com.revquix.sm.application.annotation.RevquixId;
import com.revquix.sm.application.constants.ModelConstants;
import com.revquix.sm.application.utils.ModelPayload;
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
 * File: UserAuth.java
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(
        name = ModelConstants.USER_AUTH,
        schema = ModelConstants.AUTH_SCHEMA,
        indexes = {
                @Index(
                        name = "index_userauth_userId",
                        columnList = "userId"
                ),
                @Index(
                        name = "index_userauth_sanaUID",
                        columnList = "revquixUID"
                ),
                @Index(
                        name = "index_userauth_email",
                        columnList = "email"
                ),
                @Index(
                        name = "index_userauth_username",
                        columnList = "username"
                )
        }
)
@ToString
@EntityListeners(RevquixIdGeneratorListener.class)
public class UserAuth extends ModelPayload<UserAuth> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @RevquixId(
            prefix = "UA",
            sequence = ModelConstants.USER_UID_SEQUENCE,
            length = 7
    )
    @Column(unique = true, nullable = false)
    private String revquixUID;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;
    private String mobile;

    private String password;
    private Boolean isEnabled;
    private Boolean isAccountNonLocked;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> authProvider;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPasswordChange;

    private String registerIp;
    private String lastLoginIp;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            schema = ModelConstants.AUTH_SCHEMA,
            joinColumns = @JoinColumn(name = "UserAuth", referencedColumnName = "userId"),
            inverseJoinColumns = @JoinColumn(name = "Role", referencedColumnName = "roleId")
    )
    @Builder.Default
    private List<Role> roles = new ArrayList<>();
}
