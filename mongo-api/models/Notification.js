const mongoose = require('mongoose');

const notificationSchema = new mongoose.Schema({
    userId: { type: String, required: true, unique: true },
    title: String,
    body: String,
    type: String,
    timestamp: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Notification', notificationSchema);