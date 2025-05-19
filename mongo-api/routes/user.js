const express = require('express');
const router = express.Router();
const User = require('../models/User');

// Đăng ký hoặc cập nhật user
router.post('/register-or-update', async (req, res) => {
    const { userId, email, name, role, location, latitude, longitude } = req.body;

    if (!userId || !email) return res.status(400).json({ message: 'Missing fields' });

    try {
        let user = await User.findOne({ userId });

        if (user) {
            user.email = email;
            user.name = name;
            user.role = role;
            user.location = location;
            user.latitude = latitude;
            user.longitude = longitude;
            await user.save();
        } else {
            user = new User({ userId, email, name, role, location, latitude, longitude });
            await user.save();
        }

        res.status(200).json({ message: 'User saved', user });
    } catch (err) {
        res.status(500).json({ message: 'Server error', error: err.message });
    }
});

// Lấy thông tin user theo ID
router.get('/:uid', async (req, res) => {
    try {
        const user = await User.findOne({ userId: req.params.uid });
        if (!user) return res.status(404).json({ message: 'User not found' });
        res.json(user);
    } catch (err) {
        res.status(500).json({ message: 'Server error', error: err.message });
    }
});

module.exports = router;
