package org.gallery.common;


/**
 *
 * @author likaihua
 *
 */
public class LogicEntityImpl extends BaseEntityImpl implements StatusEntity {

    /**
     * 状态
     */
    protected Status status = Status.Normal;

    /**
     * 名称
     */
    protected String name;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + " " + id + " " + status + "]"
            + name;
    }

}
