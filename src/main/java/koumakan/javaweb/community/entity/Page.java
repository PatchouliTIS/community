package koumakan.javaweb.community.entity;

/**
 * @Package: koumakan.javaweb.community.entity
 * @Author: Alice Maetra
 * @Date: 2023/3/16 18:36
 * @Decription:
 *      封装当前页面的信息
 */
public class Page {

    // 当前页码
    private int current = 1;

    // 显示页码显示上限
    private int limit = 10;

    // 数据库中数据项总数，用于计算页面总页数
    private int rows;

    // 当前页在数据库中的起始行位置
    private int offset;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // 查询路径，用于复用分页链接
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {

        this.rows = rows;
    }


    /**
     * 获取当前页的第一条记录在起始行
     *
     * @return 返回上一页最后一条记录的序号
     */
    public int getOffset() {
        this.offset = (this.current - 1) * this.limit;
        return offset;
    }


    /**
     * 获取总页数
     * @return
     */
    public int getTotalPage() {
        return (rows / limit) + (rows % limit == 0 ? 0 : 1);
    }


    public int getBegin() {
        int begin = current - 2;
        return begin < 1 ? 1 : begin;
    }

    public int getEnd() {
        int end = current + 2;
        int total = getTotalPage();
        return end > total ? total : end;
    }
}
