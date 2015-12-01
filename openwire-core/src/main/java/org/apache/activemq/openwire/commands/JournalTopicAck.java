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
 * @openwire:marshaller code="50"
 */
@OpenWireType(typeCode = 50)
public class JournalTopicAck implements DataStructure {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.JOURNAL_ACK;

    @OpenWireProperty(version = 1, sequence = 1)
    protected OpenWireDestination destination;

    @OpenWireProperty(version = 1, sequence = 2)
    protected MessageId messageId;

    @OpenWireProperty(version = 1, sequence = 3)
    protected long messageSequenceId;

    @OpenWireProperty(version = 1, sequence = 4)
    protected String subscritionName;

    @OpenWireProperty(version = 1, sequence = 5)
    protected String clientId;

    @OpenWireProperty(version = 1, sequence = 6)
    protected TransactionId transactionId;

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
    public MessageId getMessageId() {
        return messageId;
    }

    public void setMessageId(MessageId messageId) {
        this.messageId = messageId;
    }

    /**
     * @openwire:property version=1
     */
    public long getMessageSequenceId() {
        return messageSequenceId;
    }

    public void setMessageSequenceId(long messageSequenceId) {
        this.messageSequenceId = messageSequenceId;
    }

    /**
     * @openwire:property version=1
     */
    public String getSubscritionName() {
        return subscritionName;
    }

    public void setSubscritionName(String subscritionName) {
        this.subscritionName = subscritionName;
    }

    /**
     * @openwire:property version=1
     */
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @openwire:property version=1
     */
    public TransactionId getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(TransactionId transaction) {
        this.transactionId = transaction;
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
