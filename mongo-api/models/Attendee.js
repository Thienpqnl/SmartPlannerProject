const mongoose = require('mongoose');

const attendeeSchema = new mongoose.Schema({
    userId: { type: String, required: true },
    eventsRegistered: [String],
    qrCodes: [{
        qr: { type: String, required: true },
        checkedIn: { type: Boolean, default: false },
        eventId: { type: String, required: true }
    }]
});

module.exports = mongoose.model('Attendee', attendeeSchema);