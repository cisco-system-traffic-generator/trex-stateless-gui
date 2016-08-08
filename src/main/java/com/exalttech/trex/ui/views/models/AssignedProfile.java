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
/*



 */
package com.exalttech.trex.ui.views.models;

import com.exalttech.trex.remote.models.validate.Rate;

/**
 * Model that present assigned port profile
 *
 * @author GeorgeKh
 */
public class AssignedProfile {

    String profileName;

    ProfileMultiplier multiplier;

    boolean profileAssigned;

    boolean streamStarted;

    boolean hasDuration;

    Rate rate;

    boolean allStreamsWithLatency;

    /**
     *
     */
    public AssignedProfile() {
        profileName = "";
        multiplier = new ProfileMultiplier();
        profileAssigned = false;
        streamStarted = false;
        hasDuration = false;
        allStreamsWithLatency = false;
    }

    /**
     * Get profile name
     *
     * @return
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * Set profile name
     *
     * @param profileName
     */
    public void setProfileName(String profileName) {
        this.profileName = profileName;
        setProfileAssigned(true);
        setStreamStarted(false);
    }

    /**
     * Return profile multiplier
     *
     * @return
     */
    public ProfileMultiplier getMultiplier() {
        return multiplier;
    }

    /**
     * Set profile multiplier
     *
     * @param multiplier
     */
    public void setMultiplier(ProfileMultiplier multiplier) {
        this.multiplier = multiplier;
    }

    /**
     *
     * @return
     */
    public boolean isProfileAssigned() {
        return profileAssigned;
    }

    /**
     *
     * @param profileAssigned
     */
    public void setProfileAssigned(boolean profileAssigned) {
        this.profileAssigned = profileAssigned;
    }

    /**
     *
     * @return
     */
    public boolean isStreamStarted() {
        return streamStarted;
    }

    /**
     *
     * @param streamStarted
     */
    public void setStreamStarted(boolean streamStarted) {
        this.streamStarted = streamStarted;
    }

    /**
     *
     * @param rate
     */
    public void setRate(Rate rate) {
        this.rate = rate;
    }

    /**
     *
     * @return
     */
    public Rate getRate() {
        return rate;
    }

    /**
     *
     * @param hasDuration
     */
    public void setHasDuration(boolean hasDuration) {
        this.hasDuration = hasDuration;
    }

    /**
     *
     * @return
     */
    public boolean isHasDuration() {
        return hasDuration;
    }

    /**
     * Return latency profile streams indicator
     * @return 
     */
    public boolean isAllStreamsWithLatency() {
        return allStreamsWithLatency;
    }

    /**
     * Set latency profile streams indicator
     * @param allStreamsWithLatency 
     */
    public void setAllStreamsWithLatency(boolean allStreamsWithLatency) {
        this.allStreamsWithLatency = allStreamsWithLatency;
    }
    
}
