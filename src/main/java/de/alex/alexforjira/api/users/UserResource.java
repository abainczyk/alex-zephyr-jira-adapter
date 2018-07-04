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
import de.alex.alexforjira.db.h2.tables.pojos.User;
import de.alex.alexforjira.shared.RestError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;

/**
 * Endpoints for handling users in the application.
 */
@RestController
public class UserResource {

    private static final String RESOURCE_URL = "/rest/users";

    /**
     * Requests to endpoints that are prefixed with this URL can only be requested by users that inhibit the "ADMIN"
     * role. Other users will get a 401 response.
     */
    private static final String ADMIN_RESOURCE_URL = "/rest/admin/users";

    private final UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users.
     *
     * @return The users.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = ADMIN_RESOURCE_URL,
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Creates a new user.
     *
     * @param user
     *         The user to create.
     * @return The created user.
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = ADMIN_RESOURCE_URL,
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity create(@RequestBody User user) {
        try {
            final User createdUser = userService.create(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RestError(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Deletes an existing user.
     *
     * @param userId
     *         The ID of the user to delete.
     * @return Nothing.
     */
    @RequestMapping(
            method = RequestMethod.DELETE,
            value = ADMIN_RESOURCE_URL + "/{userId}",
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity delete(@PathVariable("userId") Long userId) {
        try {
            userService.deleteById(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RestError(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Update the password of a user.
     *
     * @param userId
     *         The ID the user.
     * @param userLogin
     *         The credentials of the new user. Only the password is used.
     * @return Nothing.
     */
    @RequestMapping(
            method = RequestMethod.PUT,
            value = ADMIN_RESOURCE_URL + "/{userId}/setPassword",
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity setPassword(@PathVariable("userId") Long userId, @RequestBody UserCredentials userLogin) {
        try {
            userService.setPassword(userId, userLogin.getPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RestError(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Updates a user. Only the email and role can be updated. Also, the user object. has to contain an ID.
     *
     * @param userId
     *         The ID of the user.
     * @param user
     *         The updated user.
     * @return The updated user.
     */
    @RequestMapping(
            method = RequestMethod.PUT,
            value = ADMIN_RESOURCE_URL + "/{userId}",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity update(@PathVariable("userId") Long userId, @RequestBody User user) {
        try {
            if (!userId.equals(user.getId())) {
                throw new Exception("The IDs do not match.");
            }
            final User updatedUser = userService.update(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RestError(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    /**
     * Checks if a user exists. Since we use HTTP Basic Auth, a request to this endpoint is not really required.
     * However, it makes it easier to check if the user can login on the client side.
     *
     * @param userLogin
     *         The credentials of the user.
     * @return The user that tried to sign in.
     */
    @RequestMapping(
            method = RequestMethod.POST,
            value = RESOURCE_URL + "/signIn",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public ResponseEntity signIn(@RequestBody final UserCredentials userLogin) {
        if (userService.findByEmail(userLogin.getEmail()) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RestError(HttpStatus.BAD_REQUEST, "The user with the email could not be found."));
        }

        if (!userService.signIn(userLogin)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RestError(HttpStatus.BAD_REQUEST, "The password is incorrect."));
        }

        final User user = userService.findByEmail(userLogin.getEmail());
        return ResponseEntity.ok(UserResponse.fromUser(user));
    }
}
