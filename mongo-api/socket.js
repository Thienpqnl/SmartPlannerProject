const { Server } = require('socket.io');

// Lưu userId <-> socket.id để quản lý online
const onlineUsers = new Map();

function initSocket(server) {
  const io = new Server(server, {
    cors: {
      origin: "*", // Chú ý cấu hình kỹ hơn khi production!
      methods: ["GET", "POST"]
    }
  });

  io.on('connection', (socket) => {
    console.log('User connected:', socket.id);

    // Đăng ký userId
    socket.on('register', (userId) => {
      if (userId) {
        onlineUsers.set(userId, socket.id);
        socket.userId = userId;
        console.log(`User registered: ${userId} with socket.id: ${socket.id}`);
      }
    });

    // Khi nhận sự kiện gửi tin nhắn từ client
    socket.on('chat message', (data) => {
      // data: { toUserId, message, fromName }
      const toSocketId = onlineUsers.get(data.toUserId);
      if (toSocketId) {
        io.to(toSocketId).emit('chat message', {
          fromUserId: socket.userId,    // ID người gửi
          fromName: data.fromName,      // Tên người gửi (lấy từ client hoặc DB)
          message: data.message
        });
      }
    });

    // Gửi thông báo kết bạn (hoặc các loại notification khác)
    socket.on('send notification', (data) => {
      // data: { toUserId, type, content }
      const toSocketId = onlineUsers.get(data.toUserId);
      if (toSocketId) {
        io.to(toSocketId).emit('receive notification', {
          from: socket.userId,
          fromName: data.fromName,
          type: data.type,
          content: data.content,
        });
      }
    });

    // Gửi notification cho nhiều user cùng lúc (nếu cần)
    socket.on('broadcast notification', (data) => {
      // data: { toUserIds: [], type, content }
      if (Array.isArray(data.toUserIds)) {
        data.toUserIds.forEach(uid => {
          const toSocketId = onlineUsers.get(uid);
          if (toSocketId) {
            io.to(toSocketId).emit('receive notification', {
              from: socket.userId,
              type: data.type,
              content: data.content
            });
          }
        });
      }
    });

    // Khi user disconnect, xóa khỏi danh sách online
    socket.on('disconnect', () => {
      if (socket.userId) {
        onlineUsers.delete(socket.userId);
        console.log(`User disconnected: ${socket.userId}`);
      }
    });
  });

  // Cho phép các file khác truy cập io và onlineUsers nếu cần
  module.exports.io = io;
  module.exports.onlineUsers = onlineUsers;
}
module.exports = { initSocket };