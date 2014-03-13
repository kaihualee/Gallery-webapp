package org.gallery.persist.exception;

/**
 * DAO接口调用异常
 * 
 * @author likaihua
 */
public class DaoException extends RuntimeException {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

}
