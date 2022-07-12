const express = require(`express`);
const mysql = require(`mysql2`);
const asyncHandler = require('express-async-handler');
const emit = require('process');

const app = express();
const server = require(`http`).createServer(app);
const io = require(`socket.io`)(server);

const router = express.Router();
const port = 443;

// TODO: Clean up console logs

// Configuring express to use body-parser as middle-ware
app.use(express.urlencoded({ extended: false }));
app.use(express.json());

const con = mysql.createConnection({
    host: `localhost`,
    user: `root`,
    password: ``,
    database: `project2`
});

con.connect(function (err) {
    try {
        console.log(`General: Connection to MySQL established.`);
    } catch(err) {
        next(err);
    }
});

// ROOM
// TODO: More efficient code?
router.post(`/room/add`, asyncHandler(async (req, res, next)  => {
    try {
        // Compute maximum room number to generate next room number
        const sql1 = `SELECT COALESCE(MAX(room_number), 0) AS max FROM room`;
        const [result1] = await con.promise().query(sql1);

        console.log(`/room/add: Current maximum room number is ${result1[0].max}.`);
        
        const sql2 = `INSERT INTO room SET ?`;
        const param2 = {
            room_number: result1[0].max + 1,
            title: req.body.title,
            num_turns: req.body.num_turns,
            num_moves: req.body.num_moves,
            game_type: req.body.game_type,
            host_id: req.body.host_id,
            locked: req.body.locked,
            hidden: 0,
            password: req.body.password
        };
        const [result2] = await con.promise().query(sql2, param2);
        
        res.send(`{id: ${result2.insertId}}`);
        
        console.log(`/room/add: Room "${req.body.title}" with id ${result2.insertId} created.`);
    } catch(err) {
        next(err);
    }
}));

router.get(`/room/list`, asyncHandler(async (req, res, next) => {
    try {
        const sql = `SELECT room.id AS id, room_number, title, num_turns, num_moves, game_type, state, locked, user.name AS host_name FROM room JOIN user ON room.host_id = user.id WHERE hidden = 0 ORDER BY room_number`;
        const [result] = await con.promise().query(sql);
        
        res.send(result);
        
        console.log(`/room/list: Sending the list of rooms to client.`);
    } catch(err) {
        next(err);
    }
}));

router.post(`/room/remove`, asyncHandler(async (req, res, next) => {
    try {
        const sql = `DELETE FROM room WHERE ?`;
        const param = {id: req.body.id}
        const [result] = await con.promise().query(sql, param);
        
        res.send(result); // TODO: What should be sent? Change console.log as well
        
        console.log(result);
    } catch(err) {
        next(err);
    }
}));

router.post(`/room/join`, asyncHandler(async (req, res, next) => {
    try {
        const sql1 = `SELECT locked, password FROM room WHERE ?`;
        const param1 = {id: req.body.room_id};
        const [result1] = await con.promise().query(sql1, param1);

        if (result1[0].locked == 1 && result1[0].password != req.body.password) {
            res.send(`{success: 0}`);
            
            console.log(`/room/join: Incorrect password; Aborting.`);

            throw new Error("Incorrect password");
        }

        const sql = `UPDATE room SET guest_id = ?, state = ? WHERE id = ?`;
        // TODO: Should backend check for invalid query? (e.g. locked room, state is already playing, ...)
        const param = [req.body.guest_id, `P`, req.body.room_id];
        const [result] = await con.promise().query(sql, param);

        res.send(`{success: 1}`);
        
        console.log(`/room/join: User with id ${req.body.guest_id} has joined room with id ${req.body.room_id} as guest.`);
    } catch(err) {
        next(err);
    }
}));

