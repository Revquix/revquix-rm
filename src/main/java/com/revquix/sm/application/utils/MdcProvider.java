package com.revquix.sm.application.utils;

/*
  Developer: Rohit Parihar
  Project: revquix-sm
  GitHub: github.com/rohit-zip
  File: MdcProvider
 */

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@UtilityClass
public class MdcProvider {

    public static String getBreadcrumbId() {
        String breadcrumbId = MDC.get("breadcrumbId");
        if (breadcrumbId == null) {
            return "";
        } else {
            return breadcrumbId;
        }
    }
}
