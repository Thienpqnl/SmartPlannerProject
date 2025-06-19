const mongoose = require('mongoose');


const userNotifySchema = new mongoose.Schema({
    userId: { type: String, required: true, unique: true },
    fcmToken: String,
    updatedAt: { type: Date, default: Date.now }
});
module.exports = mongoose.model('UserNotify', userNotifySchema);

