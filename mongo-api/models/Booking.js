const mongoose = require('mongoose');

const bookingSchema = new mongoose.Schema({
    id: { type: String, required: true, unique: true,  },
    creatorId:{ type: String, required: true },
    idEvent:  { type: String, required: true },
    userId:  { type: String, required: true },
    urlQR: String,
    date: String,
    time:String,
    status: { type: String, enum: ['doing', 'delete'], default: 'doing' }
});

module.exports = mongoose.model('Booking', bookingSchema);