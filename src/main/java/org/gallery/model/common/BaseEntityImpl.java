package org.gallery.model.common;

import java.util.Date;
import java.util.List;


/**
 * 包含id,createTime，modifyTime
 * @author likaihua
 */
public abstract class BaseEntityImpl implements BaseEntity {

    private static final long serialVersionUID = -2673524660550133744L;

    /**
     * 主键
     */
    protected Long id;

    /**
     * 创建时间
     */
    protected Date createDate = new Date();

    /**
     * 最后一次修改时间
     */
    protected Date modifyTime = new Date();

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the modifyTime
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * @param createDate
     *            the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
/*
    public void setEntity(BaseEntity entity) {
        List<String> fields = BeanUtils.getPropertyNames(this.getClass());
        for (String fieldName: fields) {
            try {
                Object newValue = BeanUtils.forceGetProperty(entity, fieldName);
                if (newValue != null) {
                    BeanUtils.forceSetProperty(this, fieldName, newValue);
                }
            } catch (NoSuchFieldException e) {
                // ignore
            }
        }
    }
*/
    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param modifyTime
     *            the modifyTime to set
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        if (id != null) {
            return "[" + getClass().getSimpleName() + "]" + id.toString();
        } else {
            return "[" + getClass().getSimpleName() + "]"
                + Integer.valueOf(hashCode()).toString();
        }
    }

}
