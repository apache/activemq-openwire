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
import org.apache.activemq.openwire.annotations.OpenWireProperty;

/**
 * @openwire:marshaller code="52"
 */
@OpenWireType(typeCode = 52)
public class JournalQueueAck implements DataStructure {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.JOURNAL_REMOVE;

    @OpenWireProperty(version = 1, sequence = 1)
    OpenWireDestination destination;

    @OpenWireProperty(version = 1, sequence = 2)
    MessageAck messageAck;

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * @openwire:property version=1
     */
    public OpenWireDestination getDestination() {
        return destination;
    }

    public void setDestination(OpenWireDestination destination) {
        this.destination = destination;
    }

    /**
     * @openwire:property version=1
     */
    public MessageAck getMessageAck() {
        return messageAck;
    }

    public void setMessageAck(MessageAck messageAck) {
        this.messageAck = messageAck;
    }

    @Override
    public boolean isMarshallAware() {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{ " + destination + " }";
    }
}
