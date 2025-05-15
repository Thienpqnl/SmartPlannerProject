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



const Event = require('./models/Event');
const User = require('./models/User');
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
// POST /api/users/register-or-update
app.post('/api/users/register-or-update', async (req, res) => {
  const { userId, email, name, role, location,latitude, longitude } = req.body;

  if (!userId || !email) return res.status(400).json({ message: 'Missing fields' });

  try {
    let user = await User.findOne({ userId });

    if (user) {
      // update
      user.email = email;
      user.name = name;
      user.role = role;
      user.location = location;
      user.latitude = latitude;
      user.longitude = longitude;
      await user.save();
    } else {
      // create
      user = new User({ userId, email, name, role,  location,latitude, longitude });
      await user.save();
    }

    res.status(200).json({ message: 'User saved', user });
  } catch (err) {
    res.status(500).json({ message: 'Server error', error: err.message });
  }
});
app.get('/api/users/:uid', async (req, res) => {
  try {
    const user = await User.findOne({ userId: req.params.uid });
    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }
    res.json(user);
  } catch (err) {
    res.status(500).json({ message: 'Server error', error: err.message });
  }
});

app.listen(3000, '0.0.0.0', () => {
    console.log("Server running on port 3000");
});

