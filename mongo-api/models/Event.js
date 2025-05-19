const mongoose = require('mongoose');

const eventSchema = new mongoose.Schema({
    id: String,
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
    createdAt: {
        type: Number,
        default: Date.now,
    },
});

module.exports = mongoose.model('Event', eventSchema);