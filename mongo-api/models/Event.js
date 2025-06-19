const mongoose = require('mongoose');

const eventSchema = new mongoose.Schema({
    name: String,
    date: String,
    time: String,
    location: String,
    type: String,
    description: String,
    imageUrl: String,
    seats: Number,
    longitude: Number,
    latitude: Number,
    creatorUid: String,
    isPresent: {
        type: Boolean,
        default: true,
    },
    createdAt: {
        type: Number,
        default: Date.now,
    },
    restrictedUserIds: {
        type: [String], // Mảng chứa id user bị hạn chế
        default: [],
    },
});

module.exports = mongoose.model('Event', eventSchema);