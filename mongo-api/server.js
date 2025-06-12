const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
const path = require("path");
const fs = require("fs");
require("dotenv").config();

const app = express();
app.use(cors());
app.use(express.json());
app.use("/uploads", express.static("uploads"));

// Đọc biến môi trường
const MONGODB_URI = process.env.MONGODB_URI;
const JWT_SECRET = process.env.JWT_SECRET;

// Kết nối MongoDB
mongoose.connect(MONGODB_URI)
    .then(() => console.log("✅ MongoDB connected"))
    .catch(err => console.error("❌ MongoDB connection error:", err));

// Logging middleware
app.use((req, res, next) => {
    const fullUrl = `${req.protocol}://${req.get('host')}${req.originalUrl}`;
    console.log("Đang truy cập:", fullUrl);
    next();
});

// Routers
const uploadRoutes = require('./routes/upload');
const eventRoutes = require('./routes/event');
const userRoutes = require('./routes/user');
const bookingRoutes = require('./routes/booking');
const attendeeRoutes = require('./routes/attendee');
const organizerRoutes = require('./routes/organizer');  
const conservationRoutes = require('./routes/conservation');

app.use("/organizers", organizerRoutes);
app.use("/bookings", bookingRoutes);
app.use("/upload", uploadRoutes);
app.use("/events", eventRoutes);
app.use("/api/users", userRoutes);
app.use("/attendees", attendeeRoutes)
app.use("/conservation", conservationRoutes)

// Khởi động server
app.listen(3000, '0.0.0.0', () => {
    console.log("🚀 Server running on port 3000");
});
