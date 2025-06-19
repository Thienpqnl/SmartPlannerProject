const express = require('express');
const router = express.Router();
const Friend = require('../models/Friend');
const Message = require('../models/Message');
const User = require('../models/User');
const { getIO, onlineUsers } = require('../socket');
// Gửi lời mời kết bạn
router.post('/addFriend', async (req, res) => {
    try {
        const io = getIO();
        const { from, to } = req.body;
        if (!from || !to) {
            return res.status(400).json({ message: 'Thiếu thông tin.' });
        }
        // Kiểm tra đã tồn tại mối quan hệ chưa (theo cả hai chiều)
        const friend = await Friend.findOne({
            $or: [
                { userA: from, userB: to },
                { userA: to, userB: from }
            ]
        });
        if (friend) {
            return res.status(400).json({ message: 'Đã có lời mời kết bạn hoặc đã là bạn.' });
        }
        // Lưu đúng vai trò
        const newFriend = new Friend({
            userA: from,
            userB: to,
            statusA: 'sent',    // Người gửi
            statusB: 'pending'  // Người nhận
        });
        await newFriend.save();
        const toSocketId = onlineUsers.get(to);
            if (toSocketId) {
              io.to(toSocketId).emit('receive invite', {
                to: to
              });
               io.to(toSocketId).emit('receive notification', {
                          type: 'addFriend',
                          content: 'Bạn có lời mời kết bạn mới'
                        });
            }

        return res.status(200).json({ message: 'Đã gửi lời mời bạn thành công' });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Chấp nhận lời mời kết bạn (userA: gửi, userB: nhận)
router.post('/acceptFriend', async (req, res) => {
    try {
        const { from, to } = req.body; // from: người gửi, to: người nhận/chấp nhận
        if (!from || !to) {
            return res.status(400).json({ message: 'Thiếu thông tin.' });
        }

        // Tìm đúng chiều: userA là người gửi, userB là người nhận
        const friend = await Friend.findOne({
            userA: from,
            userB: to,
            statusA: 'sent',
            statusB: 'pending'
        });

        if (friend) {
            // Chấp nhận: chuyển cả hai status thành 'accepted'
            friend.statusA = 'accepted';
            friend.statusB = 'accepted';
            await friend.save();
            return res.status(200).json({ message: 'Kết bạn thành công' });
        }
        return res.status(400).json({ message: 'Lời mời không tồn tại hoặc đã được xử lý.' });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});
router.post('/rejectFriend', async (req, res) => {
    try {
        const { from, to } = req.body;
        if (!userA || !userB) {
            return res.status(400).json({ message: 'Thiếu thông tin.' });
        }
        // Tìm lời mời kết bạn đúng chiều
        const friend = await Friend.findOne({
            $or: [
                { userA: from, userB: to, statusA: 'sent', statusB: 'pending' }, // userB gửi, userA nhận
                { userA: to, userB: from, statusA: 'pending', statusB: 'sent' }  // userA nhận, userB gửi (phòng trường hợp lưu chiều ngược)
            ]
        });

        if (friend) {
            await Friend.deleteOne({ _id: friend._id });
            return res.status(200).json({ message: 'Đã từ chối lời mời kết bạn.' });
        }
        return res.status(400).json({ message: 'Lời mời không tồn tại hoặc đã xử lý.' });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Xóa bạn - chỉ khi đã là bạn
router.post('/deleteFriend', async (req, res) => {
    try {
        const { userA, userB } = req.body;
        if (!userA || !userB) {
            return res.status(400).json({ message: 'Thiếu thông tin.' });
        }
        const friend = await Friend.findOne({
            $or: [
                { userA, userB },
                { userA: userB, userB: userA }
            ]
        });

        if (friend && friend.statusA === 'accepted' && friend.statusB === 'accepted') {
            friend.statusA = 'deleted';
            friend.statusB = 'deleted';
            await friend.save();
            return res.status(200).json({ message: 'Xóa kết bạn thành công' });
        }
        return res.status(400).json({ message: 'Hai bạn chưa phải là bạn' });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Block bạn - ai block thì status của người đó là 'blocked'
router.post('/blockFriend', async (req, res) => {
    try {
        const { userA, userB } = req.body;
        if (!userA || !userB) {
            return res.status(400).json({ message: 'Thiếu thông tin.' });
        }
        const friend = await Friend.findOne({
            $or: [
                { userA, userB },
                { userA: userB, userB: userA }
            ]
        });

        if (friend) {
            // Xác định ai là userA trong friend
            if (friend.userA === userA && friend.statusA !== 'blocked') {
                friend.statusA = 'blocked';
                await friend.save();
                return res.status(200).json({ message: 'Block thành công' });
            }
            if (friend.userB === userA && friend.statusB !== 'blocked') {
                friend.statusB = 'blocked';
                await friend.save();
                return res.status(200).json({ message: 'Block thành công' });
            }
            return res.status(400).json({ message: 'Bạn đã block người này rồi.' });
        }
        return res.status(400).json({ message: 'Hai bạn chưa phải là bạn' });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Gửi tin nhắn
router.post('/sendMessage', async (req, res) => {
    const io = getIO();
    console.log('IO INSTANCE:', io);
    try {
        const { friendId, sender, content } = req.body;
        if (!friendId || !sender || !content) {
            return res.status(400).json({ message: 'Thiếu thông tin.' });
        }
        const friend = await Friend.findById(friendId);
        if (!friend) {
            return res.status(400).json({ message: 'Không tồn tại mối quan hệ bạn bè.' });
        }
        // Kiểm tra block
        if (
            (friend.userA === sender && friend.statusA === 'blocked') ||
            (friend.userB === sender && friend.statusB === 'blocked')
        ) {
            return res.status(400).json({ message: 'Bạn đã block người này.' });
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

        let toUserId;
        if (friend.userA === sender) {
          toUserId = friend.userB;
        } else {
          toUserId = friend.userA;
        }
        // gửi message qua socket
        const toSocketId = onlineUsers.get(toUserId);
            if (toSocketId) {
              io.to(toSocketId).emit('chat message', {
                friendId: friendId,
                fromUserId: sender,
                status: 'sent',
                message: content
            });
            io.to(toSocketId).emit('receive notification', {
                                      type: 'message',
                                      content: 'Bạn có tin nhắn mới'
                                    });
        }

        return res.status(200).json({ message: 'Gửi tin nhắn thành công' });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Lấy danh sách tin nhắn theo friendId (phân trang)
router.get('/listMessage/:friendId/:page/:size', async (req, res) => {
    try {
        const { friendId, page, size } = req.params;
        const currentPage = Math.max(1, parseInt(page)); // đảm bảo >= 1
        const limit = Math.max(1, parseInt(size));
        const skip = (currentPage - 1) * limit;

        // Kiểm tra tồn tại mối quan hệ bạn bè
        const friend = await Friend.findById(friendId);
        if (!friend) {
            return res.status(404).json({ message: 'Không tìm thấy mối quan hệ bạn bè.' });
        }

        // Lấy danh sách tin nhắn
        const messages = await Message.find({ friendId: friendId })
            .sort({ createdAt: -1 })
            .skip(skip)
            .limit(limit);

        return res.status(200).json(messages.reverse());
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Danh sách bạn bè đã kết bạn của user
router.get('/listFriend/:userId', async (req, res) => {
    try {
        const { userId } = req.params;
        // Lấy tất cả các mối quan hệ bạn bè đã accepted
        const friends = await Friend.find({
            $or: [{ userA: userId }, { userB: userId }],
            statusA: 'accepted',
            statusB: 'accepted'
        });

        // Lấy danh sách userId của bạn bè (người còn lại trong mỗi mối quan hệ)
        const friendIds = friends.map(f =>
            f.userA === userId ? f.userB : f.userA
        );

        // Lấy thông tin user theo danh sách friendIds (object { userId: user })
        const users = await User.find({ userId: { $in: friendIds } });
        // Chuyển users thành map để dễ lấy
        const userMap = {};
        users.forEach(u => {
            userMap[u.userId] = u;
        });
        // Map lại danh sách trả về đúng { friendId, user }
        const result = friends.map(f => {
            const fid = f._id;
            const friendUserId = f.userA === userId ? f.userB : f.userA;
            return {
                friendId: fid,
                user: userMap[friendUserId] || null
            };
        });

        return res.status(200).json(result);
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Danh sách người đã gửi lời mời kết bạn đi từ user (user là người gửi)
router.get('/listSentFriend/:userId', async (req, res) => {
    try {
        const { userId } = req.params;
        // user là người gửi => userA = userId, statusA = 'sent', statusB = 'pending'
        const friends = await Friend.find({
            userA: userId,
            statusA: 'sent',
            statusB: 'pending'
        });
        return res.status(200).json(friends);
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

// Danh sách user đã gửi lời mời kết bạn cho userId (userId là người nhận)
router.get('/listReceivedFriend/:userId', async (req, res) => {
    try {
        const { userId } = req.params;
        // Tìm các lời mời mà userId là người nhận (userB)
        const friends = await Friend.find({
            userB: userId,
            statusA: 'sent',
            statusB: 'pending'
        });
        // Lấy danh sách userA (người gửi lời mời)
        const userIds = friends.map(f => f.userA);
        // Lấy thông tin user
        const users = await User.find({ userId: { $in: userIds } });
        return res.status(200).json(users);
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});
router.get('/listChatBox/:userId', async (req, res) => {
    try {
        const { userId } = req.params;
        // 1. Lấy tất cả các mối quan hệ bạn bè đã accepted
        const friends = await Friend.find({
            $or: [{ userA: userId }, { userB: userId }],
            statusA: 'accepted',
            statusB: 'accepted'
        });

        // 2. Lấy danh sách userId của bạn bè
        const friendIds = friends.map(f =>
            f.userA === userId ? f.userB : f.userA
        );

        // 3. Lấy thông tin user theo danh sách friendIds
        const users = await User.find({ userId: { $in: friendIds } });
        const userMap = {};
        users.forEach(u => {
            userMap[u.userId] = u;
        });

        // 4. Lấy tin nhắn cuối cùng cho mỗi cuộc hội thoại
        const result = await Promise.all(friends.map(async f => {
            const fid = f._id;
            const friendUserId = f.userA === userId ? f.userB : f.userA;

            // Chỉ lấy tin nhắn cuối cùng
            const lastMessage = await Message.findOne({ friendId: fid }).sort({ createdAt: -1 });

            return {
                friendId: fid,
                user: userMap[friendUserId] || null,
                lastMessage: lastMessage || null
            };
        }));

        return res.status(200).json(result);
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});


router.post('/setIsRead', async (req, res) => {
    try {
        const { friendId, userId } = req.body;
        if (!friendId || !userId) {
            return res.status(400).json({ message: "Thiếu friendId hoặc userId." });
        }
        // Đánh dấu tất cả tin nhắn trong đoạn chat friendId mà sender khác user hiện tại là đã đọc
        const result = await Message.upd
        ateMany(
            {
                friendId: friendId,
                sender: { $ne: userId }, // sender khác userId, tức là của bạn bè gửi tới user này
                isRead: false
            },
            { $set: { isRead: true } }
        );



        return res.status(200).json({
            message: 'Đã đánh dấu là đã đọc',
            modifiedCount: result.modifiedCount || result.nModified
        });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});

router.post('/status', async (req, res) => {
    try {
        const { from, to } = req.body;
        if (!from || !to) {
            return res.status(400).json({ message: "Thiếu from hoặc to." });
        }

        // Tìm mối quan hệ bạn bè giữa 2 user (có thể userA gửi cho userB hoặc ngược lại)
        const friendship = await Friend.findOne({
            $or: [
                { userA: from, userB: to },
                { userA: to, userB: from }
            ]
        });

        if (!friendship) {
            return res.status(404).json({ message: "Không tìm thấy mối quan hệ bạn bè." });
        }

        // Xác định tình trạng của từng phía
        let statusFrom, statusTo;
        if (friendship.userA === from) {
            statusFrom = friendship.statusA;
            statusTo = friendship.statusB;
        } else {
            statusFrom = friendship.statusB;
            statusTo = friendship.statusA;
        }

        return res.status(200).json({
            from,
            to,
            statusFrom,
            statusTo
        });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ message: 'Lỗi server.' });
    }
});
module.exports = router;