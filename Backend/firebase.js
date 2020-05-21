var admin = require("firebase-admin");

// Private key for firebase
// ** DO NOT SHARE **
var serviceAccount = require("./se-environmental-app-firebase-adminsdk-fx69d-601b8c5684");

class Firebase {
  constructor() {
    // Makes the connection to Firebase
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
      databaseURL: "https://se-environmental-app.firebaseio.com",
    });

    // Connects to the cloud firestore
    this.db = admin.firestore();
  }

  async getUIDFromToken(idToken) {
    try {
      // idToken comes from the client app
      let decodedToken = await admin.auth().verifyIdToken(idToken);
      return decodedToken.uid;
    } catch (err) {
      console.log("Error getting UID from token", err);
    }
  }

  // Returns the user json object based on their UUID
  async getUser(uuid) {
    // Queries for the specified user
    let userDoc = await this.db.collection("users").doc(uuid).get();

    if (!userDoc.exists) {
      console.log("No such document!");
      return null;
    }
    return userDoc.data();
  }

  // Check to ensure username doesn't exist, then creates a user
  async createUser(uid, email) {
    await this.db
      .collection("users")
      .doc(uid)
      .set({ username: email, score: 0, friendsList: [] });
  }

  getAllUsers() {
    // Queries and returns all the users in the db
    this.db
      .collection("users")
      .get()
      .then((snapshot) => {
        snapshot.forEach((doc) => {
          console.log(doc.id, "=>", doc.data());
        });
      })
      .catch((err) => {
        console.log("Error getting documents", err);
      });
  }

  // Queries and returns a product document
  async getProduct(id) {
    let productQuery = await this.db
      .collection("products")
      .doc(`00${id}`)
      .get();

    console.log(productQuery);
    if (productQuery.empty) {
      console.log("No matching documents.");
      return null;
    }
    // if a product exists, returns the document
    return productQuery.data();
  }

  // Updates a product in the database
  async updateProduct(id, product) {
    await this.db
      .collection("products")
      .doc(id)
      .set(
        {
          ...product.info,
          dateModified: admin.firestore.Timestamp.fromDate(
            new Date(product.dateModified)
          ),
        },
        { merge: true }
      );
    return id;
  }

  // Batch writes products to firebase (max 500 operations)
  async productBatchWrite(products) {
    let batch = this.db.batch();

    // Takes all the products, formats each product for the batch write
    products.map((product) => {
      let ref = this.db.collection("products").doc(product.id);
      batch.set(ref, {
        ...product.info,
        dateModified: admin.firestore.Timestamp.fromDate(
          new Date(product.dateModified)
        ),
      });
    });

    return await batch.commit();
  }

  async getFriends(idToken) {
    //list of UUIDs
    let id = await this.getUIDFromToken(idToken);
    let user = await this.getUser(id);

    let friendsListDetailed = [];

    for (let i = 0; i < user.friendsList.length; i++) {
      let friendData = await this.getUser(user.friendsList[i]);
      console.log(friendData);
      friendsListDetailed.push(friendData);
    }

    console.log(friendsListDetailed);

    // if a product exists, returns the document
    return friendsListDetailed;
  }
}

// Exports firebase functions to be used in a different file
exports.Firebase = Firebase;
