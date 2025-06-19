const mongoose = require('mongoose');

const attendeeSchema = new mongoose.Schema({
    userId: { type: String, required: true },
    qrCodes: [{
        eventId: { type: String, required: true },
        qr: { type: String, required: true },
        checkedIn: { type: Boolean, default: false },
        checkInTime: { type: Date, default: null },
        status: { type: String, enum: ['doing', 'delete'], default: 'doing' }
    }]
});

module.exports = mongoose.model('Attendee', attendeeSchema);