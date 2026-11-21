package com.gym.members;

import java.time.LocalDate;

public class Member {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private LocalDate joinedDate;
    private Long trainerId;
    public Member(){}
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public String getFullName(){
        return fullName;
    }
    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public LocalDate getDateOfBirth(){
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }
    public LocalDate getJoinedDate(){
        return joinedDate;
    }
    public void setJoinedDate(LocalDate joinedDate){
        this.joinedDate = joinedDate;
    }

    public Long getTrainerId(){
        return trainerId;
    }
    public void setTrainerId(Long trainerId){
        this.trainerId = trainerId;
    }

    @Override
    public String toString(){
        return "Member{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", joinedDate=" + joinedDate +
                ", trainerId=" + trainerId +
                '}';
    }

}