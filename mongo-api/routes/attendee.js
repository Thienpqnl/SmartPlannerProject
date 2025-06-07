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
      { $match: { idEvent: eventId, status:'doing'} },
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

        // Tìm qrCode hợp lệ (status "doing") cho eventId này
        const qrCode = attendee.qrCodes.find(qr => qr.eventId === eventId && qr.status === 'doing');
        if (!qrCode) {
           return res.status(400).json({ success: false, message: "Người dùng chưa đăng ký sự kiện này hoặc đã bị xoá." });
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

// GET /attendee/getListAttendeeHasCheckInEvent/:eventId
router.get('/getListAttendeeHasCheckInEvent/:eventId', async (req, res) => {
  const { eventId } = req.params;
  if (!eventId) {
    return res.status(400).json({ success: false, message: "Thiếu thông tin eventId" });
  }
  try {
    // Lấy attendee có check-in event này và qrCode status "doing"
    const attendees = await Attendee.find({
      qrCodes: { $elemMatch: { eventId, checkedIn: true, status: "doing" } }
    });

    if (!attendees || attendees.length === 0) {
      return res.status(400).json({ success: false, message: "Sự kiện chưa có ai tham gia" });
    }

    // Lấy thông tin user kèm checkInTime
    const results = [];

    for (const attendee of attendees) {
      // Lấy qrCode đã check-in cho event này với status "doing"
      const qr = attendee.qrCodes.find(qr =>
        qr.eventId === eventId && qr.checkedIn === true && qr.status === "doing"
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

// GET /attendee/getListEventUserHasCheckIn/:userId
router.get('/getListEventUserHasCheckIn/:userId', async (req, res) => {
  const { userId } = req.params;
  try {
    const attendee = await Attendee.findOne({ userId });
    if (!attendee) return res.json([]);
    // Lọc ra các eventId mà user đã checkIn = true, status = "doing"
    const checkedInEvents = attendee.qrCodes
      .filter(qr => qr.checkedIn && qr.status === "doing")
      .map(qr => qr.eventId);
    res.json(checkedInEvents);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Lỗi server" });
  }
});

// DELETE /attendee/deleteAttendee/:eventId/:userId
router.delete('/deleteAttendee/:userId/:eventId', async (req, res) => {
  const { userId, eventId } = req.params;
  try {
    // Xoá mềm attendee: cập nhật mọi qrCodes của userId, eventId này, status "doing", checkedIn: false
    const attendeeResult = await Attendee.updateOne(
      { userId },
      { $set: { "qrCodes.$[elem].status": "delete" } },
      {
        arrayFilters: [
          { "elem.eventId": eventId, "elem.status": "doing", "elem.checkedIn": false }
        ]
      }
    );

    // Xoá mềm booking: chỉ chuyển status nếu userId, eventId đúng và status đang là "doing"
    const bookingResult = await Booking.updateOne(
      { userId, idEvent: eventId, status: "doing" },
      { $set: { status: "delete" } }
    );

    // Nếu cả hai đều không tìm thấy, trả về 404
    if (attendeeResult.matchedCount === 0 && bookingResult.matchedCount === 0) {
      return res.status(404).json({
        message: "Không tìm thấy user hoặc eventId tương ứng ở cả Attendee và Booking."
      });
    }

    res.json({
      message: "Đã cập nhật trạng thái xoá thành công.",
      attendeeResult,
      bookingResult
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Lỗi server khi cập nhật trạng thái xoá." });
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
// POST /attendee/sendEmailAboutDeteleBookTicket
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
router.post('/sendEmailInvite', async (req, res) => {
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
//router.get('/backup', async (req,res) =>{
//    await Booking.updateMany(
//      { status: { $exists: false } },
//      { $set: { status: "doing" } }
//    )
//})
module.exports = router;