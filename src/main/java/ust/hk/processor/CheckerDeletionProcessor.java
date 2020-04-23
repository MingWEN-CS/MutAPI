package ust.hk.processor;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.support.reflect.code.CtAssignmentImpl;
import spoon.support.reflect.code.CtIfImpl;
import spoon.support.reflect.code.CtLocalVariableImpl;
import spoon.support.reflect.code.CtStatementImpl;
import spoon.support.reflect.code.CtWhileImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import ust.hk.entity.APIUsage;
import ust.hk.entity.Method;

public class CheckerDeletionProcessor extends CiviProcessor {
	
	public int sourceStart = -1;
	public int sourceEnd = -1;
	
	public List<APIUsage> objectUsages;
	public Method targetMethod;
	
	public CheckerDeletionProcessor(int sourceStart, int sourceEnd, List<APIUsage> usages) {
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		
		objectUsages = new ArrayList<APIUsage>(usages);
	}
	
	public CheckerDeletionProcessor(Method targetMethod, List<APIUsage> usages) {
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
	
	public boolean isTargetAssignments(CtElement candidate) {
		
				
//		if (candidate.toString().contains("this.plot.getDataset"))
//			System.out.println(candidate.toString() + "\t" + candidate.getClass().toString() + "\t" + candidate.getPosition().getSourceStart() + "\t" + candidate.getPosition().getSourceEnd());
//		boolean withinScope = candidate.getPosition().getSourceStart() >= sourceStart 
//				&& candidate.getPosition().getSourceEnd() <= sourceEnd;
		boolean withinScope = isWithinScope(candidate);
		if (withinScope) {			
			
			if (candidate instanceof CtAssignmentImpl) {
				
				CtAssignmentImpl<?,?> assignmentImpl = (CtAssignmentImpl<?, ?>) candidate;
				//System.out.println(assignmentImpl.toString());
				if (assignmentImpl == null || assignmentImpl.getAssignment() == null) return false;
				for (APIUsage usage : objectUsages) {		
					if (assignmentImpl.getAssignment().toString().contains(usage.instance))
						return true;
				}	
			} else if (candidate instanceof CtLocalVariableImpl) {
				CtLocalVariableImpl<?> localVariableImpl = (CtLocalVariableImpl<?>) candidate;
//				if (candidate.toString().contains("this.plot.getDataset"))
//					System.out.println(localVariableImpl.getAssignment().toString() + "\t" + candidate.getClass().getName());

				if (localVariableImpl == null || localVariableImpl.getAssignment() == null) return false;
				for (APIUsage usage : objectUsages) {		
//					System.out.println(localVariableImpl.getAssignment().toString() + "\t" + usage.instance + "\t" + localVariableImpl.getAssignment().toString().contains(usage.instance));
					if (localVariableImpl.getAssignment().toString().contains(usage.instance))
						return true;
				}	
			}
		}
		return false;
	}
	
	
	public boolean isToBeProcessed(CtElement candidate, List<CtElement> elements) {
		boolean withinScope =  candidate instanceof CtIfImpl && isWithinScope(candidate);
		if (withinScope) {
			CtIfImpl ifStatement = (CtIfImpl) candidate;
			String conditionExpression = ifStatement.getCondition().toString().trim();
			String variableExpression = "";
			
			for (CtElement element : elements) {
				if (element instanceof CtLocalVariableImpl) {
					CtLocalVariableImpl<?> localVariableImpl = (CtLocalVariableImpl<?>) element;
					variableExpression = localVariableImpl.getSimpleName();
					//System.out.println(localVariableImpl.getSimpleName());
				} else if (element instanceof CtAssignmentImpl) {
					variableExpression = ((CtAssignmentImpl<?,?>) element).getAssigned().toString().trim();
				
				}
				if (conditionExpression.contains(variableExpression))
					return true;
			}
		}
		return false;
	}
	
	public boolean isConditionToBeProcessed(CtElement candidate, List<CtElement> elements) {
		boolean withinScope =  candidate instanceof CtExpression<?> && ((CtExpression<?>) candidate).getType().toString().equals("boolean") && isWithinScope(candidate);
		if (withinScope) {
			
			CtElement parent = candidate.getParent();
			while (!(parent instanceof CtStatementImpl)) {
				parent = parent.getParent();
				if (parent == null) break;
			}
			if (parent != null && (parent instanceof CtIfImpl || parent instanceof CtWhileImpl)) {
				String conditionExpression = candidate.toString();
				String variableExpression = "";
				
				for (CtElement element : elements) {
					if (element instanceof CtLocalVariableImpl) {
						CtLocalVariableImpl<?> localVariableImpl = (CtLocalVariableImpl<?>) element;
						variableExpression = localVariableImpl.getSimpleName();
						//System.out.println(localVariableImpl.getSimpleName());
					} else if (element instanceof CtAssignmentImpl) {
						variableExpression = ((CtAssignmentImpl<?,?>) element).getAssigned().toString().trim();
					}
					if (conditionExpression.contains(variableExpression))
						return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void process(CtElement element) {
		element.delete();
	}
	
}
