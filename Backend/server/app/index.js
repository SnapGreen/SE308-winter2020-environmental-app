const express = require("express");
const app = express();
const http = require("http").createServer(app);
const io = require("socket.io")(http);
const port = 3000;
const firebase = require("../../firebase");

app.get("/", (req, res) => res.sendFile(__dirname + "/html/index.html"));

io.on("connection", function(socket) {
  console.log("a user connected");
});

// Makes the db connection to firebase
firebase.initFirebase();

http.listen(port, () => console.log(`Example app listening on port ${port}!`));
