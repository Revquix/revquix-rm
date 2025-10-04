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
package com.revquix.sm.application.exception;

import lombok.Getter;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: ErrorData.java
 */

/**
 * Enum representing various error codes and their corresponding messages.
 */
@Getter
public enum ErrorData {

    USER_NOT_FOUND_ID("1001", "User not found with the given User Id"),
    USER_NOT_FOUND_EMAIL("1002", "User not found with the given Email"),
    USER_ALREADY_REGISTERED("1003", "User is already registered with given Email"),
    ROLE_NOT_FOUND_BY_ID("1004", "Role not found with the given Role Id"),
    UNABLE_TO_GENERATE_USERNAME("1005", "Unable to create username"),
    USER_NOT_FOUND("1006", "User not found with the given data"),
    INCORRECT_PASSWORD("1007", "Incorrect password entered. Please verify your credentials and try again."),
    USER_NOT_ENABLED("1008", "User is not enabled"),
    USER_ACCOUNT_LOCKED("1009", "User account is locked"),
    NO_USER_WITH_EMAIL("1010", "User not found with the given Email"),
    NO_USER_WITH_USERNAME("1011", "User not found with the given Username"),
    NO_USER_WITH_MOBILE("1012", "User not found with the given Mobile Number"),
    CLIENT_NOT_FOUND_ID("1013", "Client not found with the given Client Id"),
    INVALID_CLIENT_ID("1014", "Client Id is invalid"),
    CLIENT_CREDENTIALS_EXPIRED("1015", "Client credentials are expired"),
    INVALID_CLIENT_SECRET("1016", "Client secret is invalid"),
    CLIENT_STATUS_NOT_ACTIVE("1017", "Client status is not active"),
    CLIENT_ID_MANDATORY("1018", "Client Id is not present in the request"),
    CLIENT_SECRET_MANDATORY("1019", "Client secret is not present in the request"),
    PASSWORD_MANDATORY("1020", "Password is not present in the request"),
    ENTRYPOINT_MANDATORY("1021", "Please enter a valid email or username or mobile number to continue"),
    INVALID_EMAIL("1022", "Email is not valid"),
    INVALID_USERNAME("1023", "Username should be of 4 characters, starts with alphabets, can contain numbers in between and special characters (-, _)"),
    INVALID_MOBILE_NUMBER("1024", "Mobile number is not valid"),
    AUTHORITIES_MISSING("1025", "Not authorized to access the application"),
    TOKEN_EXPIRED("1026", "JWT Token is expired"),
    MALFORMED_TOKEN("1027", "JWT token is malformed"),
    REFRESH_TOKEN_NOT_ALLOWED("1028", "Refresh token is not allowed for accessing the resources"),
    OUTPUT_STREAM_ALREADY_SHOWN("1029", "Output stream is already shown"),
    FULL_AUTHENTICATION_IS_REQUIRED("1030", "User Authentication is required to access this resource"),
    REMOTE_ADDRESS_AUTHENTICATION_FAILED("1031", "Remote address authentication failed"),
    BLOGGIOS_ROLES_PROCESSOR_ERROR("1032", "Roles processor error occurred"),
    USER_ID_INVALID("1033", "User id is invalid"),
    USER_NOT_PRESENT_OTP("1034", "User is not present in the given OTP Request"),
    INVALID_OTP("1035", "Invalid OTP entered. Please verify and try again with the correct OTP."),
    USER_ALREADY_ENABLED("1036", "User is already enabled"),
    OTP_EXPIRED("1037", "OTP is expired. Please resend OTP to generate new OTP"),
    INTERNAL_ERROR("1038", "Internal error occurred at backend"),
    PASSWORD_FORMAT("1039", "Password format is incorrect"),
    NOT_LOGGED_IN("1040", "User not logged in"),
    REFRESH_TOKEN_EXCEPTION("1041", "Refresh token exception occurred. Please reach out to support"),
    ACCESS_TOKEN_NOT_PRESENT("1042", "Access token is not present in request header"),
    INVALID_REFRESH_TOKEN("1043", "Refresh token is invalid"),
    INVALID_ACCESS_TOKEN("1044", "Access token is invalid"),
    REFRESH_TOKEN_EXPIRED("1045", "Refresh token is expired"),
    INCORRECT_DATE("1052", "Incorrect date"),
    USER_NOT_LOGGED_IN("1059", "User is not logged in"),
    JSON_DESERIALIZATION("1060", "JSON deserialization error"),
    NO_OTP_PRESENT_FOR_RESEND("1061", "Resend OTP can only be used if OTP is sent at least once"),
    RESEND_OTP_LIMIT_EXCEED("1062", "Resend OTP limit exceeded"),
    INVALID_FACEBOOK_ACCESS_TOKEN("1063", "Facebook access token is invalid"),
    USER_AUTH_NULL_AUTHENTICATE_SSO("1064", "User Auth injected as null in the SSO Authentication"),
    GOOGLE_AUTH_TOKEN_EXCHANGE_ERROR("1065", "Google auth token exchange error"),
    NAME_REQUIRED("1066", "Name is mandatory and cannot be null"),
    INVALID_NAME_FORMAT("1067", "Name must be between 2 and 50 characters and contain only letters and spaces"),
    PROFILE_ALREADY_EXISTS("1068", "Profile already exists for the user"),
    PROFILE_NOT_FOUND("1069", "Profile not found for the user"),
    PROFILE_IMAGE_EMPTY("1070", "Profile image file is empty"),
    INVALID_IMAGE_TYPE("1071", "Invalid image format. Only JPEG and PNG are allowed"),
    PROFILE_IMAGE_SIZE_EXCEEDED("1072", "Profile image size exceeded the limit"),
    DIGEST_ERROR("1073", "Error generating image digest"),
    ERROR_DURING_S3_IMAGE_UPLOAD("1074", "Error occurred during S3 image upload"),
    INVALID_REGISTER_ROLE("1075", "Role must be either 'USER' or 'DOCTOR'"),
    USER_ALREADY_REGISTERED_AS_DOCTOR("1076", "User is already registered as a doctor"),
    PLEASE_ENTER_CORRECT_PASSWORD_TO_SWITCH_TO_DOCTOR_ROLE("1077", "Please enter correct password to switch to doctor role"),
    FIRST_NAME_REQUIRED("1078", "First name is mandatory and cannot be null"),
    DOB_CANNOT_FUTURE("1079", "Date of birth cannot be in the future"),
    DOCTOR_MUST_BE_OLDER_THAN_18("1080", "Doctor must be older than 18 years"),
    BIO_LENGTH_INVALID("1081", "Bio must be between 10 and 1000 characters"),
    DOCTOR_PROFILE_NOT_FOUND("1082", "Doctor profile not found for the user"),
    DOCTOR_PROFILE_IMAGE_EMPTY("1083", "Doctor profile image file is empty"),
    DOCTOR_PROFILE_IMAGE_SIZE_EXCEEDED("1084", "Doctor profile image size exceeded the limit"),
    DOCTOR_CERTIFICATE_IMAGE_EMPTY("1085", "Doctor certificate image file is empty"),
    DOCTOR_CERTIFICATE_IMAGE_SIZE_EXCEEDED("1086", "Doctor certificate image size exceeded the limit"),
    FAILED_TO_GENERATE_SEQUENCE("1087", "Failed to generate the Sequence"),
    FAILED_TO_SEND_MAIL_API_ERROR("1088", "Zepto mail API error while sending the mail"),
    EXCEPTION_WHILE_FETCHING_PRIVATE_KEY("1089", "Exception occurred while fetching private key"),
    EXCEPTION_WHILE_FETCHING_PUBLIC_KEY("1090", "Exception occurred while fetching public key"),;

    private static final String PREFIX = "SANA-ERROR-";

    private final String code;
    private final String message;

    ErrorData(String code, String message) {
        this.code = PREFIX + code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

