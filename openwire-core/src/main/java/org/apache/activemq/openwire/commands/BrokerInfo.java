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
 * When a client connects to a broker, the broker send the client a BrokerInfo
 * so that the client knows which broker node he's talking to and also any peers
 * that the node has in his cluster. This is the broker helping the client out
 * in discovering other nodes in the cluster.
 *
 * @openwire:marshaller code="2"
 */
@OpenWireType(typeCode = 2)
public class BrokerInfo extends BaseCommand {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.BROKER_INFO;

    @OpenWireProperty(version = 1, sequence = 1, cached = true)
    protected BrokerId brokerId;

    @OpenWireProperty(version = 1, sequence = 2)
    protected String brokerURL;

    @OpenWireProperty(version = 1, sequence = 3)
    protected BrokerInfo peerBrokerInfos[];

    @OpenWireProperty(version = 1, sequence = 4)
    protected String brokerName;

    @OpenWireProperty(version = 1, sequence = 5)
    protected boolean slaveBroker;

    @OpenWireProperty(version = 1, sequence = 6)
    protected boolean masterBroker;

    @OpenWireProperty(version = 1, sequence = 7)
    protected boolean faultTolerantConfiguration;

    @OpenWireProperty(version = 2, sequence = 8)
    protected boolean duplexConnection;

    @OpenWireProperty(version = 2, sequence = 9)
    protected boolean networkConnection;

    @OpenWireProperty(version = 2, sequence = 10)
    protected long connectionId;

    @OpenWireProperty(version = 3, sequence = 11)
    protected String brokerUploadUrl;

    @OpenWireProperty(version = 3, sequence = 12)
    protected String networkProperties;

    public BrokerInfo copy() {
        BrokerInfo copy = new BrokerInfo();
        copy(copy);
        return copy;
    }

    private void copy(BrokerInfo copy) {
        super.copy(copy);
        copy.brokerId = this.brokerId;
        copy.brokerURL = this.brokerURL;
        copy.slaveBroker = this.slaveBroker;
        copy.masterBroker = this.masterBroker;
        copy.faultTolerantConfiguration = this.faultTolerantConfiguration;
        copy.networkConnection = this.networkConnection;
        copy.duplexConnection = this.duplexConnection;
        copy.peerBrokerInfos = this.peerBrokerInfos;
        copy.brokerName = this.brokerName;
        copy.connectionId = this.connectionId;
        copy.brokerUploadUrl = this.brokerUploadUrl;
        copy.networkProperties = this.networkProperties;
    }

    @Override
    public boolean isBrokerInfo() {
        return true;
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public BrokerId getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(BrokerId brokerId) {
        this.brokerId = brokerId;
    }

    /**
     * @openwire:property version=1
     */
    public String getBrokerURL() {
        return brokerURL;
    }

    public void setBrokerURL(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    /**
     * @openwire:property version=1 testSize=0
     */
    public BrokerInfo[] getPeerBrokerInfos() {
        return peerBrokerInfos;
    }

    public void setPeerBrokerInfos(BrokerInfo[] peerBrokerInfos) {
        this.peerBrokerInfos = peerBrokerInfos;
    }

    /**
     * @openwire:property version=1
     */
    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    @Override
    public Response visit(CommandVisitor visitor) throws Exception {
        return visitor.processBrokerInfo(this);
    }

    /**
     * @openwire:property version=1
     */
    public boolean isSlaveBroker() {
        return slaveBroker;
    }

    public void setSlaveBroker(boolean slaveBroker) {
        this.slaveBroker = slaveBroker;
    }

    /**
     * @openwire:property version=1
     */
    public boolean isMasterBroker() {
        return masterBroker;
    }

    /**
     * @param masterBroker The masterBroker to set.
     */
    public void setMasterBroker(boolean masterBroker) {
        this.masterBroker = masterBroker;
    }

    /**
     * @openwire:property version=1
     * @return Returns the faultTolerantConfiguration.
     */
    public boolean isFaultTolerantConfiguration() {
        return faultTolerantConfiguration;
    }

    /**
     * @param faultTolerantConfiguration The faultTolerantConfiguration to set.
     */
    public void setFaultTolerantConfiguration(boolean faultTolerantConfiguration) {
        this.faultTolerantConfiguration = faultTolerantConfiguration;
    }

    /**
     * @openwire:property version=2
     * @return the duplexConnection
     */
    public boolean isDuplexConnection() {
        return this.duplexConnection;
    }

    /**
     * @param duplexConnection the duplexConnection to set
     */
    public void setDuplexConnection(boolean duplexConnection) {
        this.duplexConnection = duplexConnection;
    }

    /**
     * @openwire:property version=2
     * @return the networkConnection
     */
    public boolean isNetworkConnection() {
        return this.networkConnection;
    }

    /**
     * @param networkConnection the networkConnection to set
     */
    public void setNetworkConnection(boolean networkConnection) {
        this.networkConnection = networkConnection;
    }

    /**
     * The broker assigns a each connection it accepts a connection id.
     *
     * @openwire:property version=2
     */
    public long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * The URL to use when uploading BLOBs to the broker or some other external
     * file/http server
     *
     * @openwire:property version=3
     */
    public String getBrokerUploadUrl() {
        return brokerUploadUrl;
    }

    public void setBrokerUploadUrl(String brokerUploadUrl) {
        this.brokerUploadUrl = brokerUploadUrl;
    }

    /**
     * @openwire:property version=3 cache=false
     * @return the networkProperties
     */
    public String getNetworkProperties() {
        return this.networkProperties;
    }

    /**
     * @param networkProperties the networkProperties to set
     */
    public void setNetworkProperties(String networkProperties) {
        this.networkProperties = networkProperties;
    }
}
