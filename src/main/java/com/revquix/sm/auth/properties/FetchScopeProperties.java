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
package com.revquix.sm.auth.properties;

import com.revquix.sm.auth.payload.ScopePayload;
import com.revquix.sm.application.utils.YmlFileMapParserFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: FetchScopeProperties.java
 */

/**
 * FetchScopeProperties is a configuration properties class that loads scope-related data
 * from a YAML file and maps it to a dictionary of ScopePayload objects.
 */
@Configuration
@ConfigurationProperties(prefix = "scopes")
@PropertySource(
        value = "classpath:configuration/scope-data.yml",
        factory = YmlFileMapParserFactory.class
)
@Getter
@Setter
public class FetchScopeProperties {

    public Map<String, ScopePayload> data = new HashMap<>();
}
