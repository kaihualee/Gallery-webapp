/**
 * @(#)ImageDaoImpl.java, 2013年12月14日. 
 * 
 * Copyright 2013 ZJU, Inc. All rights reserved.
 *
 */
package org.gallery.persist.impl;

import java.util.List;

import org.gallery.model.ImageEntity;
import org.gallery.model.common.Status;
import org.gallery.persist.ImageDao;
import org.gallery.persist.common.LogicDaoImpl;
import org.gallery.persist.utils.PageBean;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * @author likaihua
 */
@Repository
public class ImageDaoImpl extends LogicDaoImpl<ImageEntity> implements ImageDao {

    private final static float range = 0.05f;

}
