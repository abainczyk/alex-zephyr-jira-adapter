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

package de.alex.alexforjira.api.executions.entities;

/** The status of the current test execution. */
public class ExecutionStatus {

    /** If tests are currently being executed. */
    private boolean active;

    /** How many tests are left the queue. */
    private int inQueue;

    /**
     * Constructor.
     *
     * @param active
     *         {@link #active}.
     * @param inQueue
     *         {@link #inQueue}.
     */
    public ExecutionStatus(boolean active, int inQueue) {
        this.active = active;
        this.inQueue = inQueue;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getInQueue() {
        return inQueue;
    }

    public void setInQueue(int inQueue) {
        this.inQueue = inQueue;
    }
}
