// https://jonathanmh.com/express-passport-json-web-token-jwt-authentication-beginners/
const _ = require("lodash");
const express = require("express");
const bodyParser = require("body-parser");
const jwt = require("jsonwebtoken");
const port = 8080;

const firebase = require("../../firebase");
let FIREBASE;

/* these will be integrated later, when we need socket.io comms
const http = require('http').createServer(app);
const io = require('socket.io')(http);
*/

const passport = require("passport");
const passportJWT = require("passport-jwt");

var ExtractJwt = passportJWT.ExtractJwt;
var JwtStrategy = passportJWT.Strategy;

// var jwtOptions = {};
// jwtOptions.jwtFromRequest = ExtractJwt.fromAuthHeaderAsBearerToken();
// jwtOptions.secretOrKey = "308Squad";

// var strategy = new JwtStrategy(jwtOptions, function(jwt_payload, next) {
//   console.log("payload received", jwt_payload);
//   // this will be a database call
//   var user =
//     users[
//       _.findIndex(users, {
//         id: jwt_payload.id
//       })
//     ];

//   if (user) {
//     next(null, user);
//   } else {
//     next(null, false);
//   }
// });

// passport.use(strategy);

const app = express();
// app.use(passport.initialize());

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

// Used to create a new user
app.post("/users", async function(req, res) {
  if (!req.body) {
    res.status(401).json({
      message: "No req.body present"
    });
  }

  let user = await FIREBASE.getUser(req.body.username);
  if (user) {
    res.json({
      message: "User already exists. Please try a different username."
    });
  } else {
    let newId = await FIREBASE.createUser(req.body);
    res.json({
      message: `New user added. Id is ${newId}`
    });
  }
});

// Used to verify login info is correct
app.post("/login", async function(req, res) {
  if (!req.body) {
    res.status(401).json({
      message: "No req.body present"
    });
  } else if (req.body.name && req.body.password) {
    try {
      // checks the database and then determines if the passwords matchs
      console.log("user login attempt");
      let user = await FIREBASE.getUser(req.body.name);

      if (!user) {
        res.status(401).json({
          message: "username not found"
        });
        console.log("username not found");
      }

      if (user.password === req.body.password) {
        res.json({
          message: "ok"
        });
        console.log("ok");
      } else {
        res.status(401).json({
          message: "passwords do not match"
        });
        console.log("passwords do not match");
      }
    } catch (err) {
      res.status(401).json({
        message: "Login Error"
      });
      console.log("Login Error");
    }
  }
});

app.get("/barcode", async function(req, res) {
  console.log("barcode scan request received");
  if (!req.body) {
    res.status(401).json({
      description: "No req.body present"
    });
    console.log("No req.body present");
  }
  else if(req.body.barcode == "123456789012"){
    res.status(302).json({
       description: "barcode found"
    });
    console.log("barcode request %s found", req.body.barcode);
  }
  else{
    res.status(404).json({
       description: "barcode not found"
    });
    console.log("barcode scan request %s not found", req.body.barcode);
  }

   /* Firebase part to be added later
  let user = await FIREBASE.getUser(req.body.username);
  if (user) {
    res.json({
      message: "User already exists. Please try a different username."
    });
  } else {
    let newId = await FIREBASE.createUser(req.body);
    res.json({
      message: `New user added. Id is ${newId}`
    });
  }
  */
});

/* These are for later, when we integrate real-time game comms
io.on('connection', function(socket){
   console.log('Express is up!');
});
*/

app.listen(port, function() {
  console.log("Express running");

  // Initializes the firebase object, which makes the connection to firebase
  FIREBASE = new firebase.Firebase();
});
