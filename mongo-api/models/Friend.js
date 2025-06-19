const mongoose = require('mongoose');

const friendSchema = new mongoose.Schema({
  userA: { type: String, required: true }, // userId người gửi
  userB: { type: String, required: true }, // userId người nhận
  statusA: { type: String, enum: ['pending', 'accepted', 'blocked', 'deleted', 'sent'], default: 'sent' },
  statusB: { type: String, enum: ['pending', 'accepted', 'blocked', 'deleted', 'sent'], default: 'pending' },
  createdAt: { type: Date, default: Date.now }
});

friendSchema.index(
  { userA: 1, userB: 1 },
  { unique: true }
);

module.exports = mongoose.model('Friend', friendSchema);