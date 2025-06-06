const express = require('express');
const router = express.Router();
const Event = require('../models/Event');
const Organizer = require('../models/Organizer');
const { v4: uuidv4 } = require('uuid');

// Lấy danh sách sự kiện
router.get('/', async (req, res) => {
    const events = await Event.find();
    res.json(events);
});

// Tạo sự kiện mới và tạo Organizer
router.post('/', async (req, res) => {
    try {
        console.log("Event nhận được:", req.body);

        // 1. Tạo Event
        const newEvent = new Event(req.body);
        await newEvent.save();

        // 2. Tạo Organizer tương ứng
        const organizer = new Organizer({
            id: uuidv4(), // hoặc dùng newEvent._id.toString() nếu bạn muốn dùng ID sự kiện
            userId: req.body.creatorUid,
            idEvent: newEvent._id.toString(), // id của sự kiện vừa tạo
            listIdBooking: []
        });

        await organizer.save();
        res.json(newEvent);
    } catch (err) {
        console.error("Lỗi khi tạo sự kiện:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

router.get('/users/:uid', async (req, res) => {
    const userId = req.params.uid;
    try {
        const events = await Event.find({ creatorUid: userId });
        res.status(200).json(events);
    } catch (err) {
        console.error("Loi khi lay su kien cua organizer:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});
router.get('/:idEvent', async (req, res) => {
    const { idEvent } = req.params;
    try {
        const event = await Event.findById(idEvent);
        if (!event) {
            return res.status(404).json({ error: "Không tìm thấy sự kiện." });
        }
        res.status(200).json(event);
    } catch (err) {
        console.error("Lỗi khi lấy sự kiện:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// PUT - cập nhật toàn bộ event
router.put("/:id", async (req, res) => {
    const { id } = req.params;
    try {
        const updatedEvent = await Event.findByIdAndUpdate(id, req.body, {
            new: true,
            runValidators: true
        });
        if (!updatedEvent) {
            return res.status(404).json({ message: "Event not found" });
        }
        res.json(updatedEvent);
    } catch (err) {
        console.error("Lỗi khi cập nhật sự kiện:", err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

module.exports = router;