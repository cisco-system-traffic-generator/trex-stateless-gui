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
package com.exalttech.trex.util;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author GeorgeKh
 */
public class Constants {

    /**
     *
     */
    public static final int REFRESH_FIFTEEN_INTERVAL_SECONDS = 15;

    /**
     *
     */
    public static final int REFRESH_TWO_INTERVAL_SECONDS = 2;

    /**
     *
     */
    public static final int REFRESH_ONE_INTERVAL_SECONDS = 1;

    /**
     *
     */
    public static final int EXTRA_BYTE = 4;

    /**
     *
     */
    public static final int RPC_REQUEST_ID_LENGTH = 6;

    /**
     *
     */
    public static final List<String> PORT_STATS_ROW_NAME = Arrays.asList("Owner", "State", "Tx bps L2", "Tx pps", "Rx bps", "Rx pps", "opackets",
            "ipackets", "obytes", "ibytes", "tx-bytes", "rx-bytes", "tx-pkts", "rx-pkts", "oerrors", "ierrors");

    /**
     * RPC Commands
     *
     */
    public static final String PING_METHOD = "ping";

    /**
     *
     */
    public static final String ACQUIRE_METHOD = "acquire";

    /**
     *
     */
    public static final String REMOVE_ALL_STREAMS_METHOD = "remove_all_streams";

    /**
     *
     */
    public static final String STOP_TRAFFIC_METHOD = "stop_traffic";

    /**
     *
     */
    public static final String REMOVE_RX_FILTER_METHOD = "remove_rx_filters";

    /**
     *
     */
    public static final String PAUSE_TRAFFIC_METHOD = "pause_traffic";

    /**
     *
     */
    public static final String RESUME_TRAFFIC_METHOD = "resume_traffic";

    /**
     *
     */
    public static final String RELEASE_HANDLER_METHOD = "release";

    /**
     *
     */
    public static final String VALIDATE_METHOD = "validate";

    /**
     *
     */
    public static final String START_TRAFFIC_METHOD = "start_traffic";

    /**
     *
     */
    public static final String UPDATE_TRAFFIC_METHOD = "update_traffic";

    /**
     *
     */
    public static final String PORT_STATUS_METHOD = "get_port_status";

    /**
     *
     */
    public static final String PORT_XSTATS_NAMES_METHOD = "get_port_xstats_names";

    /**
     *
     */
    public static final String PORT_XSTATS_VALUES_METHOD = "get_port_xstats_values";

    /**
     *
     */
    public static final String GET_UTILIZATION_METHOD = "get_utilization";

    /**
     *
     */
    public static final String ADD_STREAM_METHOD = "add_stream";

    /**
     *
     */
    public static final String PORT_GET_STREAM_LIST_METHOD = "get_stream_list";

    /**
     *
     */
    public static final String PORT_GET_STREAM_METHOD = "get_stream";

    /**
     *
     */
    public static final String PORT_GET_STREAM_STATS_METHOD = "get_stream_stats";

    /**
     *
     */
    public static final String SET_PORT_ATTR_METHOD = "set_port_attr";

    /**
     *
     */
    public static final String SET_L2_METHOD = "set_l2";

    /**
     *
     */
    public static final String SET_L3_METHOD = "set_l3";

    /**
     *
     */
    public static final String SELECT_PROFILE = "Select profile";

    /**
     *
     */
    public static final String TREX_GLOBAL_TAG = "\"trex-global\"";

    /**
     *
     */
    public final static String TX_GEN_TAG = "\"tx-gen\"";

    /**
     *
     */
    public static final String TEMPLATE_INFO_TAG = "\"template_info\"";

    /**
     *
     */
    public static final String RX_CHECK_TAG = "\"rx-check\"";

    /**
     *
     */
    public static final String TREX_LATENCY_TAG = "\"trex-latecny\"";

    /**
     *
     */
    public static final String TREX_LATENCY_V2_TAG = "\"trex-latecny-v2\"";

    /**
     *
     */
    public static final String TREX_EVENT = "\"trex-event\"";

    /**
     *
     */
    public static final String TREX_LATENCY = "\"latency_stats\"";

    /**
     *
     */
    public static final String TREX_FLOW_STATS = "\"flow_stats\"";

    /**
     *
     */
    private Constants() {
        // private constructor
    }
}
