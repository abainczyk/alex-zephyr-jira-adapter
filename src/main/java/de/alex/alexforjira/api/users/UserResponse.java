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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.alex.alexforjira.db.h2.tables.pojos.User;

/**
 * Wrapper class that removes the password from
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends User {

    @JsonIgnore
    @Override
    public String getHashedPassword() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setHashedPassword(String hashedPassword) {
    }

    static UserResponse fromUser(User user) {
        final UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setRole(user.getRole());
        return res;
    }
}
