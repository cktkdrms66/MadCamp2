const express = require(`express`);
const mysql = require(`mysql2`);
const asyncHandler = require('express-async-handler');

const app = express();
const server = require(`http`).createServer(app);
const io = require(`socket.io`)(server);

const router = express.Router();
const port = 443;

// Sleep function for delay
const sleep = (milliseconds) => new Promise((resolve) => setTimeout(resolve, milliseconds));

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
            password: req.body.password
        };
        const [result2] = await con.promise().query(sql2, param2);
        
        console.log(`/room/add: Room "${req.body.title}" with id ${result2.insertId} created.`);

        res.send({"id": result2.insertId});
    } catch(err) {
        next(err);
    }
}));

// TODO: Logging this to console is too verbose
router.get(`/room/list`, asyncHandler(async (req, res, next) => {
    try {
        const sql = `SELECT room.id AS id, room_number, title, num_turns, num_moves, game_type, state, locked, user.name AS host_name FROM room JOIN user ON room.host_id = user.id`;
        const [result] = await con.promise().query(sql);
        
        console.log(`/room/list: Sending the list of rooms to client.`);

        res.send(result);
    } catch(err) {
        next(err);
    }
}));

router.post(`/room/remove`, asyncHandler(async (req, res, next) => {
    try {
        const sql = `DELETE FROM room WHERE ?`;
        const param = {id: req.body.id}
        const [result] = await con.promise().query(sql, param);
        
        console.log(result);

        res.send(result); // TODO: What should be sent? Change console.log as well
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
            console.log(`/room/join: Incorrect password; Aborting.`);

            res.send(`{success: 0}`);

            throw new Error("Incorrect password");
        }

        const sql = `UPDATE room SET ? WHERE id = ${req.body.room_id}`;
        // TODO: Should backend check for invalid query? (e.g. locked room, state is already playing, ...)
        const param = {guest_id: req.body.guest_id, state: "P"};
        const [result] = await con.promise().query(sql, param);

        console.log(`/room/join: User with id ${req.body.guest_id} has joined room with id ${req.body.room_id} as guest.`);

        res.send(`{success: 1}`);
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
            email: req.body.email, 
            password: req.body.password
        };
        const [result] = await con.promise().query(sql, param);

        console.log(`/user/register: User "${req.body.name}" created with id ${result.insertId}.`);

        res.send(`{id: ${result.insertId}, success: 1}`);
    } catch(err) {
        next(err);
    }
}));

router.get(`/user/leaderboard`, asyncHandler(async (req, res, next) => {
    try {
        const sql = `SELECT id, name, acc_type, wins, losses, rating FROM user ORDER BY rating DESC`;
        const [result] = await con.promise().query(sql);

        console.log(`/user/leaderboard: Sending the ranking of users to client.`);

        res.send(result);
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

        console.log(`/user/info: information for user ${result.name} with id ${req.query.id} shown.`);

        res.send(result[0]);
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
        const param3 = {wins: result2[0].wins + req.body.host_won, losses: result2[0].losses + req.body.host_lost, rating: result2[0].rating + req.body.host_rating_change};
        const [result3] = await con.promise().query(sql3, param3);

        console.log(`/game/complete: Modifying host info to ${result2[0].wins + req.body.host_won} wins, ${result2[0].losses + req.body.host_lost} 
        losses, and rating of ${result2[0].rating + req.body.host_rating_change}.`);

        // Get current guest wins, losses, rating
        const sql4 = `SELECT wins, losses, rating FROM user WHERE ?`;
        const param4 = {id: req.body.guest_id};
        const [result4] = await con.promise().query(sql4, param4);

        console.log(`/game/complete: Getting guest info.`);

        // Modify guest wins, losses, rating
        const sql5 = `UPDATE user SET ? WHERE id = ${req.body.guest_id}`;
        const param5 = {wins: result2[0].wins + req.body.host_lost, losses: result2[0].losses + req.body.host_won, rating: result2[0].rating - req.body.host_rating_change};
        const [result5] = await con.promise().query(sql5, param5);

        console.log(`/game/complete: Modifying guest info to ${result4[0].wins + req.body.host_lost} wins, ${result2[0].losses + req.body.host_won} 
        losses, and rating of ${result2[0].rating - req.body.host_rating_change}.`);

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

        console.log(`/game/complete: Logging the gamedata to database.`);

        res.send(`{"successful": 1}`);
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

        console.log(`/record/list: Sending the list of gamedata to client.`);

        res.send(result);
    } catch(err) {
        next(err);
    }
}));

server.listen(port, () => {
    console.log(`General: Example app listening on port ${port}.`);
});

app.use(function (err, req, res, next) {
    console.log(`General: An error has occured.`);
    console.log(err);

    res.send(`Error!`);
});

app.use(`/`, router);

const roomMapObj = {};

io.on("connection", (socket) => {
    console.log(`Socket-connection: A connection was established.`);
    
    socket.on(`joinRoom`, (room_id, user_id) => {
        socket.join(room_id);
        if (roomMapObj[room_id] == undefined) {
            roomMapObj[room_id] = {host_id: user_id};
            console.log(`Socket-joinRoom: Host has created a room.`);
        }

        if (io.sockets.adapter.rooms.get(room_id).size == 2) {
            roomMapObj[room_id].guest_id = user_id;

            console.log(`Socket-joinRoom: Guest joined a room; Host and guest are matched, starting buildDeck.`);
            console.log(`Socket-joinRoom: Host id: ${roomMapObj[room_id].host_id}, guest id: ${roomMapObj[room_id].guest_id}.`);
            io.to(room_id).emit(`buildDeck`, roomMapObj[room_id].host_id, roomMapObj[room_id].guest_id);

            (async() => {
                // Sleep for 63 second
                // await sleep(63000);
                await sleep(6000);
                console.log(`Socket-joinRoom: Awoke from sleep, starting startTurn.`);
                io.to(room_id).emit(`startTurn`);
            })();
        }
    });
});
