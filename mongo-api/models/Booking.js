const mongoose = require('mongoose');

const bookingSchema = new mongoose.Schema({
    id: { type: String, required: true, unique: true,  },
    idEvent:  { type: String, required: true },
    userId:  { type: String, required: true },
    urlQR: String,
    createdAt: {
         type: Number,
        default: Date.now,
    },
});

module.exports = mongoose.model('Booking', bookingSchema);