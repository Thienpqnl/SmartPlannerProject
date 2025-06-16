const express = require('express');
const router = express.Router();
const Friend = require('../models/Friend');
const Message = require('../models/Message');
const User = require('../models/User');

// Gửi lời mời kết bạn - Để backend tự sinh status và createdAt
router.post('/addFriend', async (req, res) => {
    try {
        const { userA, userB } = req.body;
        if (!userA || !userB) {
            return res.status(400).json({ message: 'Thiếu thông tin.' });
        }
        const friend = await Friend.findOne({
            $or: [
                { userA: userA, userB: userB },
                { userA: userB, userB: userA }
            ]
        });
        if (!friend) {
            const status = { [userA]: 'sent', [userB]: 'pending' };
            const createdAt = new Date();
            const newFriend = new Friend({ userA, userB, status, createdAt });
            await newFriend.save();
            return res.status(200).json({ message: 'Đã gửi lời mời bạn thành công' });
        }
        return res.status(400).json({ message: 'Lỗi gửi lời mời kết bạn' });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Chấp nhận lời mời kết bạn - Chỉ cho phép accept nếu đúng trạng thái
router.post('/acceptFriend', async (req, res) => {
    try {
        const { userA, userB } = req.body;
        if (!userA || !userB) {
            return res.status(400).json({ message: 'Thiếu thông tin.' });
        }
        const friend = await Friend.findOne({
            $or: [
                { userA: userA, userB: userB },
                { userA: userB, userB: userA }
            ]
        });

        if (friend) {
            // Kiểm tra trạng thái hợp lệ
            if (
                (friend.status[userA] === 'sent' && friend.status[userB] === 'pending') ||
                (friend.status[userB] === 'sent' && friend.status[userA] === 'pending')
            ) {
                friend.status[userA] = 'accepted';
                friend.status[userB] = 'accepted';
                await friend.save();
                return res.status(200).json({ message: 'Kết bạn thành công' });
            } else {
                return res.status(400).json({ message: 'Không thể chấp nhận kết bạn ở trạng thái hiện tại.' });
            }
        }
        return res.status(400).json({ message: 'Lời mời không tồn tại' });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Xoá bạn - chỉ khi đã là bạn
router.post('/deleteFriend', async (req, res) => {
    try {
        const { userA, userB } = req.body;
        if (!userA || !userB) {
            return res.status(400).json({ message: 'Thiếu thông tin.' });
        }
        const friend = await Friend.findOne({
            $or: [
                { userA: userA, userB: userB },
                { userA: userB, userB: userA }
            ]
        });

        if (friend) {
            if (friend.status[userA] === 'accepted' && friend.status[userB] === 'accepted') {
                friend.status[userA] = 'deleted';
                friend.status[userB] = 'deleted';
                await friend.save();
                return res.status(200).json({ message: 'Xoá kết bạn thành công' });
            } else {
                return res.status(400).json({ message: 'Hai bạn chưa phải là bạn' });
            }
        }
        return res.status(400).json({ message: 'Hai bạn chưa phải là bạn' });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Block bạn - ai block thì set status người đó là 'blocked'
router.post('/blockFriend', async (req, res) => {
    try {
        const { userA, userB } = req.body;
        if (!userA || !userB) {
            return res.status(400).json({ message: 'Thiếu thông tin.' });
        }
        const friend = await Friend.findOne({
            $or: [
                { userA: userA, userB: userB },
                { userA: userB, userB: userA }
            ]
        });

        if (friend) {
            if (friend.status[userA] !== 'blocked') {
                friend.status[userA] = 'blocked';
                await friend.save();
                return res.status(200).json({ message: 'Block thành công' });
            } else {
                return res.status(400).json({ message: 'Bạn đã block người này rồi.' });
            }
        }
        return res.status(400).json({ message: 'Hai bạn chưa phải là bạn' });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Gửi tin nhắn
router.post('/sendMessage', async (req, res) => {
    try {
        const { friendId, sender, content } = req.body;
        if (!friendId || !sender || !content) {
            return res.status(400).json({ message: 'Thiếu thông tin.' });
        }
        // Kiểm tra mối quan hệ friendId hợp lệ
        const friend = await Friend.findById(friendId);
        if (!friend || friend.status[sender] === 'blocked') {
            return res.status(400).json({ message: 'Không thể gửi tin nhắn.' });
        }
        const newMessage = new Message({
            friendId,
            sender,
            content,
            status: 'sent',
            isRead: false,
            createdAt: new Date()
        });
        await newMessage.save();
        return res.status(200).json({ message: 'Gửi tin nhắn thành công' });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Lấy danh sách tin nhắn giữa 2 người (phân trang)
router.get('/listMessage/:friendId/:page/:size', async (req, res) => {
    try {
        const { friendId, page, size } = req.params;
        const skip = (parseInt(page) - 1) * parseInt(size);
        const messages = await Message.find({ friendId })
            .sort({ createdAt: -1 })
            .skip(skip)
            .limit(parseInt(size));
        return res.status(200).json(messages.reverse()); // đảo lại cho đúng thứ tự thời gian tăng dần
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Danh sách bạn bè đã kết bạn của user
router.get('/listFriend/:userId', async (req, res) => {
    try {
        const { userId } = req.params;
        const friends = await Friend.find({
            $or: [{ userA: userId }, { userB: userId }],
            [`status.${userId}`]: 'accepted'
        });
        return res.status(200).json(friends);
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Danh sách người đã gửi lời mời kết bạn đi từ user (user là người gửi)
router.get('/listSentFriend/:userId', async (req, res) => {
    try {
        const { userId } = req.params;
        const friends = await Friend.find({
            [`status.${userId}`]: 'sent'
        });
        return res.status(200).json(friends);
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Danh sách người đã gửi lời mời kết bạn tới user (user là người nhận)
router.get('/listAddFriend/:userId', async (req, res) => {
    try {
        const { userId } = req.params;
        const friends = await Friend.find({
            [`status.${userId}`]: 'pending'
        });
        return res.status(200).json(friends);
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});



module.exports = router;