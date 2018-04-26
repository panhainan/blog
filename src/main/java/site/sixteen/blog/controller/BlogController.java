package site.sixteen.blog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;
import site.sixteen.blog.entity.Article;
import site.sixteen.blog.entity.Comment;
import site.sixteen.blog.entity.Tag;
import site.sixteen.blog.entity.User;
import site.sixteen.blog.service.BlogService;

import javax.validation.Valid;
import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
@Slf4j
@Controller
public class BlogController {

    @Autowired
    private BlogService blogService;


    @GetMapping({"/blog", "/"})
    public String index(@RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                        @RequestParam(value = "order", defaultValue = "new") String order,
                        Model model) {
        Sort sort;
        if ("hot".equals(order)) {
            sort = new Sort(Sort.Direction.DESC, "readCount", "voteCount", "commentCount");
        } else {
            order = "new";
            sort = new Sort(Sort.Direction.DESC, "createTime");
        }
        Pageable pageable = new PageRequest(page - 1, size, sort);
        Page<Article> articles = blogService.getIndexArticles(pageable);
        model.addAttribute("records", articles);
        //首页sidebar填充数据
        setSideBarData(model);

        model.addAttribute("activeTab", order);
        model.addAttribute("activeNav", "blog");
        return "index";
    }

    private void setSideBarData(Model model) {
        List<User> userList = blogService.getActiveUser(6);
        model.addAttribute("activeUsers", userList);

        List<Tag> hotTags = blogService.getHotTags();
        model.addAttribute("hotTags", hotTags);
    }


    @GetMapping("/blog/a/{id}")
    public String getArticle(@PathVariable long id, Model model) {
        Article article = blogService.getArticle(id);
        if (article == null) {
            //TODO 文章不存在 异常
        }
        model.addAttribute("article", article);
        model.addAttribute("activeNav", "blog");
        return "blog/article";
    }

    @PostMapping("/blog/a/{id}")
    @ResponseBody
    public boolean voteArticle(@PathVariable long id) {
        return blogService.voteArticle(id);
    }

    @GetMapping("/blog/t/{tagName}")
    public String getTagArticles(@PathVariable String tagName,
                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                 Model model) {
        Sort sort = new Sort(Sort.Direction.DESC, "readCount", "voteCount", "commentCount");
        Pageable pageable = new PageRequest(page - 1, size,sort);
        Page<Article> articles = blogService.getArticles(tagName, pageable);
        model.addAttribute("records", articles);

        //sidebar填充数据
        setSideBarData(model);

        model.addAttribute("activeNav", "blog");
        model.addAttribute("tagName", tagName);
        return "blog/tag-article";
    }
    @GetMapping("/blog/s")
    public String searchArticles(@RequestParam String keyword,
                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                 Model model) {
        Sort sort = new Sort(Sort.Direction.DESC, "readCount", "voteCount", "commentCount");
        Pageable pageable = new PageRequest(page - 1, size,sort);
        Page<Article> articles = blogService.searchArticles(keyword, pageable);
        model.addAttribute("records", articles);
        //sidebar填充数据
        setSideBarData(model);
        model.addAttribute("activeNav", "blog");
        model.addAttribute("keyword", keyword);
        return "blog/search";
    }

    @PostMapping("/blog/a/{articleId}/c/g")
    public String guestCommentArticle(@PathVariable long articleId, @Valid Comment comment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "blog/article";
        }
        comment.setArticleId(articleId);
        if (!blogService.guestCommentArticle(comment)) {
            return "blog/article";
        }
        return "redirect:/blog/a/" + articleId;
    }

    @PostMapping("/blog/a/{artilceId}/c/u")
    public String userCommentArticle(@PathVariable long artilceId, String content) {
        if (content == null || StringUtils.isEmpty(content.trim())) {
            return "blog/article";
        }
        Comment comment = new Comment();
        comment.setArticleId(artilceId);
        comment.setContent(content);
        if (!blogService.userCommentArticle(comment)) {
            return "blog/article";
        }
        return "redirect:/blog/a/" + artilceId;
    }


}
