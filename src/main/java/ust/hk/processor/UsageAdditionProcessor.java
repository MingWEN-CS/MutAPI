package ust.hk.processor;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtReturnImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import ust.hk.entity.APIUsage;
import ust.hk.entity.Method;

public class UsageAdditionProcessor extends CiviProcessor{
	public int sourceStart = -1;
	public int sourceEnd = -1;
	public Method targetMethod;
	public List<APIUsage> objectUsages;
	
	public UsageAdditionProcessor(int sourceStart, int sourceEnd, List<APIUsage> usages) {
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		objectUsages = new ArrayList<APIUsage>(usages);
	}
	
	public UsageAdditionProcessor(Method targetMethod, List<APIUsage> usages) {
		this.targetMethod = targetMethod;
		objectUsages = new ArrayList<APIUsage>(usages);
	}
	
	public boolean isWithinScope (CtElement candidate) {
		CtMethodImpl<?> enclosingMethod = null;
		CtElement parant = candidate.getParent();
		while (parant != null) {
			if (parant instanceof CtMethodImpl) 
				break;
			parant = parant.getParent();
		}
		
		boolean withinScope = false;
		if (parant instanceof CtMethodImpl) {
			enclosingMethod = (CtMethodImpl<?>) parant;
			//if (enclosingMethod.toString().contains("getRangeAxis")) {
				//System.out.println("ALL\t" + targetMethod.methodName + "\t" + targetMethod.paraList.toString());
				List<String> paraList = new ArrayList<>();
				for (CtParameter<?> parameter : enclosingMethod.getParameters()) {
					paraList.add(parameter.getSimpleName());
				}
				//System.out.println(enclosingMethod.getSimpleName() + "\t" + paraList);
			//}
			if (enclosingMethod.getSimpleName().equals(targetMethod.methodName) && paraList.size() == targetMethod.paraList.size()) {
				withinScope = true;
				for (int i = 0; i < paraList.size(); i++) {
					if (!paraList.get(i).equals(targetMethod.paraList.get(i))) withinScope = false;
				}
			}
		}
		return withinScope;
	}
	
	@Override
	public boolean isToBeProcessed(CtElement candidate) {
		// we only mutate those elements within the method
		boolean withinScope =  candidate instanceof CtStatement && !(candidate instanceof CtBlockImpl)
				&& isWithinScope(candidate);
		if (withinScope) {
			// check whether the statement contains the object usage
		
			for (APIUsage usage : objectUsages) {
				if (candidate.toString().contains(usage.instance))
					return true;
			}
		}
		return false;
	}
	
	public boolean process(CtElement element1, CtElement element2) {
		try {
			if (element1 instanceof CtStatement  && element2 instanceof CtStatement) {
				if (!(element2 instanceof CtReturnImpl<?>)) {
					((CtStatement) element1).insertBefore((CtStatement) element2);
					return true;
				}
				return false;
			}
		} catch (Exception exc) {
			System.out.println("Exception Caught\t");
			System.out.println(element1.toString());
			System.out.println(element2.toString());
			exc.printStackTrace();
			return false;
		}
		return false;
	}
}
