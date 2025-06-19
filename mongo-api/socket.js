const { Server } = require('socket.io');

// Lưu userId <-> socket.id để quản lý online
const onlineUsers = new Map();
let io = null;
function initSocket(server) {
  io = new Server(server, {
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

    // Khi user disconnect, xóa khỏi danh sách online
    socket.on('disconnect', () => {
      if (socket.userId) {
        onlineUsers.delete(socket.userId);
        console.log(`User disconnected: ${socket.userId}`);
      }
    });
  });

}
function getIO(){
    return io;
}
module.exports = {
  initSocket,
  getIO,
  onlineUsers
};