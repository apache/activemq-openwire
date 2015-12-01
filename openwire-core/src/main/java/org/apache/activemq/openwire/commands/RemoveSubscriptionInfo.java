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
 * @openwire:marshaller code="9"
 */
@OpenWireType(typeCode = 9)
public class RemoveSubscriptionInfo extends BaseCommand {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.REMOVE_SUBSCRIPTION_INFO;

    @OpenWireProperty(version = 1, sequence = 1, cached = true)
    protected ConnectionId connectionId;

    @OpenWireProperty(version = 1, sequence = 2)
    protected String subscriptionName;

    @OpenWireProperty(version = 1, sequence = 3)
    protected String clientId;

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public ConnectionId getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(ConnectionId connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * @openwire:property version=1
     */
    public String getSubcriptionName() {
        return subscriptionName;
    }

    /**
     */
    public void setSubcriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
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

    @Override
    public Response visit(CommandVisitor visitor) throws Exception {
        return visitor.processRemoveSubscription(this);
    }
}
