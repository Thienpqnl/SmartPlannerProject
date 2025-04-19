const mongoose = require('mongoose');

const eventSchema = new mongoose.Schema({
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  name: { type: String, required: true },
  phone: { type: String, default: '' },
  profilePicture: { type: String, default: '' },
  role: { type: String, enum: ['attendee', 'organizer'], default: 'attendee' }
});

module.exports = mongoose.model('User', eventSchema);