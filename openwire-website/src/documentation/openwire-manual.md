# OpenWire Protocol Manual

{:toc:2-5}

## The OpenWire Protocol

The OpenWire Protocol is the native binary wire protocol used by ActiveMQ and its various cross language clients.

### OpenWire protocol details
This section explains a little more about what's happening on the wire. The STOMP protocol, as it was designed,
is easy to understand and monitor since it's a text-based protocol. OpenWire, however, is binary,
and understanding the interactions that happen isn't as easy. Some clues might be helpful.

All OpenWire commands are implemented as "command" objects following the Gang of Four [Command Pattern](http://en.wikipedia.org/wiki/Command_pattern).
The structure of the objects are described [at the ActiveMQ website](http://activemq.apache.org/openwire-version-2-specification.html), but
what about the interactions?


Establishing a connection to the broker:
A connection is established between the client and the broker with the client creating a new ActiveMQConnection
(most likely using a connection factory of some sort). When a new "connection" is created, the underlying transport
mechanisms send a WireFormatInfo command to the broker. This command describes what version and configurations of the OpenWire protocol
the client wishes to use. For example, some of the configuration options are the ones listed above that can also be
configured on the broker.

When the TCP connection is handled on the broker side, it sends a WireFormatInfo to the client. The purpose of exchanging
these WireFormatInfo commands is to be able to negotiate what settings to use as each the client and the server has
their own preferred settings. The lowest protocol version between the two is used. When the broker receives the client's
WireFormatInfo command, it negotiates the differences on its side and then sends a BrokerInfo command. Conversely
on the client, when it receives the broker's WireFormatInfo, it negotiates it and sends a ConnectionInfo command. When
the broker receives a ConnectionInfo command, it will either ack it with a Response command, or use security settings established globally
for the broker or for a given virtual host to determine whether connections are allowed. If a connection is not allowed
to the broker or to to virtual host, the broker will kill the connection.

### OpenWire features to be documented

* Flow Control
* Persistent Messaging
* Message Expiration

### Unsupported OpenWire features:

You will get bad/undefined behaviour if you try to use any of the following OpenWire features:

* XA transactions
* [Message Groups using JMSXGroupID](http://activemq.apache.org/message-groups.html)
* [Subscription recovery/retroactive consumer](http://activemq.apache.org/retroactive-consumer.html)
* [Exclusive Consumer with Priority](http://activemq.apache.org/exclusive-consumer.html)
* [Virtual Destinations](http://activemq.apache.org/virtual-destinations.html)

You can use Durable Subscriptions and/or [Mirrored Queues](user-manual.html#Mirrored_Queues) to get
the same/similar behaviour that [Virtual Destinations](http://activemq.apache.org/virtual-destinations.html) provide.

<!-- The following are not really OpenWire features.. but just general brokers features.
* [Network of brokers](http://activemq.apache.org/networks-of-brokers.html)
* [Shared-state Master/Slave](http://activemq.apache.org/shared-file-system-master-slave.html)
* [Startup Destinations](http://activemq.apache.org/configure-startup-destinations.html)
* [Delete inactive dests](http://activemq.apache.org/delete-inactive-destinations.html)
* [JMX](http://activemq.apache.org/jmx.html)
-->
