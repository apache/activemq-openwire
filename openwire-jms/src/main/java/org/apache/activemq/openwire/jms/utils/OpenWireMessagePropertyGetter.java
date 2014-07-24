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
package org.apache.activemq.openwire.jms.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.activemq.openwire.commands.OpenWireMessage;
import org.apache.activemq.openwire.commands.TransactionId;

/**
 * Utility class used to intercept calls to Message property gets and map the
 * correct OpenWire fields to the property name being queried.
 */
public class OpenWireMessagePropertyGetter {

    private static final Map<String, PropertyGetter> PROPERTY_GETTERS = new HashMap<String, PropertyGetter>();

    /**
     * Interface for a Property Get intercepter object used to lookup JMS style
     * properties that are part of the OpenWire Message object members or perform
     * some needed conversion action before returned some named property.
     */
    public interface PropertyGetter {

        /**
         * Called when the names property is queried from an OpenWire Message object.
         *
         * @param message
         *        The message being acted upon.
         *
         * @return the correct property value from the given Message.
         */
        Object getProperty(OpenWireMessage message);
    }

    static {
        PROPERTY_GETTERS.put("JMSDestination", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                Destination dest = message.getOriginalDestination();
                if (dest == null) {
                    dest = message.getDestination();
                }
                if (dest == null) {
                    return null;
                }
                return dest.toString();
            }
        });
        PROPERTY_GETTERS.put("JMSReplyTo", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                if (message.getReplyTo() == null) {
                    return null;
                }
                return message.getReplyTo().toString();
            }
        });
        PROPERTY_GETTERS.put("JMSType", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return message.getType();
            }
        });
        PROPERTY_GETTERS.put("JMSDeliveryMode", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return message.isPersistent() ? "PERSISTENT" : "NON_PERSISTENT";
            }
        });
        PROPERTY_GETTERS.put("JMSPriority", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return Integer.valueOf(message.getPriority());
            }
        });
        PROPERTY_GETTERS.put("JMSMessageID", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                if (message.getMessageId() == null) {
                    return null;
                }
                return message.getMessageId().toString();
            }
        });
        PROPERTY_GETTERS.put("JMSTimestamp", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return Long.valueOf(message.getTimestamp());
            }
        });
        PROPERTY_GETTERS.put("JMSCorrelationID", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return message.getCorrelationId();
            }
        });
        PROPERTY_GETTERS.put("JMSExpiration", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return Long.valueOf(message.getExpiration());
            }
        });
        PROPERTY_GETTERS.put("JMSRedelivered", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return Boolean.valueOf(message.isRedelivered());
            }
        });
        PROPERTY_GETTERS.put("JMSXDeliveryCount", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return Integer.valueOf(message.getRedeliveryCounter() + 1);
            }
        });
        PROPERTY_GETTERS.put("JMSXGroupID", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return message.getGroupId();
            }
        });
        PROPERTY_GETTERS.put("JMSXUserID", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                Object userId = message.getUserId();
                if (userId == null) {
                    try {
                        userId = message.getProperty("JMSXUserID");
                    } catch (JMSException e) {
                    }
                }

                return userId;
            }
        });
        PROPERTY_GETTERS.put("JMSXGroupSeq", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return new Integer(message.getGroupSequence());
            }
        });
        PROPERTY_GETTERS.put("JMSXProducerTXID", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                TransactionId txId = message.getOriginalTransactionId();
                if (txId == null) {
                    txId = message.getTransactionId();
                }
                if (txId == null) {
                    return null;
                }
                return txId.toString();
            }
        });
        PROPERTY_GETTERS.put("JMSActiveMQBrokerInTime", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return Long.valueOf(message.getBrokerInTime());
            }
        });
        PROPERTY_GETTERS.put("JMSActiveMQBrokerOutTime", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return Long.valueOf(message.getBrokerOutTime());
            }
        });
        PROPERTY_GETTERS.put("JMSActiveMQBrokerPath", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return Arrays.toString(message.getBrokerPath());
            }
        });
        PROPERTY_GETTERS.put("JMSXGroupFirstForConsumer", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return Boolean.valueOf(message.isJMSXGroupFirstForConsumer());
            }
        });
        PROPERTY_GETTERS.put("JMSXMimeType", new PropertyGetter() {
            @Override
            public Object getProperty(OpenWireMessage message) {
                return String.valueOf(message.getMimeType());
            }
        });
    }

    /**
     * For each of the currently configured message property intercepter instance a
     * string key value is inserted into an Set and returned.
     *
     * @return a Set<String> containing the names of all intercepted properties.
     */
    public static Set<String> getPropertyNames() {
        return PROPERTY_GETTERS.keySet();
    }

    /**
     * Static get method that takes a property name and gets the value either via
     * a registered property get object or through the OpenWireMessage getProperty
     * method.
     *
     * @param message
     *        the OpenWireMessage instance to read from
     * @param name
     *        the property name that is being requested.
     *
     * @return the correct value either mapped to an OpenWire attribute of a Message property.
     *
     * @throws JMSException if an error occurs while reading the defined property.
     */
    public static Object getProperty(OpenWireMessage message, String name) throws JMSException {
        Object value = null;

        PropertyGetter jmsPropertyExpression = PROPERTY_GETTERS.get(name);
        if (jmsPropertyExpression != null) {
            value = jmsPropertyExpression.getProperty(message);
        } else {
            value = message.getProperty(name);
        }

        return value;
    }

    /**
     * Allows for the additional PropertyGetter instances to be added to the global set.
     *
     * @param propertyName
     *        The name of the Message property that will be intercepted.
     * @param getter
     *        The PropertyGetter instance that should be used for the named property.
     */
    public static void addPropertyGetter(String propertyName, PropertyGetter getter) {
        PROPERTY_GETTERS.put(propertyName, getter);
    }

    /**
     * Given a property name, remove the configured getter that has been assigned to
     * intercept the queries for that property value.
     *
     * @param propertyName
     *        The name of the Property Getter to remove.
     *
     * @return true if a getter was removed from the global set.
     */
    public boolean removePropertyGetter(String propertyName) {
        if (PROPERTY_GETTERS.remove(propertyName) != null) {
            return true;
        }

        return false;
    }

    private final String name;
    private final PropertyGetter jmsPropertyExpression;

    /**
     * Creates an new property getter instance that is assigned to read the named value.
     *
     * @param name
     *        the property value that this getter is assigned to lookup.
     */
    public OpenWireMessagePropertyGetter(String name) {
        this.name = name;
        jmsPropertyExpression = PROPERTY_GETTERS.get(name);
    }

    /**
     * Gets the correct property value from the OpenWireMessage instance based on
     * the predefined property mappings.
     *
     * @param message
     *        the OpenWireMessage whose property is being read.
     *
     * @return the correct value either mapped to an OpenWire attribute of a Message property.
     *
     * @throws JMSException if an error occurs while reading the defined property.
     */
    public Object get(OpenWireMessage message) throws JMSException {
        if (jmsPropertyExpression != null) {
            return jmsPropertyExpression.getProperty(message);
        }

        return message.getProperty(name);
    }

    /**
     * @return the property name that is being intercepted for the OpenWireMessage.
     */
    public String getName() {
        return name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !this.getClass().equals(o.getClass())) {
            return false;
        }
        return name.equals(((OpenWireMessagePropertyGetter) o).name);
    }
}