router.post(`/room/random`, asyncHandler(async (req, res, next) => {
    try {
        const sql1 = `SELECT id FROM room WHERE hidden = 1 AND state = "W"`;
        const [result1] = await con.promise().query(sql1);

        if (result1.length > 0) {
            console.log(`/room/random: A hidden and waiting room exists.`);

            const sql2 = `UPDATE room SET guest_id = ?, state = ? WHERE id = ?`;
            const param2 = [req.body.id, `P`, result1[0].id];
            const [result2] = await con.promise().query(sql2, param2);

            res.send(`{id: ${result1[0].id}, is_host: 0}`);

            console.log(`/room/random: User with id ${req.body.id} has joined hidden room with id ${result1[0].id} as guest.`);
        } else {
            // Compute maximum room number to generate next room number
            const sql3 = `SELECT COALESCE(MAX(room_number), 0) AS max FROM room`;
            const [result3] = await con.promise().query(sql3);

            console.log(`/room/random: Current maximum room number is ${result3[0].max}.`);
            
            const sql4 = `INSERT INTO room SET ?`;
            const param4 = {
                room_number: result3[0].max + 1,
                title: `Hidden room`,
                // TODO: Set these values; Should it be normal or extended?
                num_turns: 4,
                num_moves: 7,
                game_type: `N`,
                host_id: req.body.id,
                locked: 0,
                hidden: 1
            };
            const [result4] = await con.promise().query(sql4, param4);
            
            res.send(`{id: ${result4.insertId}, is_host: 1}`);
            
            console.log(`/room/random: Hidden room with id ${result4.insertId} created.`);
        }
    } catch(err) {
        next(err);
    }
}));

// USER
router.post(`/user/register`, asyncHandler(async (req, res, next) => {
    try {
        const sql = `INSERT INTO user SET ?`;
        const param = {
            name: req.body.name, 
            acc_type: req.body.acc_type, 
            token: req.body.token, 
            username: req.body.username, 
            password: req.body.password
        };
        const [result] = await con.promise().query(sql, param);

        res.send(`{id: ${result.insertId}, success: 1}`);
        
        console.log(`/user/register: User "${req.body.name}" created with id ${result.insertId}.`);
    } catch(err) {
        next(err);
    }
}));

router.get(`/user/leaderboard`, asyncHandler(async (req, res, next) => {
    try {
        const sql = `SELECT id, name, acc_type, wins, losses, rating FROM user ORDER BY rating DESC`;
        const [result] = await con.promise().query(sql);

        res.send(result);
        
        console.log(`/user/leaderboard: Sending the ranking of users to client.`);
    } catch(err) {
        next(err);
    }
}));

// TODO: More efficient code?
router.get(`/user/info`, asyncHandler(async (req, res, next) => {
    try {
        const sql = `SELECT name, wins, losses, rating, (SELECT COUNT(*) + 1 FROM user WHERE rating > u.rating) as rank FROM user AS u WHERE ?`;
        const param = {id: req.query.id};
        const [result] = await con.promise().query(sql, param);

        res.send(result[0]);
        
        console.log(`/user/info: Information for user ${result[0].name} with id ${req.query.id} shown.`);
    } catch(err) {
        next(err);
    }
}));

// TODO: Replace result.length with COUNT(*)?
router.post(`/user/login/native`, asyncHandler(async (req, res, next) => {
    try {
        const sql = `SELECT id, name FROM user WHERE acc_type = ? AND email = ? AND password = ?`;
        const param = [`N`, req.body.email, req.body.password];
        const [result] = await con.promise().query(sql, param);

        if (result.length > 0) {
            res.send(`{success: 1, id: ${result[0].id}}`);
            
            console.log(`/user/login/native: User ${result[0].name} (id ${result[0].id}, account type native) found; Logging the user in.`);
        } else {
            res.send(`{success: 0}`);
            
            console.log(`/user/login/native: Matching user not found.`);
        }
    } catch(err) {
        next(err);
    }
}));

