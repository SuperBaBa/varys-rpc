package org.jarvis.varys.dto;

import java.io.Serializable;

/**
 * @author marcus
 * @date 2021/8/22-12:57
 */
public class VarysResponse implements Serializable {
    /**
     * ok.
     */
    public static final byte OK = 20;

    /**
     * client side timeout.
     */
    public static final byte CLIENT_TIMEOUT = 30;

    /**
     * server side timeout.
     */
    public static final byte SERVER_TIMEOUT = 31;

    /**
     * channel inactive, directly return the unfinished requests.
     */
    public static final byte CHANNEL_INACTIVE = 35;

    /**
     * request format error.
     */
    public static final byte BAD_REQUEST = 40;

    /**
     * response format error.
     */
    public static final byte BAD_RESPONSE = 50;

    /**
     * service not found.
     */
    public static final byte SERVICE_NOT_FOUND = 60;

    /**
     * service error.
     */
    public static final byte SERVICE_ERROR = 70;

    /**
     * internal server error.
     */
    public static final byte SERVER_ERROR = 80;

    /**
     * internal server error.
     */
    public static final byte CLIENT_ERROR = 90;


    private String requestId;
    private Exception exception;
    private Object result;
    private String serviceVersion;
    private byte status = OK;
    private String mErrorMsg;

    public VarysResponse() {
    }

    public VarysResponse(String requestId, String serviceVersion) {
        this.requestId = requestId;
        this.serviceVersion = serviceVersion;
    }

    public boolean hasException() {
        return exception != null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getmErrorMsg() {
        return mErrorMsg;
    }

    public void setmErrorMsg(String mErrorMsg) {
        this.mErrorMsg = mErrorMsg;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }
}
