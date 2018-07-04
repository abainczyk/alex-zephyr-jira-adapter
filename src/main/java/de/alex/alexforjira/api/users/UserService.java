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

import de.alex.alexforjira.api.users.entities.UserCredentials;
import de.alex.alexforjira.api.users.entities.UserRole;
import de.alex.alexforjira.db.h2.tables.pojos.User;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data access for users.
 */
@Service
public class UserService {

    private static final de.alex.alexforjira.db.h2.tables.User USER
            = de.alex.alexforjira.db.h2.tables.User.USER;

    private final DSLContext dsl;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(final DSLContext dsl, final PasswordEncoderProvider passwordEncoderProvider) {
        this.dsl = dsl;
        this.passwordEncoder = passwordEncoderProvider.getEncoder();
    }

    /**
     * Get all users in the system.
     *
     * @return The users.
     */
    public List<UserResponse> findAll() {
        return dsl.select()
                .from(USER)
                .fetch()
                .stream()
                .map(r -> UserResponse.fromUser(r.into(new User())))
                .collect(Collectors.toList());
    }

    /**
     * Get a single user by ID.
     *
     * @param userId
     *         The ID of the user.
     * @return The user or null if not found.
     */
    public User findById(Long userId) {
        final Record record = dsl.select().from(USER).where(USER.ID.eq(userId)).fetchOne();
        return record == null ? null : record.into(new User());
    }

    /**
     * Count how many admins there are.
     *
     * @return The number of admins.
     */
    public int getNumberOfAdmins() {
        return dsl.fetchCount(dsl.select().from(USER).where(USER.ROLE.eq(UserRole.ADMIN.toString())));
    }

    /**
     * Updates the email and role of a user.
     *
     * @param user
     *         The updated user. Has to have an ID.
     * @return The updated user.
     */
    public User update(User user) {
        // check if the email is already taken by another user.
        final User userByEmail = findByEmail(user.getEmail());
        if (userByEmail != null && !userByEmail.getId().equals(user.getId())) {
            throw new ValidationException("The email is already in use.");
        }

        // an admin cannot make himself a default user if he is the only admin.
        final User userInDb = findById(user.getId());
        if (getNumberOfAdmins() == 1
                && userInDb.getRole().equals(UserRole.ADMIN.toString())
                && user.getRole().equals(UserRole.DEFAULT.toString())) {
            throw new ValidationException("You cannot change your role if you are the only admin.");
        }

        dsl.update(USER)
                .set(USER.EMAIL, user.getEmail())
                .set(USER.ROLE, user.getRole())
                .where(USER.ID.eq(user.getId()))
                .execute();

        return findById(user.getId());
    }

    /**
     * Updates the password of a user.
     *
     * @param userId
     *         The ID of the user.
     * @param password
     *         The new password.
     */
    public void setPassword(Long userId, String password) {
        if (password == null || password.trim().equals("")) {
            throw new ValidationException("The password may not be empty.");
        }

        final User user = findById(userId);
        if (user == null) {
            throw new NotFoundException("The user with the ID " + userId + " could not be found.");
        }

        final String encodedPassword = passwordEncoder.encode(password);
        dsl.update(USER).set(USER.HASHED_PASSWORD, encodedPassword).where(USER.ID.eq(userId)).execute();
    }

    /**
     * Delete a user by its ID.
     *
     * @param userId
     *         The ID of the user to delete.
     * @throws NotFoundException
     *         If the user could not be found.
     */
    public void deleteById(Long userId) throws NotFoundException {
        final User user = findById(userId);
        if (user == null) {
            throw new NotFoundException("The user with the ID " + userId + " could not be found.");
        } else if (getNumberOfAdmins() == 1) {
            throw new ValidationException("There has to be at least one admin left.");
        } else {
            dsl.delete(USER).where(USER.ID.eq(userId)).execute();
        }
    }

    /**
     * Creates a new user.
     *
     * @param user
     *         The user to create.
     * @return The created user.
     */
    public User create(User user) {
        if (findByEmail(user.getEmail()) != null) {
            throw new ValidationException("A user with the email already exists.");
        }

        user.setHashedPassword(passwordEncoder.encode(user.getHashedPassword()));

        return dsl.insertInto(USER)
                .columns(USER.EMAIL, USER.HASHED_PASSWORD, USER.ROLE)
                .values(user.getEmail(), user.getHashedPassword(), user.getRole())
                .returning()
                .fetchOne()
                .into(new User());
    }

    /**
     * Count the number of users in the database.
     *
     * @return The number of users.
     */
    public int getNumberOfUsers() {
        return dsl.fetchCount(USER);
    }

    /**
     * Get a user by its email address.
     *
     * @param email
     *         The email address.
     * @return The user or null if the email does not exist.
     */
    public User findByEmail(String email) {
        final Record record = dsl.select()
                .from(USER)
                .where(USER.EMAIL.eq(email))
                .fetchOne();

        return record == null ? null : record.into(new User());
    }

    /**
     * Check if the email/password combination is correct.
     *
     * @param userLogin
     *         The credentials of the user.
     * @return True, if the email/password combination exists.
     */
    public boolean signIn(UserCredentials userLogin) {
        final User user = findByEmail(userLogin.getEmail());
        return passwordEncoder.matches(userLogin.getPassword(), user.getHashedPassword());
    }
}
