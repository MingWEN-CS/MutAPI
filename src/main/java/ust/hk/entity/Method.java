package ust.hk.entity;

import java.util.ArrayList;
import java.util.List;

import ust.hk.util.DataTransfer;


public class Method {
	
	public String packageName = "";
	public String sourceFile;
	public String className;
	public String methodName;
	public int sourceStart;
	public int sourceEnd;
	public int lineStart;
	public int lineEnd;
	public List<String> paraList;
	public List<Method> relatedMethods;
	public APIUsage targetAPIUsage;
	
	
	public static Method toMethod(String content) {
		String[] split = content.split("\t");
		List<String> paras = DataTransfer.turnStringToListString(split[1]);
		Method method =  new Method(split[0], Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]),  paras);
		method.setSourceFile(split[6]);
		return method;
	}
	
	public Method(String methodName, int sourceStart, int sourceEnd, int lineStart, int lineEnd, List<String> paraList) {
		this.className = "";
		this.methodName = methodName;
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		this.lineStart = lineStart;
		this.lineEnd = lineEnd;
		this.paraList = new ArrayList<>(paraList);
	}
	
	public Method(String className, String methodName, List<String> paraList, int sourceStart, int sourceEnd, String sourceFile) {
		this.className = className;
		
		if (this.className.contains(".")) {
			packageName = this.className.substring(0, this.className.lastIndexOf("."));
			this.className = this.className.substring(this.className.lastIndexOf(".") + 1);
		}
		
		this.methodName = methodName;
		this.paraList = new ArrayList<String>();
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		this.sourceFile = sourceFile;
	}
	
	public void setTargetAPI(APIUsage usage) {
		this.targetAPIUsage = usage;
	} 
	
	public void setRelatedMethods(List<Method> methods) {
		relatedMethods = new ArrayList<>(methods);
	}

	public void setSourceFile(String filename) {
		this.sourceFile = filename;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + sourceEnd;
		result = prime * result + sourceStart;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Method other = (Method) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (sourceEnd != other.sourceEnd)
			return false;
		if (sourceStart != other.sourceStart)
			return false;
		return true;
	}

	public String simpleString() {
		return methodName + "\t" + paraList.toString() + "\t" + sourceStart + "\t" + sourceEnd + "\t" + lineStart + "\t" + lineEnd;
	}
	
	@Override
	public String toString() {
		return packageName + "." + className + "\t" + methodName + "\t" + paraList.toString() + "\t" + sourceStart + "\t" + sourceEnd + "\t" + sourceFile;
	}
}
