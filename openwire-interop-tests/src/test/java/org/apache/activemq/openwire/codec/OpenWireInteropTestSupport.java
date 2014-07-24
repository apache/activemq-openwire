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
package org.apache.activemq.openwire.codec;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.JMSException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.apache.activemq.openwire.codec.OpenWireFormatFactory;
import org.apache.activemq.openwire.commands.BrokerInfo;
import org.apache.activemq.openwire.commands.Command;
import org.apache.activemq.openwire.commands.KeepAliveInfo;
import org.apache.activemq.openwire.commands.Message;
import org.apache.activemq.openwire.commands.MessageDispatch;
import org.apache.activemq.openwire.commands.Response;
import org.apache.activemq.openwire.commands.ShutdownInfo;
import org.apache.activemq.openwire.commands.WireFormatInfo;
import org.apache.activemq.openwire.util.TcpTransport;
import org.apache.activemq.openwire.util.TransportListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class used in testing the interoperability between the OpenWire
 * commands and Marshalers in this library and those in ActiveMQ.
 */
public abstract class OpenWireInteropTestSupport implements TransportListener {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWireInteropTestSupport.class);

    @Rule public TestName name = new TestName();

    protected BrokerService brokerService;

    private TcpTransport transport;
    protected URI connectionURI;

    private OpenWireFormatFactory factory;
    protected OpenWireFormat wireFormat;

    private CountDownLatch connected;

    private WireFormatInfo remoteWireformatInfo;
    private BrokerInfo remoteInfo;
    private Exception failureCause;
    private final AtomicInteger requestIdGenerator = new AtomicInteger(1);

    private final Map<Integer, CountDownLatch> requestMap =
        new ConcurrentHashMap<Integer, CountDownLatch>();

    protected Command latest;
    protected final Queue<Message> messages = new LinkedList<Message>();

    @Before
    public void setUp() throws Exception {
        brokerService = createBroker();
        brokerService.start();
        brokerService.waitUntilStarted();

        factory = new OpenWireFormatFactory();
        factory.setVersion(getOpenWireVersion());
        factory.setCacheEnabled(false);
        factory.setTightEncodingEnabled(isTightEncodingEnabled());

        wireFormat = factory.createWireFormat();
    }

    @After
    public void tearDown() throws Exception {
        disconnect();

        if (brokerService != null) {
            brokerService.stop();
            brokerService.waitUntilStopped();
        }
    }

    protected abstract int getOpenWireVersion();

    protected abstract boolean isTightEncodingEnabled();

    protected void connect() throws Exception {
        connected = new CountDownLatch(1);

        transport = new TcpTransport(wireFormat, connectionURI);
        transport.setTransportListener(this);
        transport.start();

        transport.oneway(wireFormat.getPreferedWireFormatInfo());
    }

    protected void disconnect() throws Exception {
        if (transport != null && transport.isStarted()) {
            ShutdownInfo done = new ShutdownInfo();
            transport.oneway(done);
            Thread.sleep(50);
            transport.stop();
        }
    }

    protected boolean request(Command command, long timeout, TimeUnit units) throws Exception {
        command.setCommandId(requestIdGenerator.getAndIncrement());
        command.setResponseRequired(true);
        CountDownLatch complete = new CountDownLatch(1);
        requestMap.put(new Integer(command.getCommandId()), complete);
        transport.oneway(command);
        return complete.await(timeout, units);
    }

    protected boolean awaitConnected(long time, TimeUnit unit) throws InterruptedException {
        return connected.await(time, unit);
    }

    protected BrokerService createBroker() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setPersistent(false);
        brokerService.setAdvisorySupport(false);
        brokerService.setDeleteAllMessagesOnStartup(true);
        brokerService.setUseJmx(true);

        TransportConnector connector = brokerService.addConnector("tcp://0.0.0.0:0?transport.trace=true&trace=true");
        connectionURI = connector.getPublishableConnectURI();
        LOG.debug("Using openwire port: {}", connectionURI);
        return brokerService;
    }

    @Override
    public void onCommand(Object command) {
        try {
            if (command instanceof WireFormatInfo) {
                handleWireFormatInfo((WireFormatInfo) command);
            } else if (command instanceof KeepAliveInfo) {
                handleKeepAliveInfo((KeepAliveInfo) command);
            } else if (command instanceof BrokerInfo) {
                handleBrokerInfo((BrokerInfo) command);
            } else if (command instanceof Response) {
                Response response = (Response) command;
                this.latest = response;
                LOG.info("Received response for request: {}, response = {}", response.getCorrelationId(), latest);
                CountDownLatch done = requestMap.get(response.getCorrelationId());
                if (done != null) {
                    done.countDown();
                }
            } else if (command instanceof MessageDispatch) {
                LOG.info("Received new MessageDispatch: {}", command);
                MessageDispatch dispatch = (MessageDispatch) command;
                messages.add(dispatch.getMessage());
            } else {
                LOG.info("Received unknown command: {}", command);
            }
        } catch (Exception e) {
            failureCause = e;
        }
    }

    @Override
    public void onException(IOException error) {
        failureCause = error;
    }

    @Override
    public void transportInterupted() {
    }

    @Override
    public void transportResumed() {
    }

    public WireFormatInfo getRemoteWireFormatInfo() {
        return this.remoteWireformatInfo;
    }

    public BrokerInfo getRemoteBrokerInfo() {
        return this.remoteInfo;
    }

    public Command getLastCommandReceived() {
        return this.latest;
    }

    public boolean isFailed() {
        return this.failureCause != null;
    }

    protected void handleWireFormatInfo(WireFormatInfo info) throws Exception {
        LOG.info("Received remote WireFormatInfo: {}", info);
        this.remoteWireformatInfo = info;

        if (LOG.isDebugEnabled()) {
            LOG.debug(this + " before negotiation: " + wireFormat);
        }
        if (!info.isValid()) {
            onException(new IOException("Remote wire format magic is invalid"));
        } else if (info.getVersion() < getOpenWireVersion()) {
            onException(new IOException("Remote wire format (" + info.getVersion() +
                        ") is lower the minimum version required (" + getOpenWireVersion() + ")"));
        }

        wireFormat.renegotiateWireFormat(info);
        if (LOG.isDebugEnabled()) {
            LOG.debug(this + " after negotiation: " + wireFormat);
        }

        connected.countDown();
    }

    protected void handleKeepAliveInfo(KeepAliveInfo info) throws Exception {
        LOG.info("Received remote KeepAliveInfo: {}", info);
        if (info.isResponseRequired()) {
            KeepAliveInfo response = new KeepAliveInfo();
            transport.oneway(response);
        }
    }

    protected void handleBrokerInfo(BrokerInfo info) throws Exception {
        LOG.info("Received remote BrokerInfo: {}", info);
        this.remoteInfo = info;
    }

    protected QueueViewMBean getProxyToQueue(String name) throws MalformedObjectNameException, JMSException {
        ObjectName queueViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName="+name);
        QueueViewMBean proxy = (QueueViewMBean) brokerService.getManagementContext()
                .newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true);
        return proxy;
    }

    protected QueueViewMBean getProxyToTopic(String name) throws MalformedObjectNameException, JMSException {
        ObjectName queueViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Topic,destinationName="+name);
        QueueViewMBean proxy = (QueueViewMBean) brokerService.getManagementContext()
                .newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true);
        return proxy;
    }
}
