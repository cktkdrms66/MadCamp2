package com.chajun.madcamp.logic;

import com.chajun.madcamp.config.Constant;
import com.chajun.madcamp.enums.GameResult;
import com.chajun.madcamp.enums.GameType;
import com.chajun.madcamp.enums.Move;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameInfo {

    public int roomId;
    public boolean isHost;
    public int[] myMoveCounts;
    public int[] enemyMoveCounts;
    public GameType gameType = GameType.N;
    public int currentTurn = 0;
    public int totalTurn = 4;
    public int totalDeck = 7;

    public Socket socket;

    public List<Move> myMoves;
    public List<Move> enemyMoves;
    public List<GameResult> gameResults;


    private static GameInfo instance;

    public static GameInfo getInstance() {
        if (instance == null) {
            return instance = new GameInfo();
        } else {
            return instance;
        }
    }

    private GameInfo() {

    }

    public void connectSocket(Emitter.Listener listener) throws URISyntaxException {
        socket = IO.socket(Constant.BASE_URL);
        socket.on(io.socket.client.Socket.EVENT_CONNECT, listener);
        socket.connect();

    }

    public void disconnectSocket() {
        if (socket != null) {
            socket.disconnect();
        }
    }
    public void init1(boolean isHost, int roomId) {
        this.isHost = isHost;
        this.roomId = roomId;
    }
    public void init2(int[] moveCounts) {
        myMoves = new ArrayList<>();
        enemyMoves = new ArrayList<>();
        gameResults = new ArrayList<>();
        instance.myMoveCounts = moveCounts;
        instance.enemyMoveCounts = new int[moveCounts.length];
        instance.currentTurn = 0;
    }

}
