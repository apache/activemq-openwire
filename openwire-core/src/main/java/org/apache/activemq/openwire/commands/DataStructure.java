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
package org.apache.activemq.openwire.commands;

/**
 * Base class for all the OpenWire commands and resource objects.
 */
public interface DataStructure {

    /**
     * @return The type of the data structure
     */
    byte getDataStructureType();

    /**
     * Returns true when a Data Structure instance want to be made aware of the
     * marshaling events associated with send and receive of the command.
     *
     * Any instance that is marshal aware will implement the MarshalAware interface
     * however this method provides a shortcut that can be faster than a direct
     * instance of check on each marshaled object.
     *
     * @return if the data structure is marshal aware.
     */
    boolean isMarshallAware();
}
