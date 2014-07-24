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
package org.apache.activemq.openwire.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.activemq.openwire.commands.OpenWireObjectMessage;

/**
 * Wrapper class that provides ObjectMessage compliant mappings to the OpenWireObjectMessage
 */
public class OpenWireJMSObjectMessage extends OpenWireJMSMessage implements ObjectMessage {

    private final OpenWireObjectMessage message;

    /**
     * Creates a new instance that wraps a new OpenWireMessage instance.
     */
    public OpenWireJMSObjectMessage() {
        this(new OpenWireObjectMessage());
    }

    /**
     * Creates a new instance that wraps the given OpenWireMessage
     *
     * @param message
     *        the OpenWireMessage to wrap.
     */
    public OpenWireJMSObjectMessage(OpenWireObjectMessage message) {
        this.message = message;
    }

    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        message.clearBody();
    }

    @Override
    public OpenWireJMSObjectMessage copy() throws JMSException {
        OpenWireJMSObjectMessage other = new OpenWireJMSObjectMessage(message.copy());
        return other;
    }

    @Override
    public void setObject(Serializable object) throws JMSException {
        checkReadOnlyBody();
        message.setObject(object);
    }

    @Override
    public Serializable getObject() throws JMSException {
        return message.getObject();
    }
}
