const mongoose = require('mongoose');

const attendeeSchema = new mongoose.Schema({
    attendeeId: String,
    userId: String,
    eventsRegistered: [String],
    qrCodes: {
        qr: String,
        checkedIn: { type: Boolean, default: false }
    }
});

module.exports = mongoose.model('Attendee', attendeeSchema);
