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

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import org.apache.activemq.openwire.annotations.OpenWireType;
import org.apache.activemq.openwire.annotations.OpenWireExtension;
import org.apache.activemq.openwire.annotations.OpenWireProperty;
import org.apache.activemq.openwire.utils.DefaultUnresolvedDestinationTransformer;
import org.apache.activemq.openwire.utils.UnresolvedDestinationTransformer;

/**
 * Base Destination class used to provide most of the utilities necessary to deal
 * with incoming and outgoing destination processing.
 *
 * @openwire:marshaller
 */
@OpenWireType(typeCode = 0)
public abstract class OpenWireDestination implements Destination, DataStructure, Comparable<OpenWireDestination> {

    public static final String PATH_SEPERATOR = ".";
    public static final char COMPOSITE_SEPERATOR = ',';

    public static final byte QUEUE_TYPE = 0x01;
    public static final byte TOPIC_TYPE = 0x02;
    public static final byte TEMP_MASK = 0x04;
    public static final byte TEMP_TOPIC_TYPE = TOPIC_TYPE | TEMP_MASK;
    public static final byte TEMP_QUEUE_TYPE = QUEUE_TYPE | TEMP_MASK;

    public static final String QUEUE_QUALIFIED_PREFIX = "queue://";
    public static final String TOPIC_QUALIFIED_PREFIX = "topic://";
    public static final String TEMP_QUEUE_QUALIFED_PREFIX = "temp-queue://";
    public static final String TEMP_TOPIC_QUALIFED_PREFIX = "temp-topic://";

    public static final String TEMP_DESTINATION_NAME_PREFIX = "ID:";

    @OpenWireProperty(version = 1, sequence = 1)
    protected String physicalName;

    @OpenWireExtension
    protected transient OpenWireDestination[] compositeDestinations;

    @OpenWireExtension
    protected transient String[] destinationPaths;

    @OpenWireExtension
    protected transient boolean isPattern;

    @OpenWireExtension
    protected transient int hashValue;

    @OpenWireExtension(serialized = true)
    protected Map<String, String> options;

    protected static UnresolvedDestinationTransformer unresolvableDestinationTransformer = new DefaultUnresolvedDestinationTransformer();

    public OpenWireDestination() {
    }

    protected OpenWireDestination(String name) {
        setPhysicalName(name);
    }

    public OpenWireDestination(OpenWireDestination composites[]) {
        setCompositeDestinations(composites);
    }

    // static helper methods for working with destinations
    // -------------------------------------------------------------------------
    public static OpenWireDestination createDestination(String name, byte defaultType) {
        if (name.startsWith(QUEUE_QUALIFIED_PREFIX)) {
            return new OpenWireQueue(name.substring(QUEUE_QUALIFIED_PREFIX.length()));
        } else if (name.startsWith(TOPIC_QUALIFIED_PREFIX)) {
            return new OpenWireTopic(name.substring(TOPIC_QUALIFIED_PREFIX.length()));
        } else if (name.startsWith(TEMP_QUEUE_QUALIFED_PREFIX)) {
            return new OpenWireTempQueue(name.substring(TEMP_QUEUE_QUALIFED_PREFIX.length()));
        } else if (name.startsWith(TEMP_TOPIC_QUALIFED_PREFIX)) {
            return new OpenWireTempTopic(name.substring(TEMP_TOPIC_QUALIFED_PREFIX.length()));
        }

        switch (defaultType) {
        case QUEUE_TYPE:
            return new OpenWireQueue(name);
        case TOPIC_TYPE:
            return new OpenWireTopic(name);
        case TEMP_QUEUE_TYPE:
            return new OpenWireTempQueue(name);
        case TEMP_TOPIC_TYPE:
            return new OpenWireTempTopic(name);
        default:
            throw new IllegalArgumentException("Invalid default destination type: " + defaultType);
        }
    }

    public static OpenWireDestination transform(Destination dest) throws JMSException {
        if (dest == null) {
            return null;
        }
        if (dest instanceof OpenWireDestination) {
            return (OpenWireDestination)dest;
        }

        if (dest instanceof Queue && dest instanceof Topic) {
            String queueName = ((Queue) dest).getQueueName();
            String topicName = ((Topic) dest).getTopicName();
            if (queueName != null && topicName == null) {
                return new OpenWireQueue(queueName);
            } else if (queueName == null && topicName != null) {
                return new OpenWireTopic(topicName);
            } else {
                return unresolvableDestinationTransformer.transform(dest);
            }
        }
        if (dest instanceof TemporaryQueue) {
            return new OpenWireTempQueue(((TemporaryQueue)dest).getQueueName());
        }
        if (dest instanceof TemporaryTopic) {
            return new OpenWireTempTopic(((TemporaryTopic)dest).getTopicName());
        }
        if (dest instanceof Queue) {
            return new OpenWireQueue(((Queue)dest).getQueueName());
        }
        if (dest instanceof Topic) {
            return new OpenWireTopic(((Topic)dest).getTopicName());
        }
        throw new JMSException("Could not transform the destination into a ActiveMQ destination: " + dest);
    }

    public static int compare(OpenWireDestination destination, OpenWireDestination destination2) {
        if (destination == destination2) {
            return 0;
        }
        if (destination == null) {
            return -1;
        } else if (destination2 == null) {
            return 1;
        } else {
            if (destination.isQueue() == destination2.isQueue()) {
                return destination.getPhysicalName().compareTo(destination2.getPhysicalName());
            } else {
                return destination.isQueue() ? -1 : 1;
            }
        }
    }

