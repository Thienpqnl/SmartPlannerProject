const express = require('express');
const router = express.Router();
const Booking = require('../models/Booking');
const { v4: uuidv4 } = require('uuid');

// Tạo Booking mới
router.post('/', async (req, res) => {
    const { idEvent, userId, createdAt } = req.body;
    console.log("Data sent to server:", req.body);
    if (!idEvent || !userId||!createdAt) {
        return res.status(400).json({ error: "Thiếu thông tin cần thiết." });
    }

    try {
        // Tạo URL QR code
        const qrData = `${idEvent}_${userId}_${createdAt}`;
        const urlQR = `https://api.qrserver.com/v1/create-qr-code/?size=350x350&data=${encodeURIComponent(qrData)}`;

        // Tạo Booking mới
        const newBooking = new Booking({
            id: uuidv4(), // Tạo UUID để làm id duy nhất
            idEvent,
            userId,
            urlQR,
            createdAt,
        });

        // Lưu vào database
        const savedBooking = await newBooking.save();

        // Trả về Booking đã tạo
        res.status(201).json(savedBooking);
    } catch (err) {
        console.error("Lỗi khi tạo Booking:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

module.exports = router;
