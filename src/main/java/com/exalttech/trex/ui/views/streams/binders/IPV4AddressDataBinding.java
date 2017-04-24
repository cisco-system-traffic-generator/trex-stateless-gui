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
package com.exalttech.trex.ui.views.streams.binders;

import com.exalttech.trex.ui.views.streams.builder.StreamBuilderConstants;

public class IPV4AddressDataBinding extends AddressDataBinding {
    public IPV4AddressDataBinding() {
        super();
    }

    @Override
    public void setInitialValues() {
        getSource().resetModel(StreamBuilderConstants.DEFAULT_SRC_IP_ADDRESS, "Fixed");
        getDestination().resetModel(StreamBuilderConstants.DEFAULT_DST_IP_ADDRESS, "Fixed");
    }
    
    public boolean hasInstructions() {
        String srcMode = getSource().getModeProperty().get();
        String dstMode = getSource().getModeProperty().get();
        return (!"TRex Config".equals(srcMode) && !"Fixed".equals(srcMode))
                || (!"TRex Config".equals(dstMode) && !"Fixed".equals(dstMode));

    }
}
