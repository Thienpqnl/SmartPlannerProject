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


// POST /attendee/checking
router.post('/checking', async (req, res) => {
    const { userId, eventId } = req.body;

    if (!userId || !eventId) {
        return res.status(400).json({ success: false, message: "Thiếu thông tin userId hoặc eventId" });
    }

    try {
        const attendee = await Attendee.findOne({ userId });

        if (!attendee) {
            return res.status(404).json({ success: false, message: "Không tìm thấy người tham dự." });
        }

        // Kiểm tra sự kiện có được đăng ký không
        if (!attendee.eventsRegistered.includes(eventId)) {
            return res.status(400).json({ success: false, message: "Người dùng chưa đăng ký sự kiện này." });
        }

        // Cập nhật trạng thái check-in
        attendee.qrCodes.checkedIn = true;
        await attendee.save();

        return res.status(200).json({ success: true, message: "Check-in thành công." });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ success: false, message: "Lỗi server." });
    }
});

module.exports = router;
