/*
 * *****************************************************************************
 * Copyright (c) 2016
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */

package com.exalttech.trex.core;

import com.cisco.trex.stateless.TRexClient;
import com.cisco.trex.stateless.exception.TRexConnectionException;
import com.cisco.trex.stateless.util.IDataCompressor;
import com.exalttech.trex.application.TrexApp;
import com.exalttech.trex.remote.exceptions.IncorrectRPCMethodException;
import com.exalttech.trex.remote.exceptions.InvalidRPCResponseException;
import com.exalttech.trex.remote.models.common.RPCError;
import com.exalttech.trex.remote.models.common.RPCRequest;
import com.exalttech.trex.remote.models.params.GetPortStatusParams;
import com.exalttech.trex.remote.models.params.Params;
import com.exalttech.trex.remote.models.profiles.Profile;
import com.exalttech.trex.ui.models.Port;
import com.exalttech.trex.ui.views.logs.LogType;
import com.exalttech.trex.ui.views.logs.LogsController;
import com.exalttech.trex.util.Constants;
import com.exalttech.trex.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xored.javafx.packeteditor.scapy.ScapyServerClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import org.apache.log4j.Logger;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import org.zeromq.ZPoller;
import zmq.ZError;

import javax.naming.SizeLimitExceededException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


public class ConnectionManager {

    public static final int MAX_REQUEST_SIZE = 999999; //TrexRpcServerReqRes does not handle requests greater this size
    private static final int HEADER_SIZE = 8; //4 magic bytes and 4 bytes integer (length of request) (see
    private TRexClient trexClient;
    private ScapyServerClient scapyServerClient;
    private static final Logger LOG = Logger.getLogger(ConnectionManager.class.getName());
    private static ConnectionManager instance = null;
    private static final StringProperty logProperty = new SimpleStringProperty();
    private final static String ASYNC_PASS_STATUS = "Pass";

    private final Object sendRequestMonitor = new Object();

    private final List<DisconnectListener> disconnectListeners = Collections.synchronizedList(new ArrayList<>());
    private boolean serverRestarted = false;
    private final Object serverRestartedMonitor = new Object();

    private final static int INTERNAL_TIMEOUT = 1000;
    private final static int DEFAULT_TIMEOUT = 3000;
    private IDataCompressor dataCompressor = TrexApp.injector.getInstance(IDataCompressor.class);

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
    private int timeout = DEFAULT_TIMEOUT;
    private boolean connected = false;

    private AtomicBoolean connectionTimeout = new AtomicBoolean(false);

    private ZMQ.Socket requester = null;
    private Task task;
    private ZContext context = new ZContext();
    private ZPoller poller = new ZPoller(context);
    private String connectionString;



    private ConnectionManager() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            String username = System.getProperty("user.name");
            setClientName(username + "@" + hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public String getClientName() {
        return clientName;
    }

    private void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public boolean initializeConnection(String ip,
                                        String rpcPort,
                                        String asyncPort,
                                        String scapyPort,
                                        int timeout,
                                        String clientName,
                                        boolean isReadOnly) throws TRexConnectionException {
        synchronized (serverRestartedMonitor) {
            serverRestarted = false;
        }

        connectionTimeout.set(false);

        this.ip = ip;
        this.rpcPort = rpcPort;
        this.asyncPort = asyncPort;
        this.scapyPort = scapyPort;
        this.clientName = clientName;

        if (timeout > 0) {
            this.timeout = timeout;
        }

        trexClient = new TRexClient(ip, rpcPort, clientName);
        trexClient.connect();

        return connectToZMQ();
    }

    public TRexClient getTrexClient() {
        return trexClient;
    }

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
        scapyServerClient.connect("tcp://" + ip + ":" + scapyPort, timeout);

        return true;
    }

    private ZMQ.Socket buildRequester() {
        ZMQ.Socket s = context.createSocket(ZMQ.REQ);
        s.setReceiveTimeOut(INTERNAL_TIMEOUT);
        s.setSendTimeOut(INTERNAL_TIMEOUT);
        return s;
    }

