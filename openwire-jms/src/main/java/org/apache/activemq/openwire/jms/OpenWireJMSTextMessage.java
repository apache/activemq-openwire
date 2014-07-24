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

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.activemq.openwire.commands.OpenWireTextMessage;

/**
 * A wrapper around an OpenWireTextMessage.
 */
public class OpenWireJMSTextMessage extends OpenWireJMSMessage implements TextMessage {

    private final OpenWireTextMessage message;

    /**
     * Creates a new instance that wraps a new OpenWireJMSTextMessage instance.
     */
    public OpenWireJMSTextMessage() {
        this(new OpenWireTextMessage());
    }

    /**
     * Creates a new instance of an OpenWireJMSTextMessage that wraps the
     * given OpenWireTextMessage instance.
     *
     * @param message
     *        the message to wrap.
     */
    public OpenWireJMSTextMessage(OpenWireTextMessage message) {
        super(message);
        this.message = message;
    }

    @Override
    public OpenWireJMSTextMessage copy() throws JMSException {
        OpenWireJMSTextMessage other = new OpenWireJMSTextMessage(message.copy());
        copy(other);
        return other;
    }

    @Override
    public void clearBody() throws JMSException {
        super.clearBody();
        message.clearBody();
    }

    @Override
    public void setText(String text) throws JMSException {
        checkReadOnlyBody();
        message.setText(text);
    }

    @Override
    public String getText() throws JMSException {
        return message.getText();
    }

    @Override
    public String toString() {
        return this.message.toString();
    }
}
