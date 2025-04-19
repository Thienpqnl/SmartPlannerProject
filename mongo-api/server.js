const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
const multer = require("multer");
const path = require("path");
const fs = require("fs");
const app = express();
app.use(cors());
app.use(express.json());
app.use("/uploads", express.static("uploads"));
const uploadRoutes = require('./routes/upload');
app.use("/upload", uploadRoutes);

require('dotenv').config();

const jwt = require('jsonwebtoken');
app.use('/uploads', express.static('uploads'));

// Lấy giá trị từ process.env
const MONGODB_URI = process.env.MONGODB_URI;
const JWT_SECRET = process.env.JWT_SECRET;


mongoose.connect(MONGODB_URI)
          .then(() => console.log("✅ MongoDB connected"))
          .catch(err => console.error("❌ MongoDB connection error:", err));


// Định nghĩa route
const authRoutes = require('./routes/auth');
app.use('/api/auth', authRoutes);


const Event = require('./models/Event');

app.get("/events", async (req, res) => {
    const events = await Event.find();
    res.json(events);
});


app.use((req, res, next) => {
    const fullUrl = `${req.protocol}://${req.get('host')}${req.originalUrl}`;
    console.log("Đang truy cập:", fullUrl);
    next();
});


app.post("/events", async (req, res) => {
    try {
        console.log("Event nhận được:", req.body);



        const newEvent = new Event(req.body);
        await newEvent.save();
        res.json(newEvent);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

app.listen(3000, '0.0.0.0', () => {
    console.log("Server running on port 3000");
});

