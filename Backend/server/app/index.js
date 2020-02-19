// https://jonathanmh.com/express-passport-json-web-token-jwt-authentication-beginners/
const _ = require("lodash");
const express = require("express");
const bodyParser = require("body-parser");
const jwt = require("jsonwebtoken");
const port = 3000;

const firebase = require("../../firebase");
// Makes the db connection to firebase
firebase.initFirebase();

/* these will be integrated later, when we need socket.io comms
const http = require('http').createServer(app);
const io = require('socket.io')(http);
*/

const passport = require("passport");
const passportJWT = require("passport-jwt");

var ExtractJwt = passportJWT.ExtractJwt;
var JwtStrategy = passportJWT.Strategy;

// this is just a dummy database for testing
var users = [
  {
    id: 1,
    name: "john",
    password: "%2xyz"
  },
  {
    id: 2,
    name: "test",
    password: "test"
  }
];

var jwtOptions = {};
jwtOptions.jwtFromRequest = ExtractJwt.fromAuthHeaderAsBearerToken();
jwtOptions.secretOrKey = "308Squad";

var strategy = new JwtStrategy(jwtOptions, function(jwt_payload, next) {
  console.log("payload received", jwt_payload);
  // this will be a database call
  var user =
    users[
      _.findIndex(users, {
        id: jwt_payload.id
      })
    ];

  if (user) {
    next(null, user);
  } else {
    next(null, false);
  }
});

passport.use(strategy);

const app = express();
app.use(passport.initialize());

// this is to test with Postman
app.use(
  bodyParser.urlencoded({
    extended: true
  })
);

// this is what we will actually use
// this will receive a "raw json" string from Android
// it allows for around two 32-character (UTF8) fields, with their
// corresponding json characters.
// The limit is meant prevent an injection
app.use(
  bodyParser.json({
    limit: "320b"
  })
);

app.get("/", function(req, res) {
  res.json({
    message: "Express is up!"
  });
});

app.post("/login", function(req, res) {
  if (req.body.name && req.body.password) {
    var name = req.body.name;
    var password = req.body.password;
  }
  // this will be replaced with a database call
  var user =
    users[
      _.findIndex(users, {
        name: name
      })
    ];

  if (!user) {
    res.status(401).json({
      message: "no such user found"
    });
  }

  if (user.password === req.body.password) {
    var payload = {
      id: user.id
    };
    var token = jwt.sign(payload, jwtOptions.secretOrKey);
    res.json({
      message: "ok",
      token: token
    });
  } else {
    res.status(401).json({
      message: "passwords do not match"
    });
  }
});

/* These are for later, when we integrate real-time game comms
io.on('connection', function(socket){
   console.log('Express is up!');
});
*/

app.listen(port, function() {
  console.log("Express running");
});
