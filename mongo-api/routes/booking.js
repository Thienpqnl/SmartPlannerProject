const express = require('express');
const router = express.Router();
const Booking = require('../models/Booking');
const Organizer = require('../models/Organizer');
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
    const { idEvent,creatorId, userId, date, time } = req.body;
    console.log("Data sent to server:", req.body);
    if (!idEvent ||!creatorId|| !userId||!date ||!time) {
        return res.status(400).json({ error: "Thiếu thông tin cần thiết." });
    }

    try {
        // Tạo URL QR code
        const qrData = `${idEvent}_${creatorId}_${userId}_${date}_${time}`;
        const urlQR = `https://api.qrserver.com/v1/create-qr-code/?size=350x350&data=${encodeURIComponent(qrData)}`;

        // Tạo Booking mới
        const idBooking=uuidv4();
        const newBooking = new Booking({
            id: idBooking, // Tạo UUID để làm id duy nhất
            idEvent,
            creatorId,
            userId,
            urlQR,
            date,
            time,
        });

        // Lưu vào database
        const savedBooking = await newBooking.save();
        //addBooking
       const updatedOrganizer = await Organizer.findOneAndUpdate(
            { userId: creatorId }, 
            { $addToSet: { listIdBooking: idBooking } }, 
            { new: true } 
        );

        if (!updatedOrganizer) {
            return res.status(404).json({ error: "Không tìm thấy Organizer với userId này." });
        }
        // Trả về Booking đã tạo
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
