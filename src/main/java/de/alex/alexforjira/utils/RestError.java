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

package de.alex.alexforjira.utils;

import org.springframework.http.HttpStatus;

/** Error data that is send to the client. */
public class RestError {

    /** The HTTP status. */
    private int status;

    /** The message to send. */
    private String message;

    /**
     * Constructor.
     *
     * @param status
     *         The HTTP status.
     * @param message
     *         The error message.
     */
    public RestError(final HttpStatus status, final String message) {
        this.status = status.value();
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
