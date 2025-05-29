const express = require('express');
const router = express.Router();
const Organizer = require('../models/Organizer');
const { v4: uuidv4 } = require('uuid');
// Thêm Organizer mới
router.post('/', async (req, res) => {
    const { userId, idEvent, idBookings } = req.body;

    if (!userId) {
        return res.status(400).json({ error: "Thiếu thông tin cần thiết." });
    }

    try {
        const id=uuidv4();
        const newOrganizer = new Organizer({
            id:id,
            userId,
            idEvent: idEvent || [],
            listIdBooking: idBookings || [],
        });

        await newOrganizer.save();
        res.status(201).json(newOrganizer);
    } catch (err) {
        console.error("Lỗi khi thêm Organizer:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

router.get('/:organizerId', async (req, res) => {
    const { id } = req.params;
    try {
        const organizer = await Organizer.findOne({ id });

        if (!id) {
            return res.status(404).json({ error: "Organizer không tồn tại." });
        }

        res.status(200).json(organizer);
    } catch (err) {
        console.error("Lỗi khi lấy Organizer:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// Thêm ID Booking vào danh sách
router.put('/:organizerId/bookings', async (req, res) => {
    const { id } = req.params;
    const { bookingId } = req.body;

    if (!bookingId) {
        return res.status(400).json({ error: "Thiếu ID của Booking." });
    }

    try {
        const updatedOrganizer = await Organizer.findOneAndUpdate(
            { id },
            { $addToSet: { listIdBooking: bookingId } }, 
            { new: true }
        );

        if (!updatedOrganizer) {
            return res.status(404).json({ error: "Không tìm thấy Organizer." });
        }

        res.status(200).json(updatedOrganizer);
    } catch (err) {
        console.error("Lỗi khi thêm ID Booking:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// Xóa ID Booking khỏi danh sách
router.delete('/:organizerId/bookings', async (req, res) => {
    const { userId } = req.params;
    const { bookingId } = req.body;

    if (!bookingId) {
        return res.status(400).json({ error: "Thiếu ID của Booking." });
    }

    try {
        const updatedOrganizer = await Organizer.findOneAndUpdate(
            { userId },
            { $pull: { listIdBooking: bookingId } },
            { new: true }
        );

        if (!updatedOrganizer) {
            return res.status(404).json({ error: "Không tìm thấy Organizer." });
        }

        res.status(200).json(updatedOrganizer);
    } catch (err) {
        console.error("Lỗi khi xóa ID Booking:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

module.exports = router;
