package com.exalttech.trex.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TrexEvent {
    private TrexEventType type;
    
    private String name;
    
    private JsonObject data;

    public TrexEventType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public JsonObject getData() {
        return data;
    }

    public TrexEvent(int type, String name, JsonObject data) {
        
        switch (type) {
            case 0:
                this.type = TrexEventType.PORT_STARTED;
                break;
            case 1:
                this.type = TrexEventType.PORT_STOPPED;
                break;
            case 2:
                this.type = TrexEventType.PORT_PAUSED;
                break;
            case 3:
                this.type = TrexEventType.PORT_RESUMED;
                break;
            case 4:
                this.type = TrexEventType.PORT_FINISHED_TX;
                break;
            case 5:
                this.type = TrexEventType.PORT_ACQUIRED;
                break;
            case 6:
                this.type = TrexEventType.PORT_RELEASED;
                break;
            case 7:
                this.type = TrexEventType.PORT_ERROR;
                break;
            case 8:
                this.type = TrexEventType.PORT_ATTR_CHANGED;
                break;
            case 100:
                this.type = TrexEventType.SERVER_STOPPED;
            default:
                this.type = TrexEventType.UNKNOWN_TYPE;
        }
        
        this.name = name;
        this.data = data;
        
        
    }

    public boolean is(TrexEventType type) {
        return this.type.equals(type);
    }

    public String getUser() {
        JsonElement who = data.get("who"); 
        return who != null ? who.getAsString() : null;
    }
}
