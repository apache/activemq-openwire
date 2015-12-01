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
 * @openwire:marshaller
 */
@OpenWireType(typeCode = 0)
public abstract class BaseCommand implements Command {

    @OpenWireProperty(version = 1, sequence = 1)
    protected int commandId;

    @OpenWireProperty(version = 1, sequence = 2)
    protected boolean responseRequired;

    public void copy(BaseCommand copy) {
        copy.commandId = commandId;
        copy.responseRequired = responseRequired;
    }

    /**
     * @openwire:property version=1
     */
    @Override
    public int getCommandId() {
        return commandId;
    }

    @Override
    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    /**
     * @openwire:property version=1
     */
    @Override
    public boolean isResponseRequired() {
        return responseRequired;
    }

    @Override
    public void setResponseRequired(boolean responseRequired) {
        this.responseRequired = responseRequired;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isWireFormatInfo() {
        return false;
    }

    @Override
    public boolean isBrokerInfo() {
        return false;
    }

    @Override
    public boolean isResponse() {
        return false;
    }

    @Override
    public boolean isMessageDispatch() {
        return false;
    }

    @Override
    public boolean isMessage() {
        return false;
    }

    @Override
    public boolean isMarshallAware() {
        return false;
    }

    @Override
    public boolean isMessageAck() {
        return false;
    }

    @Override
    public boolean isMessageDispatchNotification() {
        return false;
    }

    @Override
    public boolean isShutdownInfo() {
        return false;
    }

    @Override
    public boolean isConnectionControl() {
        return false;
    }

    @Override
    public boolean isConnectionInfo() {
        return false;
    }

    @Override
    public boolean isSessionInfo() {
        return false;
    }

    @Override
    public boolean isProducerInfo() {
        return false;
    }

    @Override
    public boolean isConsumerInfo() {
        return false;
    }
}
