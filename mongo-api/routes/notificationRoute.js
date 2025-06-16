const express = require('express');
const router = express.Router();
const UserNotify = require('../models/UserNotify');
router.post('/api/send-notification', async (req, res) => {
    const { userId, title, body, type } = req.body;

    try {
        const userRecord = await UserNotify.findOne({ userId });
        if (!userRecord || !userRecord.fcmToken) {
            return res.status(404).json({ error: 'Không tìm thấy token' });
        }

        const message = {
            notification: { title, body },
            data: { type },
            token: userRecord.fcmToken,
        };

        await admin.messaging().send(message);
        res.json({ success: true, message: 'Thông báo đã được gửi!' });

    } catch (error) {
        console.error("Lỗi gửi thông báo:", error);
        res.status(500).json({ error: "Không thể gửi thông báo" });
    }
});

// POST /api/save-token
router.post('/api/save-token', async (req, res) => {
    const { userId, fcmToken } = req.body;

    if (!userId || !fcmToken) {
        return res.status(400).json({ error: 'Thiếu userId hoặc fcmToken' });
    }

    try {
        // Tìm xem userId đã tồn tại chưa
        let userRecord = await UserNotify.findOne({ userId });

        if (userRecord) {
            // Nếu đã có, cập nhật fcmToken
            userRecord.fcmToken = fcmToken;
        } else {
            // Nếu chưa có, tạo mới bản ghi
            userRecord = new UserNotify({ userId, fcmToken });
        }

        // Lưu vào MongoDB
        await userRecord.save();

        res.json({ success: true, message: 'Token đã được lưu thành công' });

    } catch (error) {
        console.error('Lỗi khi lưu token:', error);
        res.status(500).json({ error: 'Không thể lưu token' });
    }
});

module.exports = router;