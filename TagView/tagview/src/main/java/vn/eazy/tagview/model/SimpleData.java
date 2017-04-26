package vn.eazy.tagview.model;

/**
 * Created by Harry on 2/15/17.
 */

public class SimpleData implements BaseData {
    private String title;
    private String avatar;

    public SimpleData(String title, String avatar) {
        this.title = title;
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
        return getTitle();
    }
}
