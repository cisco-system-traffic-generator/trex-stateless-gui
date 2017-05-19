package com.exalttech.trex.remote.models.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;


public class RPCResult {
    @JsonProperty("id")
    private String id;
    @JsonProperty("jsonrpc")
    private String jsonrpc;
    @JsonProperty("result")
    private String result;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(final String id) {
        this.id = id;
    }

    @JsonProperty("jsonrpc")
    public String getJsonrpc() {
        return jsonrpc;
    }

    @JsonProperty("jsonrpc")
    public void setJsonrpc(final String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    @JsonProperty("result")
    public String getResult() {
        return result;
    }

    @JsonProperty("result")
    public void setResult(final JsonNode result) {
        this.result = result.toString();
    }
}
