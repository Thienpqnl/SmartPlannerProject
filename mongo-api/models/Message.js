const mongoose = require('mongoose');

const messageSchema = new mongoose.Schema({
  friendId: { type: mongoose.Schema.Types.ObjectId, ref: 'Friend', required: true },
  sender: { type: String, required: true },
  content: { type: String, required: true },
  status: { type: String, enum: ['sent', 'deleted'], default: 'sent' },
  isRead: { type: Boolean, default: false },
  createdAt: { type: Date, default: Date.now },
});
module.exports = mongoose.model('Message', messageSchema);