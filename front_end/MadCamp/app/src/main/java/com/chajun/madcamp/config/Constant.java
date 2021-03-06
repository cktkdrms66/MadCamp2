package com.chajun.madcamp.config;

import com.chajun.madcamp.enums.GameType;

public class Constant {
    public final static String kakaoNativeKey = "b8184b5b4a5d3896f2c1e1196805c056";
//    public static final String BASE_URL = "http://172.10.18.163:443"; //NAT IP
    public static final String BASE_URL = "http://192.249.18.163:443"; //공인 IP

    public final static int BUILD_DECK_MAX_COUNT_DOWN = 10;
    public final static int TURN_MAX_COUNT_DOWN = 10;

    public final static GameType DEFAULT_GAME_TYPE = GameType.N;
    public final static int DEFAULT_NUM_TURN = 4;
    public final static int DEFAULT_NUM_DECK = 7;

}