    @Override
    public int compareTo(OpenWireDestination that) {
        if (that == null) {
            return -1;
        }
        return compare(this, that);
    }

    public boolean isComposite() {
        return compositeDestinations != null;
    }

    public OpenWireDestination[] getCompositeDestinations() {
        return compositeDestinations;
    }

    public void setCompositeDestinations(OpenWireDestination[] destinations) {
        this.compositeDestinations = destinations;
        this.destinationPaths = null;
        this.hashValue = 0;
        this.isPattern = false;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < destinations.length; i++) {
            if (i != 0) {
                sb.append(COMPOSITE_SEPERATOR);
            }
            if (getDestinationType() == destinations[i].getDestinationType()) {
                sb.append(destinations[i].getPhysicalName());
            } else {
                sb.append(destinations[i].getQualifiedName());
            }
        }
        physicalName = sb.toString();
    }

    public String getQualifiedName() {
        if (isComposite()) {
            return physicalName;
        }
        return getQualifiedPrefix() + physicalName;
    }

    protected abstract String getQualifiedPrefix();

    /**
     * @openwire:property version=1
     */
    public String getPhysicalName() {
        return physicalName;
    }

    public void setPhysicalName(String physicalName) {
        physicalName = physicalName.trim();
        final int length = physicalName.length();

        if (physicalName.isEmpty()) {
            throw new IllegalArgumentException("Invalid destination name: a non-empty name is required");
        }

        // options offset
        int p = -1;
        boolean composite = false;
        for (int i = 0; i < length; i++) {
            char c = physicalName.charAt(i);
            if (c == '?') {
                p = i;
                break;
            }
            if (c == COMPOSITE_SEPERATOR) {
                // won't be wild card
                isPattern = false;
                composite = true;
            } else if (!composite && (c == '*' || c == '>')) {
                isPattern = true;
            }
        }

        // Strip off any options
        if (p >= 0) {
            String optstring = physicalName.substring(p + 1);
            physicalName = physicalName.substring(0, p);
            try {
                options = parseQuery(optstring);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid destination name: " + physicalName + ", it's options are not encoded properly: " + e);
            }
        }

        this.physicalName = physicalName;
        this.destinationPaths = null;
        this.hashValue = 0;
        if (composite) {
            // Check to see if it is a composite.
            Set<String> l = new HashSet<String>();
            StringTokenizer iter = new StringTokenizer(physicalName, "" + COMPOSITE_SEPERATOR);
            while (iter.hasMoreTokens()) {
                String name = iter.nextToken().trim();
                if (name.length() == 0) {
                    continue;
                }
                l.add(name);
            }
            compositeDestinations = new OpenWireDestination[l.size()];
            int counter = 0;
            for (String dest : l) {
                compositeDestinations[counter++] = createDestination(dest);
            }
        }
    }

    public OpenWireDestination createDestination(String name) {
        return createDestination(name, getDestinationType());
    }

    public String[] getDestinationPaths() {
        if (destinationPaths != null) {
            return destinationPaths;
        }

        List<String> l = new ArrayList<String>();
        StringBuilder level = new StringBuilder();
        final char separator = PATH_SEPERATOR.charAt(0);
        for (char c : physicalName.toCharArray()) {
            if (c == separator) {
                l.add(level.toString());
                level.delete(0, level.length());
            } else {
                level.append(c);
            }
        }
        l.add(level.toString());

        destinationPaths = new String[l.size()];
        l.toArray(destinationPaths);
        return destinationPaths;
    }

    public abstract byte getDestinationType();

    public boolean isQueue() {
        return false;
    }

    public boolean isTopic() {
        return false;
    }

    public boolean isTemporary() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OpenWireDestination d = (OpenWireDestination)o;
        return physicalName.equals(d.physicalName);
    }

    @Override
    public int hashCode() {
        if (hashValue == 0) {
            hashValue = physicalName.hashCode();
        }
        return hashValue;
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

    public String getDestinationTypeAsString() {
        switch (getDestinationType()) {
        case QUEUE_TYPE:
            return "Queue";
        case TOPIC_TYPE:
            return "Topic";
        case TEMP_QUEUE_TYPE:
            return "TempQueue";
        case TEMP_TOPIC_TYPE:
            return "TempTopic";
        default:
            throw new IllegalArgumentException("Invalid destination type: " + getDestinationType());
        }
    }

    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public boolean isMarshallAware() {
        return false;
    }

    public boolean isPattern() {
        return isPattern;
    }

    public static UnresolvedDestinationTransformer getUnresolvableDestinationTransformer() {
        return unresolvableDestinationTransformer;
    }

    public static void setUnresolvableDestinationTransformer(UnresolvedDestinationTransformer unresolvableDestinationTransformer) {
        OpenWireDestination.unresolvableDestinationTransformer = unresolvableDestinationTransformer;
    }

    private static Map<String, String> parseQuery(String uri) throws Exception {
        if (uri != null) {
            Map<String, String> rc = new HashMap<String, String>();
            if (uri != null) {
                String[] parameters = uri.split("&");
                for (int i = 0; i < parameters.length; i++) {
                    int p = parameters[i].indexOf("=");
                    if (p >= 0) {
                        String name = URLDecoder.decode(parameters[i].substring(0, p), "UTF-8");
                        String value = URLDecoder.decode(parameters[i].substring(p + 1), "UTF-8");
                        rc.put(name, value);
                    } else {
                        rc.put(parameters[i], null);
                    }
                }
            }
            return rc;
        }
        return Collections.emptyMap();
    }
}
