package name.zhangmin.boot.model;


/**
 * @author hzzhangmin15
 * @date 2018/07/05
 */

public class User {
    private Integer userId;
    private String phone;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }
}