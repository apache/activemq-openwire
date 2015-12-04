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

import org.apache.activemq.openwire.annotations.OpenWireType;

/**
 * Represents an OpenWire Temporary Queue.
 */
@OpenWireType(typeCode = 102)
public class OpenWireTempQueue extends OpenWireTempDestination {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.OPENWIRE_TEMP_QUEUE;

    public OpenWireTempQueue() {
    }

    public OpenWireTempQueue(String name) {
        super(name);
    }

    public OpenWireTempQueue(ConnectionId connectionId, long sequenceId) {
        super(connectionId.getValue(), sequenceId);
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public boolean isQueue() {
        return true;
    }

    @Override
    public byte getDestinationType() {
        return TEMP_QUEUE_TYPE;
    }

    @Override
    protected String getQualifiedPrefix() {
        return TEMP_QUEUE_QUALIFED_PREFIX;
    }

    public String getQueueName() {
        return getPhysicalName();
    }
}
