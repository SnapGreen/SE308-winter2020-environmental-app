var admin = require("firebase-admin");

// Private key for firebase
// ** DO NOT SHARE **
var serviceAccount = require("./se-environmental-app-firebase-adminsdk-fx69d-601b8c5684");

function initFirebase() {
  // Makes the connection to Firebase
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://se-environmental-app.firebaseio.com"
  });

  // Connects to the cloud firestore
  let db = admin.firestore();

  // Queries and returns all the users in the db
  db.collection("users")
    .get()
    .then(snapshot => {
      snapshot.forEach(doc => {
        console.log(doc.id, "=>", doc.data());
      });
    })
    .catch(err => {
      console.log("Error getting documents", err);
    });
}

// Exports firebase functions to be used in a different file
exports.initFirebase = initFirebase;
