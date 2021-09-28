package org.jarvis.varys.client;

import org.apache.zookeeper.Version;
import org.jarvis.varys.core.VarysChannel;
import org.jarvis.varys.client.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public abstract class AbstractClient {
    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

    private volatile boolean closing;

    private volatile boolean closed;
    private final Lock connectLock = new ReentrantLock();


    public AbstractClient() throws RemotingException {
        try {
            doOpen();
        } catch (Throwable t) {
            close();
            throw new RemotingException(null, null, "Failed to start " + getClass().getSimpleName() + " connect to the server " + getRemoteAddress() + ", cause: " + t.getMessage(), t);
        }

        try {
            connect();
            if (logger.isInfoEnabled()) {
                logger.info("Start " + getClass().getSimpleName() + " connect to the server " + getRemoteAddress());
            }
        } catch (RemotingException t) {
            logger.warn("Failed to start " + getClass().getSimpleName() + " connect to the server " + getRemoteAddress() + " (check == false, ignore and retry later!), cause: " + t.getMessage(), t);
        } catch (Throwable t) {
            close();
            throw new RemotingException(null, null, "Failed to start " + getClass().getSimpleName() + " connect to the server " + getRemoteAddress() + ", cause: " + t.getMessage(), t);
        }
    }

    public InetSocketAddress getRemoteAddress() {
        VarysChannel channel = getChannel();
        return channel.getRemoteAddress();
    }

    public InetSocketAddress getLocalAddress() {
        VarysChannel channel = getChannel();
        return channel.getLocalAddress();
    }

    public boolean isConnected() {
        VarysChannel channel = getChannel();
        if (channel == null) {
            return false;
        }
        return channel.isConnected();
    }

    public boolean isClosed() {
        return closed;
    }

    protected void connect() throws RemotingException {
        connectLock.lock();

        try {
            if (isConnected()) {
                return;
            }

            if (isClosed()) {
                logger.warn("No need to connect to server " + getRemoteAddress() + " from " + getClass().getSimpleName() + " using dubbo version " + Version.getVersion() + ", cause: client status is closed or closing.");
                return;
            }

            doConnect();

            if (!isConnected()) {
                throw new RemotingException(null, "Failed connect to server " + getRemoteAddress() + " from " + getClass().getSimpleName());

            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("Successed connect to server " + getRemoteAddress() + " from " + getClass().getSimpleName() + ", channel is " + this.getChannel());
                }
            }

        } catch (RemotingException e) {
            throw e;

        } catch (Throwable e) {
            throw new RemotingException(null, "Failed connect to server " + getRemoteAddress() + " from " + getClass().getSimpleName() + " , cause: " + e.getMessage(), e);

        } finally {
            connectLock.unlock();
        }
    }

    public void close() {
        if (isClosed()) {
            logger.warn("No need to close connection to server " + getRemoteAddress() + " from " + getClass().getSimpleName() +
                    " using dubbo version " + Version.getVersion() + ", cause: the client status is closed.");
            return;
        }

        connectLock.lock();
        try {
            if (isClosed()) {
                logger.warn("No need to close connection to server " + getRemoteAddress() + " from " + getClass().getSimpleName() +
                        " using dubbo version " + Version.getVersion() + ", cause: the client status is closed.");
                return;
            }


            try {
                disconnect();
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }

            try {
                doClose();
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }

        } finally {
            connectLock.unlock();
        }
    }

    public void disconnect() {
        connectLock.lock();
        try {
            try {
                VarysChannel channel = getChannel();
                if (channel != null) {
                    //channel.close();
                }
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
            try {
                doDisConnect();
            } catch (Throwable e) {
                logger.warn(e.getMessage(), e);
            }
        } finally {
            connectLock.unlock();
        }
    }

    public void close(int timeout) {
        close();
    }

    /**
     * Open client.
     *
     * @throws Throwable
     */
    protected abstract void doOpen() throws Throwable;

    /**
     * Close client.
     *
     * @throws Throwable
     */
    protected abstract void doClose() throws Throwable;

    /**
     * Connect to server.
     *
     * @throws Throwable
     */
    protected abstract void doConnect() throws Throwable;

    /**
     * disConnect to server.
     *
     * @throws Throwable
     */
    protected abstract void doDisConnect() throws Throwable;

    /**
     * Get the connected channel.
     *
     * @return channel
     */
    protected abstract VarysChannel getChannel();
}
