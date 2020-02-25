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

  // Returns the user that matches the provided username
  async getUser(username) {
    username = username.toLowerCase();

    // Queries for the specified user
    let userQuery = await this.db
      .collection("users")
      .where("username", "==", username)
      .get();

    if (userQuery.empty) {
      console.log("No matching documents.");
      return null;
    }
    // if username matches, returns the document for that user
    return userQuery.docs[0].data();
  }

  // Check to ensure username doesn't exist, then creates a user
  async createUser(user) {
    let ref = await this.db.collection("users").add(
      {
        username: user.username,
        password: user.password,
        firstName: user.firstName,
        lastName: user.lastName
      },
      { merge: true }
    );
    return ref.id;
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
