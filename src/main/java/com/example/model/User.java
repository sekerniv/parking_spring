package com.example.model;

public class User {
    private String id;
    private String email;
    private String homeAddress;
    private Double homeLat;
    private Double homeLng;
    private boolean isResident;

    public User() {}

    public User(String id, String email, String homeAddress, Double homeLat, Double homeLng, boolean isResident) {
        this.id = id;
        this.email = email;
        this.homeAddress = homeAddress;
        this.homeLat = homeLat;
        this.homeLng = homeLng;
        this.isResident = isResident;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Double getHomeLat() {
        return homeLat;
    }

    public void setHomeLat(Double homeLat) {
        this.homeLat = homeLat;
    }

    public Double getHomeLng() {
        return homeLng;
    }

    public void setHomeLng(Double homeLng) {
        this.homeLng = homeLng;
    }

    public boolean isResident() {
        return isResident;
    }

    public void setResident(boolean resident) {
        isResident = resident;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private String id;
        private String email;
        private String homeAddress;
        private Double homeLat;
        private Double homeLng;
        private boolean isResident;

        public UserBuilder id(String id) {
            this.id = id;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder homeAddress(String homeAddress) {
            this.homeAddress = homeAddress;
            return this;
        }

        public UserBuilder homeLat(Double homeLat) {
            this.homeLat = homeLat;
            return this;
        }

        public UserBuilder homeLng(Double homeLng) {
            this.homeLng = homeLng;
            return this;
        }

        public UserBuilder isResident(boolean isResident) {
            this.isResident = isResident;
            return this;
        }

        public User build() {
            return new User(id, email, homeAddress, homeLat, homeLng, isResident);
        }
    }
}
