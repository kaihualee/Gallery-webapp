package org.gallery.common;

import java.io.Serializable;
import java.util.Date;

/**
 * @author likaihua
 */
/* 记录对象id和创建时间和序列化 */
public interface BaseEntity extends Serializable, Cloneable {

    public Date getCreateDate();

    public Long getId();

    public Date getModifyTime();

    public void setCreateDate(Date createDate);

    public void setId(Long id);

    public void setModifyTime(Date modifyTime);

}
