package org.gallery.model.common;

/**
 * @author likaihua
 */
public class PageBean {

    private int firstResult = 0;

    private int maxResults = 10;

    /**
     * the number of page
     */
    private int pageNum = 1;

    private int pageUp = 0;

    private int pageDown = 0;

    /**
     * total count of beans
     */
    private int resultsCount = 0;

    /**
     * total count of pages
     */
    private int pageCount = 0;

    /**
     * @return
     */
    public int getFirstResult() {
        if (firstResult == 0) {
            firstResult = (pageNum - 1) * maxResults;
        }
        return firstResult;
    }

    /**
     * @return
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * @param i
     */
    public void setFirstResult(int i) {
        firstResult = i;
    }

    /**
     * @param i
     */
    public void setMaxResults(int i) {
        maxResults = i;
    }

    /**
     * @return
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * @param i
     */
    public void setPageNum(int i) {
        pageNum = i;
    }

    /**
     * @return
     */
    public int getPageDown() {
        if (pageDown == 0) {
            if ((getPageNum() + 1) > getPageCount()) {
                pageDown = getPageCount();
            } else {
                pageDown = getPageNum() + 1;
            }
        }
        return pageDown;
    }

    /**
     * @return
     */
    public int getPageUp() {
        if (pageUp == 0) {
            if ((getPageNum() - 1) < 1) {
                pageUp = 1;
            } else {
                pageUp = getPageNum() - 1;
            }
        }
        return pageUp;
    }

    /**
     * @param i
     */
    public void setPageDown(int i) {
        pageDown = i;
    }

    /**
     * @param i
     */
    public void setPageUp(int i) {
        pageUp = i;
    }

    /**
     * @return
     */
    public int getResultsCount() {
        return resultsCount;
    }

    /**
     * @param i
     */
    public void setResultsCount(int i) {
        resultsCount = i;
    }

    /**
     * @return
     */
    public int getPageCount() {
        if (pageCount == 0) {
            if (resultsCount % maxResults == 0) {
                pageCount = resultsCount / maxResults;
            } else {
                pageCount = (resultsCount / maxResults) + 1;
            }
        }
        return pageCount;
    }

    /**
     * @param i
     */
    public void setPageCount(int i) {
        pageCount = i;
    }

}
