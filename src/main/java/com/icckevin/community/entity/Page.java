package com.icckevin.community.entity;

import java.io.Serializable;

/**
 * @description: 处理分页
 * @author: iccKevin
 * @create: 2020-05-15 09:40
 **/
public class Page implements Serializable {
    // 总记录数
    private int rows;
    // 每页显示条数
    private int limit = 10;
    // 当前页码
    private int current = 1;
    // 当前页面的地址
    private String path;

    /**
     * 计算总页数
     * @return
     */
    public int getTotalPage() {
        return rows % limit == 0 ? rows / limit : rows / limit + 1;
    }

    /**
     * 计算起始记录
      * @return
     */
    public int getStartRow(){
        return current * limit - limit;
    }

    /**
     * 显示的起始页码
     * @return
     */
    public int getFrom(){
        return Math.max(current - 2, 1);
    }

    /**
     * 显示的结束页码
     * @return
     */
    public int getTo(){
        return Math.min(current + 2, this.getTotalPage());
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Page{" +
                "rows=" + rows +
                ", limit=" + limit +
                ", current=" + current +
                ", path='" + path + '\'' +
                '}';
    }


}