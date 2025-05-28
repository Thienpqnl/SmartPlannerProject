const express = require('express');
const router = express.Router();
const Booking = require('../models/Booking');
const User = require('../models/User');
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
module.exports = router;
