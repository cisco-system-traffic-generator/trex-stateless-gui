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
package com.exalttech.trex.ui;

import com.exalttech.trex.remote.models.validate.Rate;

/**
 * Enumerator present multiplier type
 *
 * @author GeorgeKh
 */
public enum MultiplierType {

    /**
     *
     */
    percentage() {
        @Override
        public String getTitle() {
            return "% L1";
        }

        @Override
        public double getMaxRate(Rate rate) {
            return rate.getMaxLineUtil();
        }

        @Override
        public String getTooltipString() {
            return "Max line util percentage";
        }

        @Override
        public double getMinRate(Rate rate) {
            return rate.getMinLineUtil();
        }
    },
    /**
     *
     */
    bps_L1() {
        @Override
        public String getTitle() {
            return "L1 bps";
        }

        @Override
        public double getMaxRate(Rate rate) {
            return rate.getMaxBpsL1();
        }

        @Override
        public String getTooltipString() {
            return "Max L1 BPS";
        }

        @Override
        public double getMinRate(Rate rate) {
            return rate.getMinBpsL1();
        }
    },
    /**
     *
     */
    bps_L2() {
        @Override
        public String getTitle() {
            return "L2 bps";
        }

        @Override
        public double getMaxRate(Rate rate) {
            return rate.getMaxBpsL2();
        }

        @Override
        public String getTooltipString() {
            return "Max L2 BPS";
        }

        @Override
        public double getMinRate(Rate rate) {
            return rate.getMinBpsL2();
        }
    },
    /**
     *
     */
    pps() {
        @Override
        public String getTitle() {
            return "pps";
        }

        @Override
        public double getMaxRate(Rate rate) {
            return rate.getMaxPps();
        }

        @Override
        public String getTooltipString() {
            return "Max PPS value";
        }

        @Override
        public double getMinRate(Rate rate) {
            return rate.getMinPps();
        }
    };

    /**
     * Return option type title
     *
     * @return
     */
    public abstract String getTitle();

    /**
     *
     * @param rate
     * @return
     */
    public abstract double getMaxRate(Rate rate);

    /**
     * 
     * @param rate
     * @return 
     */
     public abstract double getMinRate(Rate rate);
     
    /**
     *
     * @return
     */
    public abstract String getTooltipString();

    /**
     * Return equivalent type
     *
     * @param type
     * @return
     */
    public static MultiplierType getMultiplierType(String type) {
        return MultiplierType.valueOf(type);
    }
}
