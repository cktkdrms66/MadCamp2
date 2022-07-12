# Elements

> 평범한 가위바위보로는 아쉬울 때, Elements에 도전해 보세요!
> 

## Authors

박준현(고려대학교 20): https://github.com/channelsucre

차상근(한양대학교 18): https://github.com/cktkdrms66

## 기능 개요

Elements는 우리에게 익숙한 “가위바위보”에 기초한 게임입니다.

- 일반적인 가위바위보와 달리 상대와 매칭된 이후 자신만의 “덱”을 구성하여 독창적인 전략을 펼칠 수 있습니다. 상대의 플레이를 예측하고, 이에 대응하여 플레이를 선택하면, 승리할 확률이 올라갑니다.
- 친구와 방을 만들어 실력을 겨루어 볼 수 있고, 모르는 사람에게 랜덤 매칭을 통해 도전해 볼 수도 있습니다.
- 불, 물, 풀의 세가지 원소를 이용한 Normal 모드, 불, 풀, 바위, 얼음, 땅의 다섯가지 원소를 이용한 Extended 모드가 존재하며, 턴과 덱의 수도 정할 수 있어 다양한 즐거움을 맛볼 수 있습니다.

## 권장 사용 환경 및 개발 환경

### 프론트엔드

- Framework: Android Studio
- Language: JAVA 8
- IDE: Android Studio
- Target Device: Galaxy S6

### 백엔드

- Framework: Node.js, Express.js, Socket.io, MySQL
- Language: JavaScript
- IDE: Visual Studio Code

## 매칭 시스템

- 랜덤 매치를 눌러 모르는 사람과 대결하세요! 상대방과 실력을 겨루고, 레이팅 점수를 획득해
- 최정상에 도달하도록 노력하세요!

## 대전 시스템

### 일반 모드

- 기존의 가위바위보에 착안하여, 제작하였습니다. 불, 물, 풀 3속성 체제이며,
- 메가진화 버튼을 눌러 게임에 한번, 전세역전을 노릴 수 있습니다.

### 메가 진화

- 기존의 불 속성은 물을 이길 수 없습니다! 하지만 메가 진화한 불 속성은 물을 이깁니다.
- 물론 풀 속성도 마찬가지입니다.
- **하지만 같은 불 속성에게는 집니다!** 이 점을 활용해 더욱 풍부한 전략을 준비하고, 응수할 수 있습니다!

### 확장 모드

- 기존의 시스템은 겨우 3개의 속성이라 어느정도 게임을 즐긴 유저라면 지루할 수 있습니다.
- 그래서 저희 Elements에는 얼음, 불, 풀, 땅, 바위, 총 5속성 체제의 Extended 타입 게임도 존재합니다! 3속성보다 훨씬 복잡하지만 더욱 치열한 전략 싸움이 될 것입니다.

## 전적 및 랭킹

- 대전을 통해 얻은 레이팅 점수에 따라 랭킹이 나옵니다. 많은 유저들을 쓰러뜨리고 최정상에 도달하세요!

## 로그인 시스템

- 기본 로그인과 카카오 로그인이 제공됩니다. 간단한 회원가입을 통해 빠르게 회원가입이 가능합니다.
- 자동 로그인도 제공되어, 한번 로그인하면 이후 로그인 시, 자동 로그인 처리됩니다!

## 프론트엔드

- Retrofit 2.0 을 활용해 서버와 HTTP 통신을 주고 받았습니다.
- Socket.io를 통해 서버와 실시간 소켓 통신을 주고 받았습니다.

### 참고 라이브러리

```python
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
//Retrofit - gson
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

implementation ('io.socket:socket.io-client:2.0.1') {
    // excluding org.json which is provided by Android
    exclude group: 'org.json', module: 'json'
}

implementation "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
implementation 'de.hdodenhof:circleimageview:3.1.0'
implementation "com.kakao.sdk:v2-user:2.11.0" // 카카오 로그인
```

## 백엔드

- 서버-클라이언트 간 통신을 위하여 Express.js와 Socket.io를 사용하였습니다.
- 열려 있는 방 리스트를 확인하고, 방을 추가하고, 랭킹을 확인하는 등 데이터베이스를 액세스하고 조작하는 동작들은 HTTP 통신의 GET과 POST기능을 사용하여 구현하였습니다.
- 실제 게임이 진행되는 동안에는, 동시성과 양방향성에 기초하여 동작하는 게임의 특성 상 Socket.io를 이용한 소켓 통신을 이용하였습니다. 클라이언트가 서버에게 요청을 보내고, 백엔드에서는 연산을 하여 결과를 room에 전달하는 방식으로 구성하였습니다. 아래는 이를 위한 알고리즘 구상 과정 당시의 자료입니다.

<aside>
❗ Note: *client, server*

- Starts: room activity
- Connects to socket
- Emits `"joinRoom"`, `roomId`, `userId` to socket
- Joins the socket to corresponding room
- (When two joins) Emits `“buildDeck”` to room
- Starts: deck selection activity
- **(Both)** Count 60 seconds (server’s timer dominates)
- Then, emits `“startGame”` to room
- Emits `“initialize”`, `numTurns`, `gameType` `deck`, `isHost` to socket
- Emits `“deckInfo”`, `deckHost`, `deckGuest` to room
- Loop the following for `numTurns`
    - Then emits `“nextTurn”` to socket
    - Check `curTurn`/`numTurns` and emits `“startTurn”` to room
    - Starts turn activity
    - **(Both)** Count 10 seconds (server’s timer dominates)
    - Then, emits `“submitMoves”` to room
    - Starts compare activity (and saves the result locally)
    - Emits `“compareStart”`, `move.index`, `isHost` to socket
    - Compare moves to find winner
    - Emits `“turnResult”`, `moveHost`, `moveGuest`, `resultHost` to socket
    - Record `hostResult` in memory
    - Counts 3 seconds
- After `numTurns`, emits `“gameComplete”`, `guest_id` to room
- **(Host only)** Emits `“gameSummary”`, `result` based on the perspective of itself
    - Instead, POST /game/complete? @차상근
- Disconnects the socket
</aside>

## Download
- [Download](https://drive.google.com/file/d/1ON19DNR5jX3u2jnX-KkAerNwoOcxrK6l/view?usp=sharing)

## Thanks to

### Copyright

- Pokemon type icons by [Lugia-sea on DeviantArt](https://www.deviantart.com/lugia-sea/art/Pokemon-Type-Icons-Vector-869706864) and [Nintendo](https://www.nintendo.com).