router.post(`/user/login/kakao`, asyncHandler(async (req, res, next) => {
    try {
        const sql1 = `SELECT name FROM user WHERE email = ?`;
        const param1 = [req.body.email];
        const [result1] = await con.promise().query(sql1, param1);

        if (result1.length == 0) {
            const sql2 = `INSERT INTO user SET ?`;
            console.log(req.body.name);
            const param2 = {name: req.body.name, acc_type: `K`, email: req.body.email, password: req.body.password};
            const [result2] = await con.promise().query(sql2, param2);

            res.send(`{success: 1, id: ${result2.insertId}}`);
            
            console.log(`/user/login/kakao: Registering user ${req.body.name} (id ${result2.insertId}, account type Kakao, email ${req.body.email}).`);
        } else {
            const sql3 = `SELECT id, name FROM user WHERE acc_type = ? AND email = ? AND password = ?`;
            const param3 = [`K`, req.body.email, req.body.password];
            const [result3] = await con.promise().query(sql3, param3);

            if (result3.length > 0) {
                res.send(`{success: 1, id: ${result3[0].id}}`);
                
                console.log(`/user/login/kakao: User ${result3[0].name} (id: ${result3[0].id}, account type Kakao) found; Logging the user in.`);
            } else {
                res.send(`{success: 0}`);
                
                console.log(`/user/login/kakao: Matching user not found.`);
            }
        }
    } catch(err) {
        next(err);
    }
}));

router.post(`/user/register/native`, asyncHandler(async (req, res, next) => {
    try {
        const sql1 = `SELECT name FROM user WHERE ?`;
        const param1 = {email: req.body.email};
        const [result1] = await con.promise().query(sql1, param1);

        if (result1.length == 0) {
            const sql2 = `INSERT INTO user SET ?`;
            const param2 = {name: req.body.name, acc_type: `N`, email: req.body.email, password: req.body.password};
            const [result2] = await con.promise().query(sql2, param2);

            res.send(`{success: 1, id: ${result2.insertId}}`);
            
            console.log(`/user/register/native: Registering user ${req.body.name} (id ${result2.insertId}, account type Native, email ${req.body.email}).`);
        }
    } catch(err) {
        next(err);
    }
}));

// GAME
// TODO: More efficient code?
// https://stackoverflow.com/questions/2762851/increment-a-database-field-by-1
router.post(`/game/complete`, asyncHandler(async (req, res, next) => {
    try {
        // Remove room
        const sql1 = `DELETE FROM room WHERE ?`;
        const param1 = {id: req.body.room_id};
        const [result1] = await con.promise().query(sql1, param1);

        console.log(`/game/complete: Deleted room.`);

        // Get current host wins, losses, rating
        const sql2 = `SELECT wins, losses, rating FROM user WHERE ?`;
        const param2 = {id: req.body.host_id};
        const [result2] = await con.promise().query(sql2, param2);

        console.log(`/game/complete: Getting host info.`);

        // Modify host wins, losses, rating
        const sql3 = `UPDATE user SET ? WHERE id = ${req.body.host_id}`;
        const param3 = {wins: result2[0].wins + (req.body.game_result == 1 ? 1 : 0), losses: result2[0].losses + (req.body.game_result == -1 ? 1 : 0), rating: result2[0].rating + req.body.host_rating_change};
        const [result3] = await con.promise().query(sql3, param3);

        console.log(`/game/complete: Modifying host info to ${result2[0].wins + (req.body.game_result == 1 ? 1 : 0)} wins, ${result2[0].losses + (req.body.game_result == -1 ? 1 : 0)} losses, and rating of ${result2[0].rating + req.body.host_rating_change}.`);

        // Get current guest wins, losses, rating
        const sql4 = `SELECT wins, losses, rating FROM user WHERE ?`;
        const param4 = {id: req.body.guest_id};
        const [result4] = await con.promise().query(sql4, param4);

        console.log(`/game/complete: Getting guest info.`);

        // Modify guest wins, losses, rating
        const sql5 = `UPDATE user SET ? WHERE id = ${req.body.guest_id}`;
        const param5 = {wins: result2[0].wins + (req.body.game_result == -1 ? 1 : 0), losses: result2[0].losses + (req.body.game_result == 1 ? 1 : 0), rating: result2[0].rating - req.body.host_rating_change};
        const [result5] = await con.promise().query(sql5, param5);

        console.log(`/game/complete: Modifying guest info to ${result4[0].wins + (req.body.game_result == -1 ? 1 : 0)} wins, ${result4[0].losses + (req.body.game_result == 1 ? 1 : 0)} losses, and rating of ${result4[0].rating - req.body.host_rating_change}.`);

        // Add to gamedata
        const sql6 = `INSERT INTO gamedata SET ?`;
        const param6 = {
            num_turns: req.body.num_turns, 
            num_moves: req.body.num_moves, 
            game_type: req.body.game_type, 
            host_id: req.body.host_id, 
            guest_id: req.body.guest_id,
            host_moves: req.body.host_moves,
            guest_moves: req.body.guest_moves,
            host_unused_moves: req.body.host_unused_moves,
            guest_unused_moves: req.body.guest_unused_moves,
        };

        const [result6] = await con.promise().query(sql6, param6);

        res.send(`{"success": 1}`);
        
        console.log(`/game/complete: Logging the gamedata to database.`);
    } catch(err) {
        next(err);
    }
}));

