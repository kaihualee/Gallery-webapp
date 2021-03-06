package org.gallery.nativemethod;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * \u914d\u8272\u7b80\u8868\u7ed3\u6784<br>
 * <i>native declaration : ImageConvertDll.h:13</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("ImageConvertDll") 
public class ccColorTheme extends StructObject {
	@Field(0) 
	public int nColorNum() {
		return this.io.getIntField(this, 0);
	}
	@Field(0) 
	public ccColorTheme nColorNum(int nColorNum) {
		this.io.setIntField(this, 0, nColorNum);
		return this;
	}
	/** C type : ccColorIngredient* */
	@Field(1) 
	public Pointer<ccColorIngredient > pColors() {
		return this.io.getPointerField(this, 1);
	}
	/** C type : ccColorIngredient* */
	@Field(1) 
	public ccColorTheme pColors(Pointer<ccColorIngredient > pColors) {
		this.io.setPointerField(this, 1, pColors);
		return this;
	}
	public ccColorTheme() {
		super();
	}
	public ccColorTheme(Pointer pointer) {
		super(pointer);
	}
}
