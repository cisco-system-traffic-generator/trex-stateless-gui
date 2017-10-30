/**
 * *****************************************************************************
 * Copyright (c) 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************
 */
package com.exalttech.trex.core;

import com.cisco.trex.stateless.TRexClient;
import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.remote.exceptions.IncorrectRPCMethodException;
import com.exalttech.trex.remote.exceptions.InvalidRPCResponseException;
import com.exalttech.trex.remote.models.common.RPCError;
import com.exalttech.trex.remote.models.common.RPCRequest;
import com.exalttech.trex.remote.models.params.Params;
import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.views.logs.LogType;
import com.exalttech.trex.ui.views.logs.LogsController;
import com.exalttech.trex.util.CompressionUtils;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xored.javafx.packeteditor.scapy.ScapyServerClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import org.apache.log4j.Logger;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import org.zeromq.ZPoller;
import zmq.ZError;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;

/**
 *
 * @author GeorgeKh
 */
public class ConnectionManager {

    private TRexClient trexClient;
    private ScapyServerClient scapyServerClient;
    private static final Logger LOG = Logger.getLogger(ConnectionManager.class.getName());
    private static ConnectionManager instance = null;
    private static StringProperty logProperty = new SimpleStringProperty();
    private final static String ASYNC_PASS_STATUS = "Pass";

    private final String MAGIC_STRING = "ABE85CEA";
    private final Object sendRequestMonitor = new Object();

    private final List<DisconnectListener> disconnectListeners = Collections.synchronizedList(new ArrayList<>());
    private boolean serverRestarted = false;
    private final Object serverRestartedMonitor = new Object();

    private final static int DEFAULT_TIMEOUT = 3000;

