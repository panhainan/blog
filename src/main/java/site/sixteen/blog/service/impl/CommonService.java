package site.sixteen.blog.service.impl;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import site.sixteen.blog.entity.*;
import site.sixteen.blog.repository.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public class CommonService {
    @Value("${spring.mail.username}")
    private String Sender;
    protected static final String DEFAULT_CATEGORY_NAME="无归类文章";
    @Autowired
    private JavaMailSender mailSender;
    /**
     * 验证码有效时长
     */
    protected static final long VALID_CODE_EFFECTIVE_DURATION = 1000 * 60 * 10L;
    @Autowired
    protected TagRepository tagRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected UserAuthRepository userAuthRepository;

    protected String saveTags(String tagIdStr) {
        if (!StringUtils.isEmpty(tagIdStr)) {
            StringBuilder stringBuilder = new StringBuilder(";");
            String[] tagNames = tagIdStr.split(";");
            for (String tagName : tagNames) {
                String tagNameTrim = tagName.trim();
                if (!StringUtils.isEmpty(tagNameTrim)) {
                    Tag dbTag = tagRepository.findTagByName(tagNameTrim);
                    if (dbTag == null) {
                        dbTag = tagRepository.save(new Tag(tagNameTrim,true));
                        stringBuilder.append(dbTag.getId()).append(";");
                    } else {
                        if (-1 == stringBuilder.indexOf(String.valueOf(dbTag.getId()))) {
                            stringBuilder.append(dbTag.getId()).append(";");
                        }
                    }
                }
            }
            return stringBuilder.toString();
        }
        return null;
    }

    protected void setArticleTags(Article article) {
        if (!StringUtils.isEmpty(article.getTagIdStr())) {
            String[] tagIdArrWithNull = article.getTagIdStr().split(";");
            String[] tagIdArr = replaceNull(tagIdArrWithNull);
            Tag[] tags = new Tag[tagIdArr.length];
            for (int i = 0; i < tagIdArr.length; i++) {
                Long tagId = Long.valueOf(tagIdArr[i]);
                tags[i] = tagRepository.findOne(tagId);
            }
            article.setTags(tags);
        }
    }

    private String[] replaceNull(String[] str) {
        //用StringBuffer来存放数组中的非空元素，用“;”分隔
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length; i++) {
            if ("".equals(str[i])) {
                continue;
            }
            sb.append(str[i]);
            if (i != str.length - 1) {
                sb.append(";");
            }
        }
        //用String的split方法分割，得到数组
        str = sb.toString().split(";");
        return str;
    }

    protected void setArticleListAssociatedInfo(List<Article> articles) {
        for (Article article : articles) {
            Category category = categoryRepository.findOne(article.getCategoryId());
            article.setCategoryName(category.getName());
            setArticleTags(article);
            setArticleUserInfo(article);
        }
    }

    protected void setArticleUserInfo(Article article) {
        User user = userRepository.findOne(article.getUserId());
        article.setUserNicName(user.getNickname());
        article.setUsername(user.getUsername());
        article.setUserFace(user.getFace());
    }

    protected User getCurrentUser() {
        Subject currentSubject = SecurityUtils.getSubject();
        String username = String.valueOf(currentSubject.getPrincipal());
        return getUserByUsername(username);
    }

    protected UserAuth getCurrentUserAuth() {
        Subject currentSubject = SecurityUtils.getSubject();
        String username = String.valueOf(currentSubject.getPrincipal());
        return getUserAuthByUsername(username);
    }

    protected boolean checkUsernameIsExisted(String username) {
        return userAuthRepository.findUserAuthByUsername(username) != null;
    }

    protected UserAuth getUserAuthByUsername(String username) {
        return userAuthRepository.findUserAuthByUsername(username);
    }

    protected User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    /**
     * 获取当前用户文章默认类别ID，不过不存在则创建
     * @param userId 当前用户id
     * @return 默认分类ID
     */
    protected long getDefaultCategory(long userId){
        Category category=categoryRepository.findCategoryByNameAndUserId(DEFAULT_CATEGORY_NAME,userId);
        if(category==null){
            //当前用户文章类别不存在默认类别
            category = new Category();
            category.setUserId(userId);
            category.setName(DEFAULT_CATEGORY_NAME);
            category = categoryRepository.save(category);
        }
        return category.getId();
    }

    protected void sendMailValidCode(String toEmail, String validCode, String username, Date endTime) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(Sender);
        message.setTo(toEmail);
        message.setSubject("博客邮箱验证邮件");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message.setText("您好，" + username + "，您的邮箱验证码是 " + validCode + " ,请尽快激活，有效期为 " + sdf.format(endTime) + " 。\n 如果您没有进行此操作请尽快登录您的账号修改密码，如果您没有博客账号请忽略此邮件。\n 谢谢！");
        mailSender.send(message);
    }
}

