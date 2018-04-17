package com.apppubs.bean.page;

import com.apppubs.util.Utils;

/**
 * Created by siger on 2018/4/17.
 */
public class DefaultUserinfoComponent extends PageComponent {

    private String username;
    private String avatarURL;

    public DefaultUserinfoComponent(String json) {
        super(json);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof DefaultUserinfoComponent)) {
            return false;
        }
        DefaultUserinfoComponent des = (DefaultUserinfoComponent) o;
        if (!Utils.compare(username, des.getUsername())) {
            return false;
        }
        if (!Utils.compare(avatarURL, des.getAvatarURL())) {
            return false;
        }
        return true;
    }
}
