/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.authentication;

import javax.security.auth.login.AccountException;

/**
 * Describes an authentication error condition where a user account has been administratively disabled.
 *
 * @author Marvin S. Addison
 * @since 4.0
 */
public class AccountDisabledException extends AccountException {

    /** Serialization metadata. */
    private static final long serialVersionUID = 7487835035108753209L;

    /**
     * Instantiates a new account disabled exception.
     */
    public AccountDisabledException() {
    }

    /**
     * Instantiates a new account disabled exception.
     *
     * @param msg the msg
     */
    public AccountDisabledException(final String msg) {
        super(msg);
    }
}
