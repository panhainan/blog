package site.sixteen.blog.enums;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public enum GenerateValidCodeResult {
    SUCCESS(1,"操作成功"),
    FAILED_EMAIL_NULL(0,"操作失败，邮箱号不能为空"),
    FAILED_EMAIL_IS_USED(-1,"操作失败，邮箱已经被使用"),
    FAILED_EMAIL_NOT_CHANGE(-2,"操作失败，邮箱已经绑定此账户"),
    FAILED_REPEAT_SEND(-3,"操作失败，不要重复发送")
    ;
    private final int number;
    private final String msg;
    GenerateValidCodeResult(int i,String msg) {
        this.number=i;
        this.msg=msg;
    }

    public int value() {
        return number;
    }

    public String msg(){
        return msg;
    }
}
