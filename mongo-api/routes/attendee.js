const express = require('express');
const router = express.Router();
const Booking = require('../models/Booking');
const Attendee = require('../models/Attendee');

// ✅ 1. Lấy danh sách người tham gia theo event
router.get('/getListByEvent/:eventId', async (req, res) => {
  const { eventId } = req.params;
  try {
    const users = await Booking.aggregate([
      { $match: { idEvent: eventId } },
      {
        $lookup: {
          from: "users",
          localField: "userId",
          foreignField: "userId",
          as: "user"
        }
      },
      { $unwind: "$user" },
      { $replaceRoot: { newRoot: "$user" } },
    ]);

    res.json(users);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Lỗi server" });
  }
});


// ✅ 2. API check-in bằng QR code
// POST /attendee/checking
router.post('/checking', async (req, res) => {
  const { qrCode } = req.body;

  if (!qrCode) {
    return res.status(400).json({ message: 'QR code is required' });
  }

  try {
    // Tìm attendee theo mã QR
    const attendee = await Attendee.findOne({ 'qrCodes.qr': qrCode });

    if (!attendee) {
      return res.status(404).json({ message: 'Attendee not found with this QR code' });
    }

    // Nếu đã check-in rồi
    if (attendee.qrCodes.checkedIn) {
      return res.status(400).json({ message: 'Attendee has already checked in' });
    }

    // Cập nhật trạng thái check-in
    attendee.qrCodes.checkedIn = true;
    await attendee.save();

    res.status(200).json({
      message: 'Check-in successful',
      userId: attendee.userId,
      eventsRegistered: attendee.eventsRegistered
    });
  } catch (err) {
    console.error('Error during check-in:', err);
    res.status(500).json({ message: 'Server error during check-in' });
  }
});



module.exports = router;
