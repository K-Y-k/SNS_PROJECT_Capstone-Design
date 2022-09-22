package com.example.sns_project;

public class MemberInfo {      // 회원정보를 초기화 해주는 클래스 생성
    private String userName;
    private String userAddress;

    public MemberInfo() {
    }

    public MemberInfo(String userName, String userAddress) {   // 생성자 메서드
        this.userName = userName;
        this.userAddress = userAddress;
    }

    public String getUserName() {               // 맞춤 객체 가져올 겟터 메서드
        return this.userName;
    }       // 닉네임

    public void setUserName(String userName) {   // 맞춤 객체 셋터 메서드
        this.userName = userName;
    }

    public String getUserAddress() {
        return this.userAddress;
    } // 농장 주소

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    @Override
    public String toString() {
        return "MemberInfo{" +
                "userName='" + userName + '\'' +
                ", userAddress='" + userAddress + '\'' +
                '}';
    }
}
