package site.sixteen.blog.enums;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public enum IdentityType {
    PHONE(1),
    EMAIL(2),
    USERNAME(3),
    QQ(4),
    WECHAT(5),
    SINA(6);
    private final int number;
    IdentityType(int i) {
        this.number= i;
    }
    public int value() {
        return number;
    }
}
