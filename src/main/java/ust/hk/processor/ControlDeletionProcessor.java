package ust.hk.processor;

import java.util.ArrayList;
import java.util.List;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.Filter;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtIfImpl;
import spoon.support.reflect.code.CtReturnImpl;
import spoon.support.reflect.code.CtStatementImpl;
import spoon.support.reflect.code.CtTryImpl;
import spoon.support.reflect.code.CtWhileImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import ust.hk.entity.APIUsage;
import ust.hk.entity.Method;

public class ControlDeletionProcessor extends CiviProcessor {
	public int sourceStart = -1;
	public int sourceEnd = -1;
	public Method targetMethod;
	public List<APIUsage> objectUsages;
	
	
	public ControlDeletionProcessor(int sourceStart, int sourceEnd, List<APIUsage> usages) {
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		objectUsages = new ArrayList<APIUsage>(usages);
	}

	public ControlDeletionProcessor(Method targetMethod, List<APIUsage> usages) {
		this.targetMethod = targetMethod;
		objectUsages = new ArrayList<APIUsage>(usages);
	}
	
	public boolean isWithinScope(CtElement candidate) {
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
	
	public boolean isToBeProcessed(CtElement candidate) {
		boolean withinScope =  candidate instanceof CtStatement && isWithinScope(candidate);
		if (withinScope) {
			
			// check whether the statement contains the object usage
			if (candidate instanceof CtIfImpl || candidate instanceof CtWhileImpl || candidate instanceof CtTryImpl) {
				
				for (APIUsage usage : objectUsages) {
					
					if (candidate.toString().contains(usage.instance))
						return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void process(CtElement element) {
		List<CtElement> blockStatements = element.getElements(new Filter<CtElement>() {
			@Override
			public boolean matches(CtElement arg0) {
				return arg0 instanceof CtStatementImpl && !arg0.equals(element) && !(arg0 instanceof CtBlockImpl);
			}
		});
		
		if (blockStatements.size() > 0) {

			List<CtElement> uniqueBlocks = new ArrayList<CtElement>();
			for (int i = 0; i < blockStatements.size(); i++) {
				boolean flag = true;
				for (int j = 0; j < blockStatements.size(); j++) {
					if (blockStatements.get(i).getPosition().getSourceStart() > blockStatements.get(j).getPosition().getSourceStart() &&
							blockStatements.get(i).getPosition().getSourceEnd() < blockStatements.get(j).getPosition().getSourceEnd()) {
						flag = false;
						break;
					}
				}
				if (flag)
					uniqueBlocks.add(blockStatements.get(i));
			}
			boolean isPreviousReturn = false;
			for (CtElement blockStatement : uniqueBlocks) {
				if (element != null) {
					if (!isPreviousReturn)
						((CtStatement) element).insertBefore((CtStatement) blockStatement);
					if (blockStatement instanceof CtReturnImpl)
						isPreviousReturn = true;
					else isPreviousReturn = false;
				}
			}
			element.delete();
		}
	} 
}