// RECORD
router.get(`/record/list`, asyncHandler(async (req, res, next) => {
    try {
        const sql = `SELECT gamedata.id, num_turns, num_moves, game_Type, host_id, 
            (SELECT u.name FROM user u WHERE u.id = host_id) AS host_name, guest_id, 
            (SELECT u.name FROM user u WHERE u.id = guest_id) AS guest_name, 
            host_moves, guest_moves, host_unused_moves, guest_unused_moves, 
            date_time FROM user JOIN gamedata ON (user.id = gamedata.host_id OR user.id = gamedata.guest_id) 
            WHERE user.id = ? ORDER BY date_time DESC`;
        const [result] = await con.promise().query(sql, [req.query.id]);

        res.send(result);
        
        console.log(`/record/list: Sending the list of gamedata to client.`);
    } catch(err) {
        next(err);
    }
}));

server.listen(port, () => {
    console.log(`General: Example app listening on port ${port}.`);
});

app.use(function (err, req, res, next) {
    res.send(`Error!`);
    
    console.log(`General: An error has occured.`);
    console.log(err);
});

app.use(`/`, router);

// Constants for socket.io
const roomMapObj = {};
const userMapObj = {};

// Sleep function for delay
const sleep = (seconds) => new Promise((resolve) => setTimeout(resolve, seconds * 1000));

// R: 0, S: 1, P: 2, MR: 3, MS: 4, MP: 5
const win = 1, tie = 0, loss = -1, na = 2;
const turnResultMatrixNormal = [
    [ tie,  win, loss,   na,   na,  win, loss, loss,   na,   na],
    [loss,  tie,  win,   na,   na, loss,  win, loss,   na,   na],
    [ win, loss,  tie,   na,   na, loss, loss,  win,   na,   na],
    [  na,   na,   na,   na,   na,   na,   na,   na,   na,   na],
    [  na,   na,   na,   na,   na,   na,   na,   na,   na,   na],
    [loss,  win,  win,   na,   na,  tie,  win, loss,   na,   na],
    [ win, loss,  win,   na,   na, loss,  tie,  win,   na,   na],
    [ win,  win, loss,   na,   na,  win, loss,  tie,   na,   na],
    [  na,   na,   na,   na,   na,   na,   na,   na,   na,   na],
    [  na,   na,   na,   na,   na,   na,   na,   na,   na,   na]
];
const turnResultMatrixExtended = [
    [ tie,  win, loss,  win, loss,  win, loss, loss,  win, loss],
    [loss,  tie,  win,  win, loss, loss,  win,  win, loss, loss],
    [ win, loss,  tie, loss,  win,  win, loss,  win, loss, loss],
    [loss, loss,  win,  tie,  win, loss, loss, loss,  win,  win],
    [ win,  win, loss, loss,  tie, loss,  win, loss, loss,  win],
    [loss,  win, loss,  win,  win,  tie,  win, loss,  win, loss],
    [ win, loss,  win,  win, loss, loss,  tie,  win,  win, loss],
    [ win, loss, loss,  win,  win,  win, loss,  tie, loss,  win],
    [loss,  win,  win, loss,  win, loss, loss,  win,  tie,  win],
    [ win,  win,  win, loss, loss,  win,  win, loss, loss,  tie]
];

