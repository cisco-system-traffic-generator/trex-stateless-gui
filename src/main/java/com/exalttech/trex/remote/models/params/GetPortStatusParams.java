package com.exalttech.trex.remote.models.params;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "port_id",
        "block"
})
public class GetPortStatusParams extends Params {
    @JsonProperty("port_id")
    private Integer portId;
    @JsonProperty("block")
    private Boolean block;

    public GetPortStatusParams(Integer portId, Boolean block) {
        this.portId = portId;
        this.block = block;
    }

    @JsonProperty("block")
    public Boolean getBlock() {
        return block;
    }

    @JsonProperty("block")
    public void setBlock(Boolean block) {
        this.block = block;
    }

    @JsonProperty("port_id")
    public Integer getPortId() {
        return portId;
    }

    @JsonProperty("port_id")
    public void setPortId(Integer portId) {
        this.portId = portId;
    }
}