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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @openwire:marshaller code="91"
 */
@OpenWireType(typeCode = 91)
public class NetworkBridgeFilter implements DataStructure {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.NETWORK_BRIDGE_FILTER;

    static final Logger LOG = LoggerFactory.getLogger(NetworkBridgeFilter.class);

    @OpenWireProperty(version = 1, sequence = 1, cached = true)
    protected BrokerId networkBrokerId;

    @OpenWireProperty(version = 10, sequence = 2)
    protected int messageTTL;

    @OpenWireProperty(version = 10, sequence = 3)
    protected int consumerTTL;

    public NetworkBridgeFilter() {
    }

    public NetworkBridgeFilter(ConsumerInfo consumerInfo, BrokerId networkBrokerId, int messageTTL, int consumerTTL) {
        this.networkBrokerId = networkBrokerId;
        this.messageTTL = messageTTL;
        this.consumerTTL = consumerTTL;
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public boolean isMarshallAware() {
        return false;
    }

    // keep for backward compat with older wire formats
    public int getNetworkTTL() {
        return messageTTL;
    }

    public void setNetworkTTL(int networkTTL) {
        messageTTL = networkTTL;
        consumerTTL = networkTTL;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public BrokerId getNetworkBrokerId() {
        return networkBrokerId;
    }

    public void setNetworkBrokerId(BrokerId remoteBrokerPath) {
        this.networkBrokerId = remoteBrokerPath;
    }

    public void setMessageTTL(int messageTTL) {
        this.messageTTL = messageTTL;
    }

    /**
     * @openwire:property version=10
     */
    public int getMessageTTL() {
        return this.messageTTL;
    }

    public void setConsumerTTL(int consumerTTL) {
        this.consumerTTL = consumerTTL;
    }

    /**
     * @openwire:property version=10
     */
    public int getConsumerTTL() {
        return this.consumerTTL;
    }
}