// Socket.io
// TODO: Cleanup looking up roomMapObj and userMapObj
io.on("connection", (socket) => {
    console.log(`Socket-connection: Callback reached: Connection (socket id: ${socket.id}) was established.`);
    
    socket.on(`joinRoom`, (room_id, user_id) => {
        // userMapObj[socket.id] = {user_id: user_id, room_id: room_id};
        userMapObj[socket.id] = room_id;

        socket.join(userMapObj[socket.id]);
        if (roomMapObj[userMapObj[socket.id]] == undefined) {
            roomMapObj[userMapObj[socket.id]] = {host_id: user_id};
            console.log(`Socket-joinRoom: Host has created a room (room id: ${userMapObj[socket.id]}).`);
        }

        if (io.sockets.adapter.rooms.get(userMapObj[socket.id]).size == 2) {
            roomMapObj[userMapObj[socket.id]].guest_id = user_id;

            console.log(`Socket-joinRoom: Guest joined a room (room id: ${userMapObj[socket.id]}); Host (user id: ${roomMapObj[userMapObj[socket.id]].host_id}) and guest (user id: ${roomMapObj[userMapObj[socket.id]].guest_id}) are matched.`);
            io.to(userMapObj[socket.id]).emit(`buildDeck`, roomMapObj[userMapObj[socket.id]].host_id, roomMapObj[userMapObj[socket.id]].guest_id);
            console.log(`Socket-joinRoom: buildDeck emitted to room (room id: ${userMapObj[socket.id]}); Entering sleep for 63s.`);

            (async() => {
                // Sleep for 63 seconds (client 60 seconds)
                // TODO: Change this
                // await sleep(63);
                await sleep(11);
                console.log(`Socket-joinRoom: Awoke from sleep.`);

                roomMapObj[userMapObj[socket.id]].ready = 0;
                io.to(userMapObj[socket.id]).emit(`startGame`);
                console.log(`Socket-joinRoom: startGame emitted to room (room id: ${userMapObj[socket.id]}).`);
            })();
        }
    });

    socket.on(`initialize`, (num_turns, game_type, deck, is_host) => {
        console.log(`Socket-initialize: Callback reached.`);
        roomMapObj[userMapObj[socket.id]].ready += 1;
        roomMapObj[userMapObj[socket.id]].num_turns = num_turns;
        roomMapObj[userMapObj[socket.id]].game_type = game_type;
        if (is_host) {
            roomMapObj[userMapObj[socket.id]].deck_host = deck;
        } else {
            roomMapObj[userMapObj[socket.id]].deck_guest = deck;
        }

        if (roomMapObj[userMapObj[socket.id]].ready == 2) {
            roomMapObj[userMapObj[socket.id]].ready = 0;
            roomMapObj[userMapObj[socket.id]].cur_turn = 0;
            
            io.to(userMapObj[socket.id]).emit(`deckInfo`, roomMapObj[userMapObj[socket.id]].deck_host, roomMapObj[userMapObj[socket.id]].deck_guest);
            console.log(`Socket-initialize: deckInfo emitted to room (room id: ${userMapObj[socket.id]}).`);
        }
    });

    socket.on(`nextTurn`, () => {
        console.log(`Socket-nextTurn: Callback reached.`);
        roomMapObj[userMapObj[socket.id]].ready += 1;

        if (roomMapObj[userMapObj[socket.id]].ready == 1 && roomMapObj[userMapObj[socket.id]].cur_turn == roomMapObj[userMapObj[socket.id]].num_turns) {
            delete userMapObj[socket.id];
            return;
        }

        if (roomMapObj[userMapObj[socket.id]].ready == 2) {
            roomMapObj[userMapObj[socket.id]].ready = 0;

            if (roomMapObj[userMapObj[socket.id]].cur_turn < roomMapObj[userMapObj[socket.id]].num_turns) {
                roomMapObj[userMapObj[socket.id]].cur_turn += 1;
                io.to(userMapObj[socket.id]).emit(`startTurn`);
                console.log(`Socket-nextTurn: startTurn emitted to room (room id: ${userMapObj[socket.id]}, turn: ${roomMapObj[userMapObj[socket.id]].cur_turn}); Entering sleep for 11s.`);

                (async() => {
                    // Sleep for 11 seconds (client 10 seconds)
                    await sleep(11);
                    console.log(`Socket-nextTurn: Awoke from sleep.`);
                    io.to(userMapObj[socket.id]).emit(`submitMoves`);
                    console.log(`Socket-nextTurn: submitMoves emitted to room (room id: ${userMapObj[socket.id]}).`);
                })();
            } else {
                (async() => {
                    const sql1 = `SELECT rating FROM user WHERE ?`;
                    const param1 = {id: roomMapObj[userMapObj[socket.id]].host_id};
                    const [result1] = await con.promise().query(sql1, param1);

                    const sql2 = `SELECT rating FROM user WHERE ?`;
                    const param2 = {id: roomMapObj[userMapObj[socket.id]].guest_id};
                    const [result2] = await con.promise().query(sql2, param2);

                    io.to(userMapObj[socket.id]).emit(`gameComplete`, roomMapObj[userMapObj[socket.id]].guest_id, result1[0].rating, result2[0].rating);
                    console.log(`Socket-nextTurn: gameComplete emitted to room (room id: ${userMapObj[socket.id]}).`);

                    // TODO: Remove me
                    console.log(JSON.stringify(roomMapObj));

                    delete roomMapObj[userMapObj[socket.id]];
                    delete userMapObj[socket.id];
                })();
            }
        }
    });

    socket.on(`compareStart`, (move, is_host) => {
        console.log(`Socket-compareStart: Callback reached.`);
        roomMapObj[userMapObj[socket.id]].ready += 1;
        if (is_host) {
            roomMapObj[userMapObj[socket.id]].move_host = move;
        } else {
            roomMapObj[userMapObj[socket.id]].move_guest = move;
        }
        
        if (roomMapObj[userMapObj[socket.id]].ready == 2) {
            roomMapObj[userMapObj[socket.id]].ready = 0;

            matrix = (roomMapObj[userMapObj[socket.id]].game_type == `N` ? turnResultMatrixNormal : turnResultMatrixExtended);

            io.to(userMapObj[socket.id]).emit(`turnResult`, roomMapObj[userMapObj[socket.id]].move_host, roomMapObj[userMapObj[socket.id]].move_guest, 
                matrix[roomMapObj[userMapObj[socket.id]].move_host][roomMapObj[userMapObj[socket.id]].move_guest]);
            console.log(`Socket-compareStart: turnResult emitted to room (room id: ${userMapObj[socket.id]}, host result: ${matrix[roomMapObj[userMapObj[socket.id]].move_host][roomMapObj[userMapObj[socket.id]].move_guest]}).`);
        }
    });
    
    socket.on(`disconnect`, () => {
        console.log(`Socket-disconnect: Callback reached: Socket (socket id: ${socket.id}) is disconnected.`);
    });
});
