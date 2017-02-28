package vn.eazy.example;

import vn.eazy.tagview.model.BaseData;

/**
 * Created by Harry on 2/13/17.
 */

public class User implements BaseData {
    private String title;
    private String avatar;
    private String desc;

    public User(String title, String avatar, String desc) {
        this.title = title;
        this.avatar = avatar;
        this.desc = desc;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getItemTitle() {
        return getTitle();
    }

    @Override
    public String getItemAvatar() {
        return getAvatar();
    }

    @Override
    public String getTag() {
        return "harryle.fit";
    }
}
