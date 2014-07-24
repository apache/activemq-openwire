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
package org.apache.activemq.openwire.utils;

import java.lang.reflect.Method;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.activemq.openwire.commands.OpenWireDestination;
import org.apache.activemq.openwire.commands.OpenWireQueue;
import org.apache.activemq.openwire.commands.OpenWireTopic;

/**
 * A default implementation of the resolver that attempts to find an isQueue or isTopic method
 * on the foreign destination to determine the correct type.
 */
public class DefaultUnresolvedDestinationTransformer implements UnresolvedDestinationTransformer {

    @Override
    public OpenWireDestination transform(Destination dest) throws JMSException {
        String queueName = ((Queue) dest).getQueueName();
        String topicName = ((Topic) dest).getTopicName();

        if (queueName == null && topicName == null) {
            throw new JMSException("Unresolvable destination: Both queue and topic names are null: " + dest);
        }

        try {
            Method isQueueMethod = dest.getClass().getMethod("isQueue");
            Method isTopicMethod = dest.getClass().getMethod("isTopic");

            if (isQueueMethod == null && isTopicMethod == null) {
                throw new JMSException("Unresolvable destination: Neither isQueue nor isTopic methods present: " + dest);
            }

            Boolean isQueue = (Boolean) isQueueMethod.invoke(dest);
            Boolean isTopic = (Boolean) isTopicMethod.invoke(dest);
            if (isQueue) {
                return new OpenWireQueue(queueName);
            } else if (isTopic) {
                return new OpenWireTopic(topicName);
            } else {
                throw new JMSException("Unresolvable destination: Neither Queue nor Topic: " + dest);
            }
        } catch (Exception e) {
            throw new JMSException("Unresolvable destination: " + e.getMessage() + ": " + dest);
        }
    }

    @Override
    public OpenWireDestination transform(String dest) throws JMSException {
        return new OpenWireQueue(dest);
    }
}