    /**
     *
     * @return
     */
    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
            instance.scapyServerClient = TrexApp.injector.getInstance(ScapyServerClient.class);
        }
        return instance;
    }

    private String apiH;
    private String clientName;

    private String ip;
    private String rpcPort;
    private String asyncPort;
    private String scapyPort;
    private boolean connected = false;

    private ZMQ.Socket requester = null;
    private boolean isReadOnly;
    private Task task;
    private ZContext context = new ZContext();;
    private ZPoller poller = new ZPoller(context);
    private String connectionString;

    /**
     *
     */
    protected ConnectionManager() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            String username = System.getProperty("user.name");
            setClientName(username + "@" + hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public String getClientName() {
        return clientName;
    }

    /**
     *
     * @param clientName
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     *
     * @return
     */
    public String getIp() {
        return ip;
    }

    /**
     *
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     *
     * @return
     */
    public boolean isIsReadOnly() {
        return isReadOnly;
    }

    /**
     *
     * @param isReadOnly
     */
    public void setIsReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    /**
     *
     * @param ip
     * @param rpcPort
     * @param asyncPort
     * @param clientName
     * @param isReadOnly
     * @return
     */
    public boolean initializeConnection(String ip, String rpcPort, String asyncPort, String scapyPort, String clientName, boolean isReadOnly) throws TRexConnectionException {
        synchronized (serverRestartedMonitor) {
            serverRestarted = false;
        }

        this.ip = ip;
        this.rpcPort = rpcPort;
        this.asyncPort = asyncPort;
        this.scapyPort = scapyPort;
        this.clientName = clientName;
        this.isReadOnly = isReadOnly;

        trexClient = new TRexClient(ip, rpcPort, clientName);
        trexClient.connect();
        // connect to zmq
        return connectToZMQ();
    }

    public TRexClient getTrexClient() {
        return trexClient;
    }

    /**
     *
     */
    private boolean connectToZMQ() {
        connectionString = "tcp://" + ip + ":" + rpcPort;
        try {
            LogsController.getInstance().appendText(LogType.INFO, "Connecting to Trex server: " + connectionString);
            requester = buildRequester();
            requester.connect(connectionString);
            poller.register(requester, ZMQ.Poller.POLLIN);
            LogsController.getInstance().appendText(LogType.INFO, "Connected");
        } catch (Exception ex) {
            LOG.error("Invalid hostname", ex);
            return false;
        }

        // Just try to connect but don't account
        scapyServerClient.connect("tcp://" + ip +":"+ scapyPort, DEFAULT_TIMEOUT);

        return true;
    }

    private ZMQ.Socket buildRequester() {
        ZMQ.Socket s = context.createSocket(ZMQ.REQ);
        s.setReceiveTimeOut(DEFAULT_TIMEOUT);
        s.setSendTimeOut(DEFAULT_TIMEOUT);
        return s;
    }

    /**
     *
     * @param isAsync
     * @return
     */
    public boolean testConnection(boolean isAsync) {
        if (isAsync) {
            return !Util.isNullOrEmpty(getAsyncResponse());
        }
        else {
            if (!connectTrex()) {
                return false;
            }
        }
        // Just try to connect, but don't take into the account
        disconnectScapy();
        connectScapy();

        return true;
    }

    private boolean connectTrex() {
        if (sendRequest("ping") != null) {
            return true;
        }
        LogsController.getInstance().appendText(LogType.ERROR, "Trex server is unreachable");
        return false;
    }

    public boolean connectScapy() {
        return connectScapy(ip, scapyPort);
    }

    public boolean connectScapy(String scapy_ip, String scapy_port) {
        LogsController.getInstance().appendText(LogType.INFO, "Connecting to Scapy server: " + "tcp://" + scapy_ip + ":" + scapy_port);
        scapyServerClient.connect("tcp://" + scapy_ip + ":" + scapy_port, DEFAULT_TIMEOUT);
        if (scapyServerClient.isConnected()) {
            LogsController.getInstance().appendText(LogType.INFO, "Connected");
            return true;
        }
        LogsController.getInstance().appendText(LogType.ERROR, "Scapy server is unreachable");
        return false;
    }

    public void disconnectScapy() {
        scapyServerClient.closeConnection();
    }

    /**
     *
     * Send request without Parameters
     *
     * @param cmd
     * @return
     */
    public String sendRequest(String cmd) {
        return sendRequest(cmd, null);
    }

    /**
     *
     * Send request with Parameters
     *
     * @param cmd
     * @param parameters
     * @return
     */
    public String sendRequest(String cmd, String parameters) {

        try {
            String param = parameters;
            ObjectMapper mapper = new ObjectMapper();
            if (parameters != null) {
                String apiHParam = "\"api_h\": \"" + apiH + "\"";
                if ("".equals(parameters)) {
                    param = "{\"api_h\": \"" + apiH + "\"}";
                } else if ("api_sync".equals(cmd)) {
                    param = "{" + parameters + "}";
                } else {
                    param = "{" + apiHParam + " , " + parameters + "}";
                }

            }
            String request = "{   \"id\" : \"aggogxls\",   \"jsonrpc\" : \"2.0\",   \"method\" : \"" + cmd + "\",   \"params\" :" + param + " }";
            LOG.trace("Sending request \n" + Util.toPrettyFormat(request));
            logProperty.setValue("Sending request " + Util.toPrettyFormat(request));
            byte[] reply = getServerRPCResponse(request);

            if (reply != null) {
                String serversResponse = new String(reply, "UTF-8");
                LOG.trace("Received Server response \n" + Util.toPrettyFormat(serversResponse));
                logProperty.setValue("Received Server response " + Util.toPrettyFormat(serversResponse));
                if (serversResponse.contains("\"error\"")) {
                    try {
                        String rpcResponse = Util.removeFirstBrackets(serversResponse);
                        RPCError rpcError = mapper.readValue(rpcResponse, RPCError.class);
                        LOG.error(rpcError.getError().getSpecificErr());
                        LogsController.getInstance().appendText(LogType.ERROR, rpcError.getError().getSpecificErr());

                    } catch (IOException ex) {
                        LOG.warn("Error parsing response", ex);
                    }
                }
                return serversResponse;
            }
        } catch (UnsupportedEncodingException ex) {
            LOG.error("Error while sending request", ex);
        }

        return null;
    }

    /**
     *
     * @param method
     * @param params
     * @return
     * @throws JsonProcessingException
     * @throws UnsupportedEncodingException
     * @throws InvalidRPCResponseException
     * @throws IncorrectRPCMethodException
     */
    public String sendRPCRequest(String method, Params params) throws JsonProcessingException, UnsupportedEncodingException, InvalidRPCResponseException, IncorrectRPCMethodException {
        RPCRequest rpcRequest = new RPCRequest();
        ObjectMapper mapper = new ObjectMapper();
        rpcRequest.setId(Util.getRandomID(Constants.RPC_REQUEST_ID_LENGTH));
        rpcRequest.setMethod(method);
        if (params != null) {
            rpcRequest.setParams(params);
        }
        String jsonRequestString = mapper.writeValueAsString(rpcRequest);
        jsonRequestString = Util.tuneJSONParams(jsonRequestString, params, apiH);
        LOG.trace("Sending request \n" + Util.toPrettyFormat(jsonRequestString));
        logProperty.setValue("Sending request " + Util.toPrettyFormat(jsonRequestString));
        byte[] serverResponse = getServerRPCResponse(jsonRequestString);
        return handleResponse(serverResponse, true);
    }

    /**
     *
     * @param profilesList
     * @return
     * @throws JsonProcessingException
     * @throws UnsupportedEncodingException
     * @throws IncorrectRPCMethodException
     * @throws InvalidRPCResponseException
     */
    public String sendAddStreamRequest(Profile[] profilesList) throws JsonProcessingException, UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException {
        List<String> addStreamCommandList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        RPCRequest rpcRequest = new RPCRequest();
        String jsonRequestString;
        for (int i = 0; i < profilesList.length; i++) {

            rpcRequest.setId(Util.getRandomID(Constants.RPC_REQUEST_ID_LENGTH));
            rpcRequest.setMethod(Constants.ADD_STREAM_METHOD);
            rpcRequest.setParams(profilesList[i]);

            jsonRequestString = mapper.writeValueAsString(rpcRequest);
            jsonRequestString = Util.tuneJSONParams(jsonRequestString, profilesList[i], apiH);
            addStreamCommandList.add(jsonRequestString);

        }
        String requestCommand = Util.toPrettyFormat(addStreamCommandList.toString());
        LOG.info(requestCommand);
        logProperty.setValue("Sending request " + requestCommand);
        byte[] serverResponse = getServerRPCResponse(addStreamCommandList.toString());
        return handleResponse(serverResponse, false);
    }

    /**
     * Send request for port status
     *
     * @param portList
     * @return
     * @throws JsonProcessingException
     * @throws UnsupportedEncodingException
     * @throws IncorrectRPCMethodException
     * @throws InvalidRPCResponseException
     */
    public String sendPortStatusRequest(List<Port> portList) throws JsonProcessingException, UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException {
        List<String> addStreamCommandList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        RPCRequest rpcRequest = new RPCRequest();
        String jsonRequestString;
        for (Port port : portList) {
            rpcRequest.setId(Util.getRandomID(Constants.RPC_REQUEST_ID_LENGTH));
            rpcRequest.setMethod(Constants.PORT_STATUS_METHOD);
            rpcRequest.setParams(port.getPortParam());

            jsonRequestString = mapper.writeValueAsString(rpcRequest);
            jsonRequestString = Util.tuneJSONParams(jsonRequestString, port.getPortParam(), apiH);
            addStreamCommandList.add(jsonRequestString);

        }
        String requestCommand = Util.toPrettyFormat(addStreamCommandList.toString());
        LOG.info("Send port status request \n " + requestCommand);
        byte[] serverResponse = getServerRPCResponse(addStreamCommandList.toString());

        return handleResponse(serverResponse, false);
    }

    /**
     * Send request for port status
     *
     * @param port
     * @return
     * @throws JsonProcessingException
     * @throws UnsupportedEncodingException
     * @throws IncorrectRPCMethodException
     * @throws InvalidRPCResponseException
     */
    public String sendPortXStatsNamesRequest(Port port) throws JsonProcessingException, UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException {
        List<String> addStreamCommandList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        RPCRequest rpcRequest = new RPCRequest();
        String jsonRequestString;

        rpcRequest.setId(Util.getRandomID(Constants.RPC_REQUEST_ID_LENGTH));
        rpcRequest.setMethod(Constants.PORT_XSTATS_NAMES_METHOD);
        rpcRequest.setParams(port.getPortParam());

        jsonRequestString = mapper.writeValueAsString(rpcRequest);
        jsonRequestString = Util.tuneJSONParams(jsonRequestString, port.getPortParam(), apiH);
        addStreamCommandList.add(jsonRequestString);

        String requestCommand = Util.toPrettyFormat(addStreamCommandList.toString());
        LOG.info("Send port xstats_names request \n " + requestCommand);

        byte[] serverResponse = getServerRPCResponse(addStreamCommandList.toString());

        return handleResponse(serverResponse, false);
    }

    /**
     * Send request for port status
     *
     * @param port
     * @return
     * @throws JsonProcessingException
     * @throws UnsupportedEncodingException
     * @throws IncorrectRPCMethodException
     * @throws InvalidRPCResponseException
     */
    public String sendPortXStatsValuesRequest(Port port) throws JsonProcessingException, UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException {
        List<String> addStreamCommandList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        RPCRequest rpcRequest = new RPCRequest();
        String jsonRequestString;

        rpcRequest.setId(Util.getRandomID(Constants.RPC_REQUEST_ID_LENGTH));
        rpcRequest.setMethod(Constants.PORT_XSTATS_VALUES_METHOD);
        rpcRequest.setParams(port.getPortParam());

        jsonRequestString = mapper.writeValueAsString(rpcRequest);
        jsonRequestString = Util.tuneJSONParams(jsonRequestString, port.getPortParam(), apiH);
        addStreamCommandList.add(jsonRequestString);

        String requestCommand = Util.toPrettyFormat(addStreamCommandList.toString());
        LOG.info("Send port xstats_values request \n " + requestCommand);

        byte[] serverResponse = getServerRPCResponse(addStreamCommandList.toString());

        return handleResponse(serverResponse, false, true);
    }

    public String sendUtilizationRequest() throws JsonProcessingException, UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException {
        String serverResponse = sendRequest(Constants.GET_UTILIZATION_METHOD, "");

        return serverResponse;
    }

    /**
     * Handle server response
     *
     * @param serverResponse
     * @param writeToLog
     * @return
     * @throws UnsupportedEncodingException
     * @throws IncorrectRPCMethodException
     * @throws InvalidRPCResponseException
     */
    private String handleResponse(byte[] serverResponse, boolean writeToLog) throws UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException {
        return handleResponse(serverResponse, writeToLog, true);
    }

    private String handleResponse(byte[] serverResponse, boolean writeToLog, boolean logTrace) throws UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException {
        if (serverResponse != null) {
            String rpcResponse = new String(serverResponse, "UTF-8");
            if (logTrace) {
                LOG.trace("Received Server response \n" + Util.toPrettyFormat(rpcResponse));
            }
            if (writeToLog) {
                logProperty.setValue("Received Server response " + Util.toPrettyFormat(rpcResponse));
            }
            if (rpcResponse.contains("\"error\"")) {
                try {
                    rpcResponse = Util.removeFirstBrackets(rpcResponse);
                    RPCError rpcError = new ObjectMapper().readValue(rpcResponse, RPCError.class);
                    LOG.error(rpcError.getError().getSpecificErr());
                    LogsController.getInstance().appendText(LogType.ERROR, rpcError.getError().getSpecificErr());
                    throw new IncorrectRPCMethodException(rpcError.getError().getSpecificErr() + "\n " + Util.toPrettyFormat(rpcResponse));
                } catch (IOException ex) {
                    LOG.warn("Error parsing response", ex);
                }
            }
            return rpcResponse;
        } else {
            throw new InvalidRPCResponseException();
        }
    }

    /**
     *
     * Async request management
     *
     * @return
     */
    public String getAsyncResponse() {
        String ret = null;
        final String address = "tcp://" + ip + ":" + asyncPort;
        LogsController.getInstance().appendText(LogType.INFO, "Connecting to Trex async port: " + address);
        final String[] error = {null};
        try {
            runAndWait(() -> {
                try {
                    ZMQ.Socket subscriber = context.createSocket(ZMQ.SUB);
                    subscriber.setReceiveTimeOut(DEFAULT_TIMEOUT);
                    subscriber.connect(address);
                    subscriber.subscribe(ZMQ.SUBSCRIPTION_ALL);

                    String res;
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            res = getDecompressedString(subscriber.recv());
                            if (res != null) {
                                handleAsyncResponse(res);
                                res = null;

                                context.destroySocket(subscriber);
                                return;
                            }
                            else {
                                error[0] = "Error while verifing the Async request: " + "Async responce is null";

                                context.destroySocket(subscriber);
                                return;
                            }
                        } catch (Exception e) {
                            error[0] = "Error while verifing the Async request: " + e.getMessage();

                            context.destroySocket(subscriber);
                            return;
                        }
                    }

                    context.destroySocket(subscriber);
                } catch (Exception e) {
                    error[0] = "Error while verifing the Async request: " + e.getMessage();
                }
            });
        } catch (InterruptedException e) {
            error[0] = "Error while verifing the Async request: " + e.getMessage();
        } catch (ExecutionException e) {
            error[0] = "Error while verifing the Async request: " + e.getMessage();
        }
        if (error[0] == null) {
            ret = ASYNC_PASS_STATUS;
        }
        else {
            LogsController.getInstance().appendText(LogType.ERROR, error[0]);
            ret = null;
        }

        // Create async task
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    ZMQ.Socket subscriber = context.createSocket(ZMQ.SUB);
                    subscriber.setReceiveTimeOut(DEFAULT_TIMEOUT);
                    subscriber.connect(address);
                    subscriber.subscribe(ZMQ.SUBSCRIPTION_ALL);

                    int failsCount = 0;
                    while (!isCancelled() && !Thread.currentThread().isInterrupted()) {
                        try {
                            final String res = getDecompressedString(subscriber.recv());
                            if (res != null) {
                                handleAsyncResponse(res);
                                failsCount = 0;
                            } else if (subscriber.base().errno() == ZError.EAGAIN && isConnected()) {
                                if (failsCount > 2) {
                                    LOG.error("Connection to server is down");
                                    synchronized (disconnectListeners) {
                                        disconnectListeners.forEach(DisconnectListener::handle);
                                    }
                                    break;
                                }

                                failsCount++;

                                LOG.error("Got EAGAIN while getting async TRex response");
                            }
                        } catch (Exception ex) {
                            LOG.error("Possible error while reading the Async request", ex);
                        }
                    }

                    context.destroySocket(subscriber);
                } catch (Exception ex) {
                    LOG.error("Possible error while reading the Async request", ex);
                }
                return null;
            }

        };
        new Thread(task).start();

        // return verified async port connection result
        return ret;
    }

    /**
     * Runs the specified {@link Runnable} on the
     * JavaFX application thread and waits for completion.
     *
     * @param action the {@link Runnable} to run
     * @throws NullPointerException if {@code action} is {@code null}
     */
    private static void runAndWait(Runnable action) throws InterruptedException, ExecutionException {
        if (action == null)
            throw new NullPointerException("action");

        // run synchronously on JavaFX thread
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }

        // queue on JavaFX thread and wait for completion
        final CountDownLatch doneLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                doneLatch.countDown();
            }
        });

        doneLatch.await();

    }

    /**
     * Decompressed response
     *
     * @param data
     * @return
     */
    private String getDecompressedString(byte[] data) {
        if (data==null) return null;

        // if the length is larger than 8 bytes
        if (data.length > 8) {

            // Take the first 4 bytes
            byte[] magicBytes = Arrays.copyOfRange(data, 0, 4);

            String magicString = DatatypeConverter.printHexBinary(magicBytes);

            /* check MAGIC in the first 4 bytes in case we have it, it is compressed */
            if (magicString.equals(MAGIC_STRING)) {

                // Skip another  4 bytes containing the uncompressed size of the  message
                byte[] compressedData = Arrays.copyOfRange(data, 8, data.length);

                try {
                    return new String(CompressionUtils.decompress(compressedData));
                } catch (IOException | DataFormatException ex) {
                    LOG.error("Failed to decompress data ", ex);
                }

            }

        }
        return new String(data);
    }

    /**
     * Handle async response
     *
     * @param res
     */
    private void handleAsyncResponse(String res) {
        if (res.contains(Constants.TREX_GLOBAL_TAG)) {
            AsyncResponseManager.getInstance().setTrexGlobalResponse(res);
        } else if (res.contains(Constants.TREX_EVENT)) {
            AsyncResponseManager.getInstance().setTRexEventValue(res);
        } else if (res.contains(Constants.TREX_LATENCY)) {
            AsyncResponseManager.getInstance().setTrexLatencyProperty(res);
        } else if (res.contains(Constants.TREX_FLOW_STATS)) {
            AsyncResponseManager.getInstance().setTrexFlowStatsProperty(res);
        }
    }

    /**
     *
     */
    public void disconnectSubscriber() {
        try {
            task.cancel(true);
        } catch (Exception ex) {
            // reclose if task still running
            if (task.isRunning()) {
                task.cancel();
            }
            LOG.error("Possible error while closing the Async request", ex);
        }
    }

    /**
     *
     */
    public void disconnectRequester() {
        setConnected(false);
        requester.disconnect(connectionString);
        requester.close();
    }

    /**
     *
     * @return
     */
    public String getIPAddress() {
        return ip;
    }

    /**
     *
     * @return
     */
    public String getAsyncPort() {
        return asyncPort;
    }

    public String getScapyPort() {
        return scapyPort;
    }

    /**
     *
     * @return
     */
    public String getRpcPort() {
        return rpcPort;
    }

    /**
     *
     * @return
     */
    public boolean isConnected() {
        return this.connected;
    }

    public boolean isTrexConnected() {
        return this.connected;
    }

    public boolean isScapyConnected() {
        return scapyServerClient.isConnected();
    }

    /**
     *
     * @param connected
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     *
     * @param request
     * @return
     */
    private byte[] getServerRPCResponse(String request) {
        try {
            // prepare compression header
            ByteBuffer headerByteBuffer = ByteBuffer.allocate(8);
            headerByteBuffer.put((byte) 0xAB);
            headerByteBuffer.put((byte) 0xE8);
            headerByteBuffer.put((byte) 0x5C);
            headerByteBuffer.put((byte) 0xEA);
            headerByteBuffer.putInt(request.length());
            byte[] headerBytes = headerByteBuffer.array();
            // compress request
            byte[] compressedRequest = CompressionUtils.compress(request.getBytes());
            byte[] finalRequest = concatByteArrays(headerBytes, compressedRequest);
            byte[] serverResponse = null;
            boolean success = false;
            synchronized (sendRequestMonitor) {
                try {
                    success = requester.send(finalRequest);
                } catch (ZMQException e) {
                    if (e.getErrorCode() == ZError.EFSM) {
                        success = resend(finalRequest);
                    } else {
                        throw e;
                    }
                }
                if (success) {
                    serverResponse = requester.recv(0);
                    if (serverResponse == null) {
                        if (requester.base().errno() == ZError.EAGAIN) {
                            int retries = 5;
                            while (serverResponse == null && retries > 0) {
                                retries--;
                                serverResponse = requester.recv(0);
                            }
                            if (retries == 0) {
                                resend(finalRequest);
                                serverResponse = requester.recv(0);
                            }
                        } else {
                            LOG.error("Error sending request");
                        }
                    }
                } else {
                    LOG.error("Error sending request");
                    return null;
                }
            }

            return serverResponse == null
                ? null
                : getDecompressedString(serverResponse).getBytes();
        } catch (IOException ex) {
            LOG.error("Error sending request", ex);
            return null;
        }
    }
    
    private boolean resend(byte[] msg) {
        context.destroySocket(requester);
        requester = buildRequester();
        requester.connect(connectionString);
        return requester.send(msg);
    }
    
    private byte[] concatByteArrays(byte[] firstDataArray, byte[] secondDataArray) {
        byte[] concatedDataArray = new byte[firstDataArray.length + secondDataArray.length];
        System.arraycopy(firstDataArray, 0, concatedDataArray, 0, firstDataArray.length);
        System.arraycopy(secondDataArray, 0, concatedDataArray, firstDataArray.length, secondDataArray.length);
        return concatedDataArray;
    }

    void setApiH(String apiH) {
        this.apiH = apiH;
    }

    /**
     *
     * @return
     */
    public String getApiH() {
        return apiH;
    }

    public void propagatePortHandler(int portID, String handler) {
        trexClient.updatePortHandler(portID, handler);
    }
    public void invalidatePortHandler(int portID) {
        trexClient.invalidatePortHandler(portID);
    }

    public void disconnect() {
        setConnected(false);

        disconnectSubscriber();
        disconnectRequester();
        disconnectScapy();
        getTrexClient().disconnect();

        if (poller != null) {
            try {
                poller.close();
            } catch (IOException ex) {
                LOG.error("Error poller closing", ex);
            }
            poller.destroy();
        }

        context = new ZContext();
        poller = new ZPoller(context);
    }

    public void notifyServerWasRestarted() {
        synchronized (serverRestartedMonitor) {
            if (serverRestarted) { // That means we already notified manager about server restart
                return;
            }

            serverRestarted = true;
        }

        synchronized (disconnectListeners) {
            disconnectListeners.forEach(DisconnectListener::handle);
        }
    }

    public void addDisconnectListener(final DisconnectListener listener) {
        disconnectListeners.add(listener);
    }

    public void removeDisconnectListener(final DisconnectListener listener) {
        disconnectListeners.remove(listener);
    }

    public interface DisconnectListener {
        void handle();
    }
}
