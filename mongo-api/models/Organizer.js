const mongoose = require('mongoose');
const organizerSchema = new mongoose.Schema({
    userId: { 
        type: String, 
        required: true,
        unique: true,
    },
    eventsCreated: [{ 
        type: String 
    }], 
    listIdBooking: [{ 
        type: String 
    }],
}, { timestamps: true });

module.exports = mongoose.model('Organizer', organizerSchema);
