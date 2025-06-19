const express = require('express');
const router = express.Router();
const Booking = require('../models/Booking');
const Attendee = require('../models/Attendee');
const User = require('../models/User');
const nodemailer = require('nodemailer');
const Event = require('../models/Event');
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
    if (checkedInEvents.length === 0) return res.json([]);
      //Lấy danh sách Event theo eventId
      const events = await Event.find({ _id: { $in: checkedInEvents } });
      res.json(events);
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
function buildMailContent(from, to, event) {
    return `Sinh chào bạn ${to}, bạn nhận được thư từ ${from} cho lời mời tham gia sự kiện ${event.name}.\nId đăng ký tham gia sự kiện: ${event._id || event.id}`;
}
router.post('/sendEmailInvite1', async (req, res) => {
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
    const { from, to, subject, events } = req.body;

    if (!from || !to || !subject || !events || !Array.isArray(events) || events.length === 0) {
        return res.status(400).json({ message: 'Thiếu thông tin email hoặc danh sách sự kiện.' });
    }

    let sendResults = [];
    for (const event of events) {
        const content = buildMailContent(from, to, event);
        const mailOptions = {
            from,
            to,
            subject,
            text: content
        };
        try {
            await transporter.sendMail(mailOptions);
            sendResults.push({ event: event.name, status: 'sent' });
        } catch (error) {
            sendResults.push({ event: event.name, status: 'error', error: error.toString() });
        }
    }

    // Nếu tất cả thành công
    const allSuccess = sendResults.every(r => r.status === 'sent');
    if (allSuccess) {
        res.json({ message: 'Gửi email thành công.', details: sendResults });
    } else {
        res.status(207).json({ message: 'Một số email gửi không thành công.', details: sendResults });
    }
});
// danh sách event user đã tạo, trước thời gian diễn ra, và userId không nằm trong danh sách hạn chế
router.get('/eventInviteEligible/:creatorId/:userId', async (req, res) => {
    const { creatorId, userId } = req.params;

    try {
        const now = Date.now();

        const events = await Event.find({
            creatorUid: creatorId,
            restrictedUserIds: { $ne: userId }
        });

        const eventsBeforeNow = events.filter(event => {
            const [day, month, year] = event.date.split('/');
            const hourMinute = event.time ? event.time.split(':') : ['0', '0'];
            const eventDateObj = new Date(
                parseInt(year),
                parseInt(month) - 1,
                parseInt(day),
                parseInt(hourMinute[0]),
                parseInt(hourMinute[1])
            );
            const eventTimestamp = eventDateObj.getTime();
            return eventTimestamp > now;
        });
        res.status(200).json(eventsBeforeNow);
    } catch (err) {
        console.error("Lỗi khi lấy sự kiện của organizer:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});
router.get('/getListRestricedUserInEvent/:eventId', async (req, res) => {
    const {eventId} = req.params;

    try {
        const now = Date.now();

        const event = await Event.findOne({
            _id: eventId,
        });
        const userIds = event.restrictedUserIds || [];
        const users = await User.find({ _id: { $in: userIds }});
        const usersWithBookingDate = users.map(user => ({
                    ...user.toObject(),
                    bookingDate: null,
                }));
        res.status(200).json(usersWithBookingDate);
    } catch (err) {
        console.error("Lỗi khi lấy sự kiện của organizer:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

router.get('/putUserInRestrictedList/:eventId/:userId', async (req, res) => {
    const { eventId, userId } = req.params;
    try {
        const event = await Event.findOne({ _id: eventId });
        if (!event) {
            return res.status(404).json({ error: "Không tìm thấy sự kiện." });
        }
        // Kiểm tra trùng lặp để tránh push nhiều lần
        if (!event.restrictedUserIds.includes(userId)) {
            event.restrictedUserIds.push(userId);
            await event.save();
        }
        res.status(200).json({ message: "Đã thêm user vào danh sách hạn chế.", restrictedUserIds: event.restrictedUserIds });
    } catch (err) {
        console.error("Lỗi khi thêm user vào restricted list:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});
router.get('/removeUserFromRestrictedList/:eventId/:userId', async (req, res) => {
    const { eventId, userId } = req.params;
    try {
        const event = await Event.findOne({ _id: eventId });
        if (!event) {
            return res.status(404).json({ error: "Không tìm thấy sự kiện." });
        }

        // Lọc ra khỏi danh sách hạn chế nếu tồn tại
        const before = event.restrictedUserIds.length;
        event.restrictedUserIds = event.restrictedUserIds.filter(id => id !== userId);

        if (event.restrictedUserIds.length < before) {
            await event.save();
            res.status(200).json({ message: "Đã xoá user khỏi danh sách hạn chế.", restrictedUserIds: event.restrictedUserIds });
        } else {
            res.status(200).json({ message: "User không nằm trong danh sách hạn chế.", restrictedUserIds: event.restrictedUserIds });
        }
    } catch (err) {
        console.error("Lỗi khi xoá user khỏi restricted list:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});
//router.get('/backup', async (req,res) =>{
//    try {
//        // Cập nhật tất cả event chưa có trường restrictedUserIds
//        const result = await Event.updateMany(
//          { restrictedUserIds: { $exists: false } },
//          { $set: { restrictedUserIds: [] } }
//        );
//        console.log(`Đã cập nhật ${result.nModified} event.`);
//      } catch (err) {
//        console.error('Lỗi khi cập nhật:', err);
//      }
//})
module.exports = router;