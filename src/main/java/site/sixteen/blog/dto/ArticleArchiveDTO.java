package site.sixteen.blog.dto;

import site.sixteen.blog.entity.Article;

import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public class ArticleArchiveDTO {

    private Integer year;
    private Integer month;
    private List<Article> articles;

    public ArticleArchiveDTO(Integer year, Integer month) {
        this.year = year;
        this.month = month;
    }

    public ArticleArchiveDTO() {
    }

    @Override
    public String toString() {
        return "ArticleArchiveDTO{" +
                "year=" + year +
                ", month=" + month +
                ", articles=" + articles +
                '}';
    }


    public Integer getYear() {
        return year;
    }


    public void setYear(Integer year) {
        this.year = year;
    }
    /**
     * 返回0-11
     * @return
     */
    public Integer getMonth() {
        return month;
    }
    /**
     * 0-11
     * @param month
     */
    public void setMonth(Integer month) {
        this.month = month;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
