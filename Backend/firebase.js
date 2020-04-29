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
        lastName: user.lastName,
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
    let productQuery = await this.db.collection("products").doc(`${id}`).get();

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
}

// Exports firebase functions to be used in a different file
exports.Firebase = Firebase;
