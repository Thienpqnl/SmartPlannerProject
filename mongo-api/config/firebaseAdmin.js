const admin = require('firebase-admin');

// Đảm bảo bạn đã đặt file serviceAccountKey.json ở đúng đường dẫn
const serviceAccount = require('./serviceAccountKey.json'); // hoặc '../path/to/your/serviceAccountKey.json'

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

module.exports = admin;