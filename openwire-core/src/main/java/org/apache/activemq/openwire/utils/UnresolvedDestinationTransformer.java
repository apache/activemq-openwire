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

import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.activemq.openwire.commands.OpenWireDestination;

/**
 * Allows for the configuration of a user defined Destination transformer that will
 * be called when it cannot be logically determined what type of Destination is
 * being converted to an OpenWireDestination.  This can happen for instance when a
 * JMS Provider's Destination implements both Topic and Queue from the same root
 * Destination object.
 */
public interface UnresolvedDestinationTransformer {

    /**
     * Given the JMS Destination convert it to the correct OpenWire destination
     *
     * @param destination
     *        the foreign destination to convert to the proper OpenWire type.
     *
     * @return an OpenWireDestination instance of the correct type.
     *
     * @throws JMSException if an error occurs while converting the Destination type.
     */
    public OpenWireDestination transform(Destination destination) throws JMSException;

    /**
     * Given the name of a JMS Destination convert it to the correct OpenWire destination.
     *
     * @param destination
     *        the name of a destination to convert to the proper OpenWire type.
     *
     * @return an OpenWireDestination instance of the correct type.
     *
     * @throws JMSException if an error occurs while converting the Destination type.
     */
    public OpenWireDestination transform(String destination) throws JMSException;

}
