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

import de.alex.alexforjira.api.jira.versions.JiraVersionResource;
import de.alex.alexforjira.shared.RestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Exception handler for REST requests.
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(JiraVersionResource.class);

    /**
     * Check that a user only accesses projects that are allowed.
     *
     * @param e
     *         The exception.
     * @param request
     *         The request.
     * @return A REST error with 403 - Forbidden.
     */
    @ExceptionHandler(value = {ProjectForbiddenException.class})
    protected ResponseEntity<Object> handleException(ProjectForbiddenException e, WebRequest request) {
        log.error(e.getMessage());
        final RestError error = new RestError(HttpStatus.FORBIDDEN, e.getMessage());
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return handleExceptionInternal(e, error, headers, HttpStatus.FORBIDDEN, request);
    }
}
