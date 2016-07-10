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
package com.exalttech.trex.ui.models.datastore;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Profile wrapper model
 *
 * @author Georgekh
 */
@XmlRootElement(name = "traffic-profiles")
public class ProfilesWrapper {

    private List<Profiles> profiles;

    /**
     *
     */
    public ProfilesWrapper() {
        // default constructor
    }

    /**
     *
     * @param profiles
     */
    public ProfilesWrapper(List<Profiles> profiles) {
        this.profiles = profiles;
    }

    /**
     * Return profiles list
     *
     * @return
     */
    @XmlElement(name = "profile")
    public List<Profiles> getProfiles() {
        return profiles;
    }

    /**
     * Set profiles list
     *
     * @param profiles
     */
    public void setProfiles(List<Profiles> profiles) {
        this.profiles = profiles;
    }

}