    public boolean testConnection(boolean isAsync) {
        if (isAsync) {
            return !Util.isNullOrEmpty(getAsyncResponse());
        } else {
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

    private void disconnectScapy() {
        scapyServerClient.closeConnection();
    }

    private String sendRequest(String cmd) {
        return sendRequest(cmd, null);
    }

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

            } else {
                param = "{}";
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
                        LOG.error(rpcError.getError().getSpecificOrMessage());
                        LogsController.getInstance().appendText(LogType.ERROR, rpcError.getError().getSpecificOrMessage());

                    } catch (IOException ex) {
                        LOG.warn("Error parsing response", ex);
                    }
                }
                return serversResponse;
            }
        } catch (UnsupportedEncodingException | SizeLimitExceededException ex) {
            LOG.error("Error while sending request", ex);
        }

        return null;
    }

    String sendRPCRequest(String method, Params params) throws JsonProcessingException, UnsupportedEncodingException, InvalidRPCResponseException, IncorrectRPCMethodException, SizeLimitExceededException {
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

    String sendAddStreamRequest(Profile[] profilesList) throws JsonProcessingException, UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException, SizeLimitExceededException {
        List<String> addStreamCommandList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        RPCRequest rpcRequest = new RPCRequest();
        String jsonRequestString;
        for (Profile aProfilesList : profilesList) {

            rpcRequest.setId(Util.getRandomID(Constants.RPC_REQUEST_ID_LENGTH));
            rpcRequest.setMethod(Constants.ADD_STREAM_METHOD);
            rpcRequest.setParams(aProfilesList);

            jsonRequestString = mapper.writeValueAsString(rpcRequest);
            jsonRequestString = Util.tuneJSONParams(jsonRequestString, aProfilesList, apiH);
            addStreamCommandList.add(jsonRequestString);

        }

        List<List<String>> streamGroups = packMultipleRequestsIntoGroups(addStreamCommandList);

        StringBuilder response = new StringBuilder();
        for (List<String> group : streamGroups) {
            response.append(sendStreamGroup(group)).append("\n");
        }
        return response.toString();
    }

    /**
     * Method recursively splits list of requests into several lists of requests
     * which are fit MAX_REQUEST_SIZE ({@value #MAX_REQUEST_SIZE})
     * @param requests
     * @return list of request lists which are fit size limit
     * @throws SizeLimitExceededException if there is now possibility to split lists more but there is
     * requests which are not fit (e.g. one request is greater than MAX_REQUEST_SIZE ({@value MAX_REQUEST_SIZE})
     */
    //TODO move logic to transport layer e.g. not send streams group but send commands (TRexTransport has this method
    //TODO make transport layer (or TRexClient) to be responsible for splitting and packing huge requests
    private List<List<String>> packMultipleRequestsIntoGroups(List<String> requests) throws SizeLimitExceededException {
        List<List<String>> sendingGroups = new ArrayList<>();
        sendingGroups.add(requests);

        boolean splitted = true;
        while (true) {
            boolean allFit = true;
            for (List<String> group : sendingGroups) {
                if(group.toString().getBytes().length > (MAX_REQUEST_SIZE - HEADER_SIZE)) {
                    allFit = false;
                    break;
                }
            }

            if (allFit) {
                break;
            } else if (!splitted) {
                throw new SizeLimitExceededException("There is a stream not fitting max request size");
            }

            List<List<String>> newGroups = new ArrayList<>();
            splitted = false;
            for (List<String> group : sendingGroups) {
                List<String> partA = group.subList(0, (int) Math.ceil(group.size()/2.0));
                newGroups.add(partA);
                if (group.size() == partA.size()) {
                    continue;
                }
                List<String> partB = group.subList(partA.size(), group.size());
                splitted = true;
                newGroups.add(partB);
            }

            sendingGroups = newGroups;
        }

        return sendingGroups;
    }

    private String sendStreamGroup(List<String> addStreamCommandList) throws SizeLimitExceededException, UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException {
        String requestCommand = Util.toPrettyFormat(addStreamCommandList.toString());
        LOG.info(requestCommand);
        logProperty.setValue("Sending request " + requestCommand);
        byte[] serverResponse = getServerRPCResponse(addStreamCommandList.toString());
        return handleResponse(serverResponse, false);
    }

    public String sendPortStatusRequest(List<Port> portList) throws JsonProcessingException, UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException, SizeLimitExceededException {
        List<String> addStreamCommandList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        RPCRequest rpcRequest = new RPCRequest();
        String jsonRequestString;
        for (Port port : portList) {
            rpcRequest.setId(Util.getRandomID(Constants.RPC_REQUEST_ID_LENGTH));
            rpcRequest.setMethod(Constants.PORT_STATUS_METHOD);
            rpcRequest.setParams(new GetPortStatusParams(port.getIndex(), false));
            jsonRequestString = mapper.writeValueAsString(rpcRequest);
            jsonRequestString = Util.tuneJSONParams(jsonRequestString, port.getPortParam(), apiH);
            addStreamCommandList.add(jsonRequestString);

        }
        String requestCommand = Util.toPrettyFormat(addStreamCommandList.toString());
        LOG.info("Send port status request \n " + requestCommand);
        byte[] serverResponse = getServerRPCResponse(addStreamCommandList.toString());

        return handleResponse(serverResponse, false);
    }

    public String sendPortXStatsNamesRequest(Port port) throws JsonProcessingException, UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException, SizeLimitExceededException {
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

    public String sendPortXStatsValuesRequest(Port port) throws JsonProcessingException, UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException, SizeLimitExceededException {
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

        return handleResponse(serverResponse, false);
    }

    private String handleResponse(byte[] serverResponse, boolean writeToLog) throws UnsupportedEncodingException, IncorrectRPCMethodException, InvalidRPCResponseException {
        if (serverResponse != null) {
            String rpcResponse = new String(serverResponse, "UTF-8");

            LOG.trace("Received Server response \n" + Util.toPrettyFormat(rpcResponse));

            if (writeToLog) {
                logProperty.setValue("Received Server response " + Util.toPrettyFormat(rpcResponse));
            }
            if (rpcResponse.contains("\"error\"")) {
                try {
                    rpcResponse = Util.removeFirstBrackets(rpcResponse);
                    RPCError rpcError = new ObjectMapper().readValue(rpcResponse, RPCError.class);
                    String err = rpcError.getError().getSpecificOrMessage();
                    LOG.error(err);
                    LogsController.getInstance().appendText(LogType.ERROR, err);
                    throw new IncorrectRPCMethodException(err + "\n " + Util.toPrettyFormat(rpcResponse));
                } catch (IOException ex) {
                    LOG.warn("Error parsing response", ex);
                }
            }
            return rpcResponse;
        } else {
            throw new InvalidRPCResponseException();
        }
    }

    private String getAsyncResponse() {
        String ret;
        final String address = "tcp://" + ip + ":" + asyncPort;
        LogsController.getInstance().appendText(LogType.INFO, "Connecting to Trex async port: " + address);
        final String[] error = {null};
        try {
            runAndWait(() -> {
                try {
                    ZMQ.Socket subscriber = context.createSocket(ZMQ.SUB);
                    subscriber.setReceiveTimeOut(timeout);
                    subscriber.connect(address);
                    subscriber.subscribe(ZMQ.SUBSCRIPTION_ALL);

                    String res;
                    try {
                        res = this.dataCompressor.decompressBytesToString(subscriber.recv());
                        if (res != null) {
                            handleAsyncResponse(res);
                        } else {
                            error[0] = "Error while verifing the Async request: " + "No response from server";
                        }
                    } catch (Exception e) {
                        error[0] = "Error while verifing the Async request: " + e.getMessage();
                    }
                    context.destroySocket(subscriber);
                } catch (Exception e) {
                    error[0] = "Error while verifing the Async request: " + e.getMessage();
                }
            });
        } catch (InterruptedException e) {
            error[0] = "Error while verifing the Async request: " + e.getMessage();
        }
        if (error[0] == null) {
            ret = ASYNC_PASS_STATUS;
        } else {
            LogsController.getInstance().appendText(LogType.ERROR, error[0]);
            ret = null;
        }

        // Create async task
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    ZMQ.Socket subscriber = context.createSocket(ZMQ.SUB);
                    subscriber.setReceiveTimeOut(INTERNAL_TIMEOUT);
                    subscriber.connect(address);
                    subscriber.subscribe(ZMQ.SUBSCRIPTION_ALL);

                    int failsCount = 0;
                    while (!isCancelled() && !Thread.currentThread().isInterrupted()) {
                        try {
                            final String res = dataCompressor.decompressBytesToString(subscriber.recv());
                            if (res != null) {
                                handleAsyncResponse(res);
                                failsCount = 0;
                            } else if (subscriber.base().errno() == ZError.EAGAIN) {
                                if (!isConnected()) {
                                    context.destroySocket(subscriber);
                                    return null;
                                }

                                if (failsCount > timeout / INTERNAL_TIMEOUT) {
                                    LOG.error("Connection to server is down");
                                    connectionTimeout.set(true);
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

    private static void runAndWait(Runnable action) throws InterruptedException {
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

    private void disconnectSubscriber() {
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

    private void disconnectRequester() {
        setConnected(false);
        requester.disconnect(connectionString);
        requester.close();
    }

    public String getIPAddress() {
        return ip;
    }

    public String getRpcPort() {
        return rpcPort;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public boolean isScapyConnected() {
        return scapyServerClient.isConnected();
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    private byte[] getServerRPCResponse(String request) throws SizeLimitExceededException {


        byte[] finalRequest = this.dataCompressor.compressStringToBytes(request);

        if (finalRequest.length >= MAX_REQUEST_SIZE) {
            throw new SizeLimitExceededException(MessageFormat.format("Size of request is too large (limit is {0} bytes)", MAX_REQUEST_SIZE));
        }

        byte[] serverResponse;
        boolean success;

        synchronized (sendRequestMonitor) {
            if (connectionTimeout.get()) {
                return null;
            }
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
                        int retries = timeout / INTERNAL_TIMEOUT;
                        while (serverResponse == null && retries > 0) {
                            if (connectionTimeout.get()) {
                                return null;
                            }

                            retries--;
                            serverResponse = requester.recv(0);
                        }
                        if (retries == 0 && resend(finalRequest)) {
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
            : dataCompressor.decompressBytesToString(serverResponse).getBytes();
    }

    private boolean resend(byte[] msg) {
        if (connectionTimeout.get()) {
            return false;
        }

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

    void notifyServerWasRestarted() {
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

    public interface DisconnectListener {
        void handle();
    }
}
