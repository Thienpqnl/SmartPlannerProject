const express = require("express");
const multer = require("multer");
const path = require("path");
const fs = require("fs");

const router = express.Router();

// Cấu hình nơi lưu ảnh
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        const uploadDir = "uploads";
        if (!fs.existsSync(uploadDir)) fs.mkdirSync(uploadDir);
        cb(null, uploadDir);
    },
    filename: (req, file, cb) => {
        const ext = path.extname(file.originalname);
        cb(null, Date.now() + ext);
    },
});

const upload = multer({ storage });

router.post("/", upload.single("image"), (req, res) => {
    if (!req.file) return res.status(400).send("No file uploaded.");


    const fileUrl = `http://172.17.114.181:3000/uploads/${req.file.filename}`;
    res.json({ imageUrl: fileUrl }); // ❗️Thêm dòng này để trả URL về cho client
});

module.exports = router; // ❗️Đảm bảo dòng này có mặt