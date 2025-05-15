const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    userId: { type: String, required: true, unique: true },
    email: String,
    name: String,
    location: String,
    role: { type: String, enum: ['attendee', 'organizer'] },
    longitude: Number,
    latitude: Number,
}, { timestamps: true });

module.exports = mongoose.model('User', userSchema);