package com.ccw.demo.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CodeExecutionThread extends Thread {

	private volatile Object ret = null;
	private volatile Method thisMethod;
	private volatile Object instance;
	private volatile Object[] inputs;
	
	private boolean shouldExit = false;

	public CodeExecutionThread(Method thisMethod, Object instance, Object[] inputs) {
		this.thisMethod = thisMethod;
		this.instance = instance;
		this.inputs = inputs;
	}

	@Override
	public void run() {
		try {
			ret = this.thisMethod.invoke(this.instance, this.inputs);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}finally {
			Thread.currentThread().interrupt();
		}
	}

	public Object getRet() {
		return ret;
	}

	public void setRet(Object ret) {
		this.ret = ret;
	}

	public boolean isShouldExit() {
		return shouldExit;
	}

	public void setShouldExit(boolean shouldExit) {
		this.shouldExit = shouldExit;
	}
	
}
