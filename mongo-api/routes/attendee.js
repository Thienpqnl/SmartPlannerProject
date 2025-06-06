const express = require('express');
const router = express.Router();
const Booking = require('../models/Booking');
const Attendee = require('../models/Attendee');
const User = require('../models/User');
const nodemailer = require('nodemailer');

// GET /attendee/getListByEvent/:eventId
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
      {
        $project: {
          user: "$user",
          bookingDate: "$date"
        }
      }
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
        if(qrCode.checkedIn) {
           return res.status(400).json({ success: false, message: "Người dùng đã check-in trước đó." });
        }
         qrCode.checkedIn = true;
         qrCode.checkInTime = new Date();
         await attendee.save();
        return res.status(200).json({ success: true, message: "Check-in thành công." });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ success: false, message: "Lỗi server." });
    }
});
router.get('/getListAttendeeHasCheckInEvent/:eventId', async (req, res) => {
  const { eventId } = req.params;
  if (!eventId) {
    return res.status(400).json({ success: false, message: "Thiếu thông tin eventId" });
  }
  try {
    // Lấy attendee có check-in event này
    const attendees = await Attendee.find({
      qrCodes: { $elemMatch: { eventId, checkedIn: true } }
    });

    if (!attendees || attendees.length === 0) {
      return res.status(400).json({ success: false, message: "Sự kiện chưa có ai tham gia" });
    }

    // Lấy thông tin user kèm checkInTime
    const results = [];

    for (const attendee of attendees) {
      // Lấy qrCode đã check-in cho event này
      const qr = attendee.qrCodes.find(qr =>
        qr.eventId === eventId && qr.checkedIn === true
      );
      if (!qr) continue;

      // Lấy thông tin user
      const user = await User.findOne({ userId: attendee.userId });
      if (!user) continue;
      results.push({
        user,
        bookingDate: qr.checkInTime,
      });
    }

    res.json(results);

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
router.delete('/deleteAttendee/:eventId/:userId', async (req, res) => {
  const {eventId,userId} = req.params;
  try {
    // Xoá tất cả qrCodes có eventId trùng
    const result = await Attendee.updateOne(
      { userId: userId },
      { $pull: { qrCodes: { eventId: eventId } } }
    );
    res.json({ message: "Đã xoá qrCodes thành công", result });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Lỗi server" });
  }
});
// gửi email -> cấu hình
const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'vtthanh32004@gmail.com',
        pass: 'qvwr ktll cftj cnqs'
    }
});
router.post('/sendEmailAboutDeteleBookTicket', async (req, res) => {
    const { from, to, subject, content } = req.body;

    if (!from || !to || !subject || !content) {
        return res.status(400).json({ message: 'Thiếu thông tin email.' });
    }

    const mailOptions = {
        from,
        to,
        subject,
        text: content
    };
    try {
        await transporter.sendMail(mailOptions);
        res.json({ message: 'Gửi email thành công.' });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Lỗi gửi email.' });
    }
});
module.exports = router;
