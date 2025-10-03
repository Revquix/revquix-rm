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

import com.revquix.sm.auth.model.Role;
import com.revquix.sm.auth.model.UserAuth;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Developer: Rohit Parihar
 * Project: bloggios-matching
 * GitHub: github.com/rohit-zip
 * File: UserPrincipal.java
 */

/**
 * UserPrincipal implements UserDetails to provide user information to Spring Security.
 * It includes user ID, username, email, mobile number, password, account status,
 * and authorities (roles/permissions).
 */
@Getter
@Setter
@NoArgsConstructor
public class UserPrincipal implements UserDetails {

    private String userId;
    private String bloggiosUsername;
    private String email;
    private String mobile;
    private String password;
    private Boolean isEnabled;
    private Boolean isAccountNonLocked;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(String userId, String bloggiosUsername, String email, String mobile, String password, Boolean isEnabled, Boolean isAccountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.bloggiosUsername = bloggiosUsername;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.isEnabled = isEnabled;
        this.isAccountNonLocked = isAccountNonLocked;
        this.authorities = authorities;
    }

    public static UserPrincipal create(UserAuth userAuth) {
        List<Role> roles = userAuth.getRoles();
        List<SimpleGrantedAuthority> grantedAuthorities = roles.stream().map(e -> new SimpleGrantedAuthority(e.getRole())).toList();
        return new UserPrincipal(
                userAuth.getUserId().toString(),
                userAuth.getUsername(),
                userAuth.getEmail(),
                userAuth.getMobile(),
                userAuth.getPassword(),
                userAuth.getIsEnabled(),
                userAuth.getIsAccountNonLocked(),
                grantedAuthorities
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
