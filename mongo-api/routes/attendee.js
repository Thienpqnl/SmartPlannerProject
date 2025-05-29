const express = require('express');
const router = express.Router();
const Booking = require('../models/Booking');
const Attendee = require('../models/Attendee');
// GET /attendee/getListByEvent?eventId=123
router.get('/getListByEvent/:eventId', async (req, res) => {
  const { eventId } = req.params;
  try {
    const users = await Booking.aggregate([
      {
        $match: { idEvent: eventId }  // Lọc booking theo eventId
      },
      {
        $lookup: {
          from: "users",                   // collection user
          localField: "userId",            // trường trong booking
          foreignField: "userId",             // trường _id trong users
          as: "user"
        }
      },
      { $unwind: "$user" },                // lấy user object ra khỏi mảng
      {
        $replaceRoot: { newRoot: "$user" } // chỉ trả về object user
      },
    ]);

    res.json(users);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Lỗi server" });
  }
});

// Check-in bằng QR code
router.post('/checking', async (req, res) => {
  const { qrCode } = req.body;
  if (!qrCode) return res.status(400).json({ message: 'QR code is required' });

  try {
    const attendee = await Attendee.findOne({ 'qrCodes.qr': qrCode });
    if (!attendee) return res.status(404).json({ message: 'Attendee not found' });

    attendee.qrCodes.checkedIn = true;
    await attendee.save();
    res.json({ message: 'Check-in successful', attendeeId: attendee.attendeeId });
  } catch (err) {
    console.error('Check-in error:', err);
    res.status(500).json({ message: 'Server error' });
  }
});

// POST /attendees - Thêm attendee mới
router.post('/', async (req, res) => {
    try {
        const { attendeeId, userId, eventsRegistered, qr } = req.body;
        console.log(req.body)
        const newAttendee = new Attendee({
            attendeeId,
            userId,
            eventsRegistered: eventsRegistered || [],
            qrCodes: {
                qr,
                checkedIn: false
            }
        });

        const saved = await newAttendee.save();
        res.status(201).json(saved);
    } catch (error) {
        res.status(500).json({ error: 'Lỗi khi thêm attendee: ' + error.message });
    }
});

module.exports = router;
