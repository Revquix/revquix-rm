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
package com.revquix.sm.application.annotation.impl;

/*
  Developer: Rohit Parihar
  Project: ap-payment-service
  GitHub: github.com/rohit-zip
  File: CustomIdGeneratorListener
 */

import com.revquix.sm.application.annotation.RevquixId;
import com.revquix.sm.application.exception.ErrorData;
import com.revquix.sm.application.exception.InternalServerException;
import com.revquix.sm.application.utils.SequenceEntityContextBridge;
import jakarta.persistence.PrePersist;

import java.lang.reflect.Field;

public class RevquixIdGeneratorListener {

    /* This method is called before an entity is persisted.
       It checks for fields annotated with @GeneratedCustomId and generates a custom ID if the field is null.
     */
    @PrePersist
    public void generateCustomId(Object entity) {
        Class<?> clazz = entity.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(RevquixId.class)) {
                try {
                    field.setAccessible(true);
                    Object currentValue = field.get(entity);
                    if (currentValue != null) continue;

                    RevquixId annotation = field.getAnnotation(RevquixId.class);
                    String prefix = annotation.prefix();
                    String sequence = annotation.sequence();
                    int length = annotation.length();

                    Long nextVal = SequenceEntityContextBridge.getSequenceService()
                            .getNextSequenceValue(sequence);

                    String formatted = String.format("%s%0" + length + "d", prefix, nextVal);
                    field.set(entity, formatted);

                } catch (IllegalAccessException e) {
                    throw new InternalServerException(ErrorData.FAILED_TO_GENERATE_SEQUENCE);
                }
            }
        }
    }
}
