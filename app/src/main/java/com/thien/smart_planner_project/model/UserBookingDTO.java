package com.thien.smart_planner_project.model;


public class UserBookingDTO {
    private User user;
    private String bookingDate;

    public UserBookingDTO() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
}
