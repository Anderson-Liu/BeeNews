package cn.peacesky.beenews.model;

import java.util.Arrays;

/**
 * 供listview使用的文章类
 */

public class ListArticleItem {

    private int type;
    private int id;
    private String[] imageUrls;
    // 图片资源不是必须的
    private String title;
    private String publishDate;
    private int readTimes;
    private String summary;

    public ListArticleItem() {

    }

    ListArticleItem(int id, String[] imageUrls, String title, String publishDate, int readTimes) {
        this.id = id;
        this.imageUrls = imageUrls;
        this.title = title;
        this.publishDate = publishDate;
        this.readTimes = readTimes;
    }

    public ListArticleItem(int id, String[] imageUrls, String title, String publishDate, int readTimes,
                           String summary) {
        this.id = id;
        this.imageUrls = imageUrls;
        this.title = title;
        this.publishDate = publishDate;
        this.readTimes = readTimes;
        this.summary = summary;
    }

    /**
     * 这个的设计不是很好
     * 存储轮播图片
     */
    public ListArticleItem(int id, String[] imageUrls, String title, int type) {
        this.id = id;
        this.imageUrls = imageUrls;
        this.title = title;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ListArticleItem{" +
                "type=" + type +
                ", aid=" + id +
                ", imageUrls=" + Arrays.toString(imageUrls) +
                ", title='" + title + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", readTimes=" + readTimes +
                ", summary='" + summary + '\'' +
                '}';
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public int getReadTimes() {
        return readTimes;
    }

    public void setReadTimes(int readTimes) {
        this.readTimes = readTimes;
    }

}