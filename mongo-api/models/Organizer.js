const mongoose = require('mongoose');
const organizerSchema = new mongoose.Schema({
    id:{ type: String,unique: true,},

    userId: { 
        type: String, 
        required: true,
       
    },
    idEvent: { 
        type: String, required: true,unique: true,
    }, 
    listIdBooking: [{ 
        type: String 
    }],
}, { timestamps: true });

module.exports = mongoose.model('Organizer', organizerSchema);
