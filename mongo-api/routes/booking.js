const express = require('express');
const router = express.Router();
const Booking = require('../models/Booking');
const Organizer = require('../models/Organizer');
const Event = require('../models/Event');
const { v4: uuidv4 } = require('uuid');


router.get('/user/:userId', async (req, res) => {
    const { userId } = req.params;
    try {
        const bookings = await Booking.find({ userId });

        if (!bookings || bookings.length === 0) {
            return res.status(404).json({ error: "Không tìm thấy booking nào cho userId này." });
        }

        res.status(200).json(bookings);
    } catch (err) {
        console.error("Lỗi khi lấy danh sách Booking:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// Tạo Booking mới
router.post('/', async (req, res) => {
    const { idEvent, creatorId, userId, date, time } = req.body;
    console.log("Data sent to server:", req.body);

    if (!idEvent || !creatorId || !userId || !date || !time) {
        return res.status(400).json({ error: "Thiếu thông tin cần thiết." });
    }

    try {
        // Lấy sự kiện từ idEvent
        const event = await Event.findOne({ _id: idEvent });
        if (!event) {
            return res.status(404).json({ error: "Không tìm thấy Event với idEvent này." });
        }

        // Lấy số lượng seats từ sự kiện
        const maxSeats = event.seats;

        // Lấy danh sách booking từ Organizer
        const organizer = await Organizer.findOne({ idEvent: idEvent });
        if (!organizer) {
            return res.status(404).json({ error: "Không tìm thấy Organizer với idEvent này." });
        }

        const currentBookingsCount = organizer.listIdBooking.length;

        // Kiểm tra xem số lượng booking đã đạt giới hạn seats chưa
        if (currentBookingsCount >= maxSeats) {
            return res.status(400).json({ error: "Số lượng booking đã đạt giới hạn seats." });
        }

        // Tạo URL QR code
        const qrData = `${idEvent}_${creatorId}_${userId}_${date}_${time}`;
        const urlQR = `https://api.qrserver.com/v1/create-qr-code/?size=350x350&data=${encodeURIComponent(qrData)}`;

        // Tạo Booking mới
        const idBooking = uuidv4();
        const newBooking = new Booking({
            id: idBooking,
            idEvent,
            creatorId,
            userId,
            urlQR,
            date,
            time,
        });

        // Lưu Booking vào database
        const savedBooking = await newBooking.save();

        // Thêm idBooking vào Organizer
        const updatedOrganizer = await Organizer.findOneAndUpdate(
            { idEvent: idEvent },
            { $addToSet: { listIdBooking: idBooking } },
            { new: true }
        );

        if (!updatedOrganizer) {
            return res.status(404).json({ error: "Không tìm thấy Organizer với idEvent này." });
        }

        res.status(201).json(savedBooking);
    } catch (err) {
        console.error("Lỗi khi tạo Booking:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});
router.delete('/:idBooking', async (req, res) => {
    const { idBooking } = req.params;

    try {
        // Tìm và xóa Booking
        const deletedBooking = await Booking.findOneAndDelete({ id: idBooking });

        if (!deletedBooking) {
            return res.status(404).json({ error: "Không tìm thấy Booking với idBooking được cung cấp." });
        }

        // Xóa idBooking khỏi Organizer
        const updatedOrganizer = await Organizer.findOneAndUpdate(
            { listIdBooking: idBooking },  // Tìm organizer chứa idBooking
            { $pull: { listIdBooking: idBooking } }, // Xóa idBooking
            { new: true }
        );

        if (!updatedOrganizer) {
            return res.status(404).json({ error: "Không tìm thấy Organizer chứa Booking này." });
        }

        const bookings = await Booking.find({ userId: deletedBooking.userId });

        res.status(200).json(bookings);
    } catch (err) {
        console.error("Lỗi khi xóa Booking:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

module.exports = router;
