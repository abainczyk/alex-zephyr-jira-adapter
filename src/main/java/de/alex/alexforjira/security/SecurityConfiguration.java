/*
 * Copyright 2018 Alexander Bainczyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.alex.alexforjira.security;

import de.alex.alexforjira.api.users.PasswordEncoderProvider;
import de.alex.alexforjira.api.users.UserService;
import de.alex.alexforjira.api.users.entities.UserRole;
import de.alex.alexforjira.db.h2.tables.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for HTTP Basic Auth.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfiguration(final UserService userService,
                                 final PasswordEncoderProvider passwordEncoderProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoderProvider.getEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new AuthenticationProvider() {

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                final String email = authentication.getPrincipal().toString();
                final String password = authentication.getCredentials().toString();

                // check if the combination email / password exists
                final User user = userService.findByEmail(email);
                final boolean hasCorrectCredentials = passwordEncoder.matches(
                        String.valueOf(authentication.getCredentials()),
                        user.getHashedPassword());

                if (!hasCorrectCredentials) {
                    throw new BadCredentialsException("Invalid combination of email/password");
                }

                // Prefix role with "ROLE_" so that the URL matcher works correctly
                final List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

                return new UsernamePasswordAuthenticationToken(email, password, authorities);
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return authentication.equals(UsernamePasswordAuthenticationToken.class);
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/resources/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/rest/status").permitAll()
                .antMatchers("/rest/users/signIn").permitAll()
                .antMatchers("/rest/admin/**").hasRole(UserRole.ADMIN.toString())
                .antMatchers("/rest/**").authenticated()
                .and()
                .httpBasic();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // disable authorization for the start page and for incoming webhooks.
        web.ignoring()
                .antMatchers("/")
                .antMatchers("/rest/wh/**");
    }
}
