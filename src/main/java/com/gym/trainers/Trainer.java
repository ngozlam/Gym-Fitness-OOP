package com.gym.trainers;

public class Trainer {
    private Long id;
    private String fullName;
    private String specialty;
    private String phone;
    public Trainer(){};
    public Trainer( String fullName, String specialty, String phone) {
        this.fullName = fullName;
        this.specialty = specialty;
        this.phone = phone;
    }
    public Long getId(){return id;}
    public String getFullName(){return fullName;}
    public String getSpecialty(){return specialty;}
    public String getPhone(){return phone;}
    public void setId(Long id){this.id = id;}
    public void setFullName(String fullName){this.fullName = fullName;}
    public void setSpecialty(String specialty){this.specialty = specialty;}
    public void setPhone(String phone){this.phone = phone;}
    @Override
    public String toString() {
    return "Trainer{id=" + id
            + ", fullName='" + fullName + '\''
            + ", specialty='" + specialty + '\''
            + ", phone='" + phone + '\'' + "}";
}
}
