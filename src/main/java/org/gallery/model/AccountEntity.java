/**
 * 
 */
package org.gallery.model;

import java.util.Date;

import org.gallery.common.LogicEntityImpl;

/**
 * @author Dahaka
 * 
 */
public class AccountEntity extends LogicEntityImpl {

	private String password;
	private Date birthday;
	private String email;

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the birthday
	 */
	public Date getBirthday() {
		return birthday;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param birthday
	 *            the birthday to set
	 */
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return super.toString() + " " + password + " " + email + " "
				+ birthday.toString();
	}

}
