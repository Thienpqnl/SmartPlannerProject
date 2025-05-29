const mongoose = require('mongoose');

const attendeeSchema = new mongoose.Schema({
    userId: {
        type: String,
        required: true,
    },
    eventsRegistered: [String],
    qrCodes: {
        qr: String,
        checkedIn: { type: Boolean, default: false }
    }
});

module.exports = mongoose.model('Attendee', attendeeSchema);


