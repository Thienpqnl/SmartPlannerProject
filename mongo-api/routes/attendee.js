const express = require('express');
const router = express.Router();
const Booking = require('../models/Booking');
const Attendee = require('../models/Attendee');

// 1. Lấy danh sách người đặt vé tham gia theo event
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
        const qrCode = attendee.qrCodes.find(qr => qr.eventId === eventId);
        // không tìm thấy qr code có eventId trùng với eventId đưa vào báo lỗi
        if (!qrCode) {
           return res.status(400).json({ success: false, message: "Người dùng chưa đăng ký sự kiện này." });
        }
         qrCode.checkedIn = true;
         await attendee.save();
        return res.status(200).json({ success: true, message: "Check-in thành công." });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ success: false, message: "Lỗi server." });
    }
});
router.get('/getListAttendeeHasCheckInEvent/:eventId', async (req, res) => {
  const { eventId } = req.params;
  if(!eventId){
    return res.status(400).json({ success: false, message: "Thiếu thông tin eventId" });
  }
  try {
    // Tìm các attendee có qrCodes chứa eventId và checkedIn = true
    const attendees = await Attendee.find({
      qrCodes: { $elemMatch: { eventId, checkedIn: true } }
    });
    if(!attendees){
       return res.status(400).json({ success: false, message: "Sự kiện chưa có ai tham gia" });
    }
    // Trả về danh sách userId
    const userIds = attendees.map(att => att.userId);
    const users = await User.find({ _id: { $in: userIds } });
    res.json(users);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Lỗi server" });
  }
});

router.get('/getListEventUserHasCheckIn/:userId', async (req, res) => {
  const { userId } = req.params;
  try {
    const attendee = await Attendee.findOne({ userId });
    if (!attendee) return res.json([]);
    // Lọc ra các eventId mà user đã checkIn = true
    const checkedInEvents = attendee.qrCodes
      .filter(qr => qr.checkedIn)
      .map(qr => qr.eventId);
    res.json(checkedInEvents);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Lỗi server" });
  }
});
module.exports = router;
