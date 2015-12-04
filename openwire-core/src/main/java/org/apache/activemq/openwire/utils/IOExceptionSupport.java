/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.openwire.utils;

import java.io.IOException;

/**
 * Exception support class.
 *
 * Factory class for creating IOException instances based on String messages or by
 * wrapping other causal exceptions.
 *
 * @since 1.0
 */
public final class IOExceptionSupport {

    private IOExceptionSupport() {}

    public static IOException create(String msg, Throwable cause) {
        IOException exception = new IOException(msg);
        exception.initCause(cause);
        return exception;
    }

    public static IOException create(Throwable cause) {
        if (cause instanceof IOException) {
            return (IOException) cause;
        }

        if (cause.getCause() instanceof IOException) {
            return (IOException) cause.getCause();
        }

        String msg = cause.getMessage();
        if (msg == null || msg.length() == 0) {
            msg = cause.toString();
        }

        IOException exception = new IOException(msg);
        exception.initCause(cause);
        return exception;
    }
}
