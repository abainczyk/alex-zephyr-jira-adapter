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

package de.alex.alexforjira.api.users;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** The provider for the password encoder. */
@Service
public class PasswordEncoderProvider {

    private final PasswordEncoder passwordEncoder;

    public PasswordEncoderProvider() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public PasswordEncoder getEncoder() {
        return passwordEncoder;
    }
}
