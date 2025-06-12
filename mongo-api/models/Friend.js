const mongoose = require('mongoose');

const friendSchema = new mongoose.Schema({
  userA: { type: String, required: true },
  userB: { type: String, required: true },
  status: {
    userA: { type: String, enum: ['pending', 'accepted', 'blocked', 'deleted','sent'], default: 'sent' },
    userB: { type: String, enum: ['pending', 'accepted', 'blocked', 'deleted', 'sent'], default: 'sent' }
  },
  createdAt: { type: Date, default: Date.now }
});

friendSchema.index(
  { userA: 1, userB: 1 },
  { unique: true }
);

module.exports = mongoose.model('Friend', friendSchema);