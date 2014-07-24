/**
gxfdgvdfg * Licensed to the Apache Software Foundation (ASF) under one or more
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
package org.apache.activemq.openwire.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.SocketFactory;

import org.apache.activemq.openwire.codec.OpenWireFormat;
import org.apache.activemq.transport.tcp.TcpBufferedInputStream;
import org.apache.activemq.transport.tcp.TcpBufferedOutputStream;
import org.apache.activemq.util.ServiceStopper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class TcpTransport implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(TcpTransport.class);

    protected final URI remoteLocation;
    protected final OpenWireFormat wireFormat;

    protected int connectionTimeout = 30000;
    protected int socketBufferSize = 64 * 1024;
    protected int ioBufferSize = 8 * 1024;
    protected Socket socket;
    protected DataOutputStream dataOut;
    protected DataInputStream dataIn;

    protected int minmumWireFormatVersion;
    protected SocketFactory socketFactory;
    protected final AtomicReference<CountDownLatch> stoppedLatch = new AtomicReference<CountDownLatch>();
    protected volatile int receiveCounter;

    private Thread runnerThread;
    private AtomicBoolean started = new AtomicBoolean(false);
    private AtomicBoolean stopping = new AtomicBoolean(false);
    private AtomicBoolean stopped = new AtomicBoolean(false);
    private TransportListener transportListener;

    /**
     * Connect to a remote Node - e.g. a Broker
     *
     * @param wireFormat
     * @param socketFactory
     * @param remoteLocation
     * @param localLocation
     *        - e.g. local InetAddress and local port
     * @throws IOException
     * @throws UnknownHostException
     */
    public TcpTransport(OpenWireFormat wireFormat, URI remoteLocation) throws UnknownHostException, IOException {
        this.wireFormat = wireFormat;
        this.socketFactory = SocketFactory.getDefault();
        try {
            this.socket = socketFactory.createSocket();
        } catch (SocketException e) {
            this.socket = null;
        }
        this.remoteLocation = remoteLocation;
    }

    /**
     * A one way asynchronous send
     */
    public void oneway(Object command) throws IOException {
        checkStarted();
        wireFormat.marshal(command, dataOut);
        dataOut.flush();
    }

    /**
     * reads packets from a Socket
     */
    public void run() {
        LOG.trace("TCP consumer thread for {} starting", this);
        this.runnerThread = Thread.currentThread();
        try {
            while (!isStopped()) {
                doRun();
            }
        } catch (IOException e) {
            stoppedLatch.get().countDown();
            onException(e);
        } catch (Throwable e) {
            stoppedLatch.get().countDown();
            IOException ioe = new IOException("Unexpected error occured: " + e);
            ioe.initCause(e);
            onException(ioe);
        } finally {
            stoppedLatch.get().countDown();
        }
    }

    protected void doRun() throws IOException {
        try {
            Object command = readCommand();
            doConsume(command);
        } catch (SocketTimeoutException e) {
        } catch (InterruptedIOException e) {
        }
    }

    protected Object readCommand() throws IOException {
        return wireFormat.unmarshal(dataIn);
    }

    /**
     * Configures the socket for use
     *
     * @param sock
     *        the socket
     * @throws SocketException
     *         , IllegalArgumentException if setting the options on the socket
     *         failed.
     */
    protected void initialiseSocket(Socket sock) throws SocketException, IllegalArgumentException {
        try {
            sock.setReceiveBufferSize(socketBufferSize);
            sock.setSendBufferSize(socketBufferSize);
        } catch (SocketException se) {
            LOG.warn("Cannot set socket buffer size = {}", socketBufferSize);
            LOG.debug("Cannot set socket buffer size. Reason: {}. This exception is ignored.",
                      se.getMessage(), se);
        }
    }

    public void start() throws Exception {
        if (started.compareAndSet(false, true)) {
            boolean success = false;
            stopped.set(false);
            try {
                doStart();
                success = true;
            } finally {
                started.set(success);
            }
        }
    }

    public void stop() throws Exception {
        if (stopped.compareAndSet(false, true)) {
            stopping.set(true);
            ServiceStopper stopper = new ServiceStopper();
            try {
                doStop(stopper);
            } catch (Exception e) {
                stopper.onException(this, e);
            }
            stopped.set(true);
            started.set(false);
            stopping.set(false);
            stopper.throwFirstException();
        }

        CountDownLatch countDownLatch = stoppedLatch.get();
        if (countDownLatch != null && Thread.currentThread() != this.runnerThread) {
            countDownLatch.await(1, TimeUnit.SECONDS);
        }
    }

    protected void doStart() throws Exception {
        connect();
        stoppedLatch.set(new CountDownLatch(1));

        runnerThread = new Thread(null, this, "OpenWire Test Transport: " + toString());
        runnerThread.setDaemon(false);
        runnerThread.start();
    }

    protected void connect() throws Exception {
        if (socket == null && socketFactory == null) {
            throw new IllegalStateException("Cannot connect if the socket or socketFactory have not been set");
        }

        InetSocketAddress remoteAddress = null;

        if (remoteLocation != null) {
            remoteAddress = new InetSocketAddress(remoteLocation.getHost(), remoteLocation.getPort());
        }

        socket = socketFactory.createSocket(remoteAddress.getAddress(), remoteAddress.getPort());

        initialiseSocket(socket);
        initializeStreams();
    }

    protected void doStop(ServiceStopper stopper) throws Exception {
        LOG.debug("Stopping transport {}", this);

        if (socket != null) {
            LOG.trace("Closing socket {}", socket);
            try {
                socket.close();
                LOG.debug("Closed socket {}", socket);
            } catch (IOException e) {
                LOG.debug("Caught exception closing socket {}. This exception will be ignored.", socket, e);
            }
        }
    }

    protected void initializeStreams() throws Exception {
        TcpBufferedInputStream buffIn = new TcpBufferedInputStream(socket.getInputStream(), ioBufferSize) {
            @Override
            public int read() throws IOException {
                receiveCounter++;
                return super.read();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                receiveCounter++;
                return super.read(b, off, len);
            }

            @Override
            public long skip(long n) throws IOException {
                receiveCounter++;
                return super.skip(n);
            }

            @Override
            protected void fill() throws IOException {
                receiveCounter++;
                super.fill();
            }
        };
        this.dataIn = new DataInputStream(buffIn);
        TcpBufferedOutputStream outputStream = new TcpBufferedOutputStream(socket.getOutputStream(), ioBufferSize);
        this.dataOut = new DataOutputStream(outputStream);
    }

    protected void closeStreams() throws IOException {
        if (dataOut != null) {
            dataOut.close();
        }
        if (dataIn != null) {
            dataIn.close();
        }
    }

    /**
     * Process the inbound command
     */
    public void doConsume(Object command) {
        if (command != null) {
            if (transportListener != null) {
                transportListener.onCommand(command);
            } else {
                LOG.error("No transportListener available to process inbound command: {}", command);
            }
        }
    }

    /**
     * Passes any IO exceptions into the transport listener
     */
    public void onException(IOException e) {
        if (transportListener != null) {
            try {
                transportListener.onException(e);
            } catch (RuntimeException e2) {
                // Handle any unexpected runtime exceptions by debug logging them.
                LOG.debug("Unexpected runtime exception: " + e2, e2);
            }
        }
    }

    public String getRemoteAddress() {
        if (socket != null) {
            SocketAddress address = socket.getRemoteSocketAddress();
            if (address instanceof InetSocketAddress) {
                return "tcp://" + ((InetSocketAddress) address).getAddress().getHostAddress() + ":" + ((InetSocketAddress) address).getPort();
            } else {
                return "" + socket.getRemoteSocketAddress();
            }
        }
        return null;
    }

    public int getReceiveCounter() {
        return receiveCounter;
    }

    public OpenWireFormat getWireFormat() {
        return wireFormat;
    }

    /**
     * @return true if this service has been started
     */
    public boolean isStarted() {
        return started.get();
    }

    /**
     * @return true if this service is in the process of closing
     */
    public boolean isStopping() {
        return stopping.get();
    }

    /**
     * @return true if this service is closed
     */
    public boolean isStopped() {
        return stopped.get();
    }

    /**
     * Returns the current transport listener
     */
    public TransportListener getTransportListener() {
        return transportListener;
    }

    /**
     * Registers an inbound command listener
     *
     * @param commandListener
     */
    public void setTransportListener(TransportListener commandListener) {
        this.transportListener = commandListener;
    }

    public int getMinmumWireFormatVersion() {
        return minmumWireFormatVersion;
    }

    public void setMinmumWireFormatVersion(int minmumWireFormatVersion) {
        this.minmumWireFormatVersion = minmumWireFormatVersion;
    }

    public int getSocketBufferSize() {
        return socketBufferSize;
    }

    /**
     * Sets the buffer size to use on the socket
     */
    public void setSocketBufferSize(int socketBufferSize) {
        this.socketBufferSize = socketBufferSize;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Sets the timeout used to connect to the socket
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @return the ioBufferSize
     */
    public int getIoBufferSize() {
        return this.ioBufferSize;
    }

    /**
     * @param ioBufferSize
     *        the ioBufferSize to set
     */
    public void setIoBufferSize(int ioBufferSize) {
        this.ioBufferSize = ioBufferSize;
    }

    /**
     * @return pretty print of 'this'
     */
    @Override
    public String toString() {
        return "" + (socket.isConnected() ? "tcp://" + socket.getInetAddress() + ":" + socket.getPort() + "@" + socket.getLocalPort() : remoteLocation);
    }

    protected void checkStarted() throws IOException {
        if (!isStarted()) {
            throw new IOException("The transport is not running.");
        }
    }
}
