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

import javax.jms.JMSException;
import javax.jms.Topic;

import org.apache.activemq.openwire.annotations.OpenWireType;

/**
 * @openwire:marshaller code="101"
 */
@OpenWireType(typeCode = 101)
public class OpenWireTopic extends OpenWireDestination implements Topic {

    public static final byte DATA_STRUCTURE_TYPE = CommandTypes.OPENWIRE_TOPIC;

    public OpenWireTopic() {
    }

    public OpenWireTopic(String name) {
        super(name);
    }

    @Override
    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    @Override
    public boolean isTopic() {
        return true;
    }

    @Override
    public String getTopicName() throws JMSException {
        return getPhysicalName();
    }

    @Override
    public byte getDestinationType() {
        return TOPIC_TYPE;
    }

    @Override
    protected String getQualifiedPrefix() {
        return TOPIC_QUALIFIED_PREFIX;
    }
}
