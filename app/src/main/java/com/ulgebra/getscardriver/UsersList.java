package com.ulgebra.getscardriver;
/**
 * Created by Vijayakumar on 12/07/2016.
 */

public class UsersList {
    private String user_id;
    private String username;
    private String user_mobile;
    private String user_email;
    private String user_licence_no;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setUser_licence_no(String user_licence_no) {
        this.user_licence_no = user_licence_no;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_licence_no() {
        return user_licence_no;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public String getUsername() {
        return username;
    }

}
