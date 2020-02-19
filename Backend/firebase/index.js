var admin = require("firebase-admin");

// Private key for firebase
// ** DO NOT SHARE **
var serviceAccount = require("./se-environmental-app-firebase-adminsdk-fx69d-601b8c5684");

class Firebase {
  constructor() {
    // Makes the connection to Firebase
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
      databaseURL: "https://se-environmental-app.firebaseio.com"
    });

    // Connects to the cloud firestore
    this.db = admin.firestore();
  }

  getUser(username) {
    username = username.toLowerCase();

    // Queries for the specified user
    return this.db
      .collection("users")
      .where("username", "==", username)
      .get()
      .then(snapshot => {
        if (snapshot.empty) {
          console.log("No matching documents.");
          return null;
        }
        // console.log(snapshot.docs[0].data());
        // if username matches, returns the document for that user
        return snapshot.docs[0].data();
      })
      .catch(err => {
        console.log("Error getting documents", err);
      });
    // return user;
  }

  getAllUsers() {
    // Queries and returns all the users in the db
    this.db
      .collection("users")
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
}

// Exports firebase functions to be used in a different file
exports.Firebase = Firebase;
