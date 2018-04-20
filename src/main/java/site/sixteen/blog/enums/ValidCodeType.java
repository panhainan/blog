package site.sixteen.blog.enums;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public enum ValidCodeType {
    EMAIL(1),
    MOBILE(2);

    private final int number;
    ValidCodeType(int i) {
        this.number= i;
    }

    public int value() {
        return number;
    }
}
