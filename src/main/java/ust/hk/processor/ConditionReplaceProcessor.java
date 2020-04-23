package ust.hk.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import spoon.reflect.code.CtExpression;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtFieldReadImpl;
import spoon.support.reflect.code.CtFieldWriteImpl;
import spoon.support.reflect.code.CtIfImpl;
import spoon.support.reflect.code.CtStatementImpl;
import spoon.support.reflect.code.CtVariableReadImpl;
import spoon.support.reflect.code.CtVariableWriteImpl;
import spoon.support.reflect.code.CtWhileImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import ust.hk.entity.APIUsage;
import ust.hk.entity.Method;
import ust.hk.util.Pair;

public class ConditionReplaceProcessor extends CiviProcessor {
	public int sourceStart = -1;
	public int sourceEnd = -1;
	public Method targetMethod;
	public List<APIUsage> objectUsages;
	public HashMap<Integer, HashSet<Pair<String, String>>> positionVariables;
	
	
	public ConditionReplaceProcessor(int sourceStart, int sourceEnd, List<APIUsage> usages, HashMap<Integer, HashSet<Pair<String, String>>> positionVariables) {
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		this.positionVariables = new HashMap<Integer, HashSet<Pair<String, String>>> (positionVariables);
		objectUsages = new ArrayList<APIUsage>(usages);
	}
	
	public ConditionReplaceProcessor(Method targetMethod, List<APIUsage> usages, HashMap<Integer, HashSet<Pair<String, String>>> positionVariables) {
		this.targetMethod = targetMethod;
		this.positionVariables = new HashMap<Integer, HashSet<Pair<String, String>>> (positionVariables);
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
	private boolean variableResolved(int sourcePos, CtExpression<?> element) {
		
		HashSet<Pair<String, String>> variables = new HashSet<Pair<String,String>>();
		
		List<CtElement> elements  = element.filterChildren(new TypeFilter(CtVariableReadImpl.class)).list();
		
		if (!positionVariables.containsKey(sourcePos)) return false;
		
		for (CtElement tmp : elements) {
			CtVariableReadImpl variable = (CtVariableReadImpl) tmp;
            if (variable.getType() == null || variable.getType().toString().equals("") || variable.toString().equals("")) continue;
            String simpleName = variable.toString();
            if (simpleName.startsWith("(") && simpleName.endsWith(")"))
            	simpleName = simpleName.substring(1, simpleName.length() - 1);
            variables.add(new Pair<String,String>(variable.getType().toString(), simpleName));
		}
		
		elements  = element.filterChildren(new TypeFilter(CtVariableWriteImpl.class)).list();
		for (CtElement tmp : elements) {
			CtVariableWriteImpl variable = (CtVariableWriteImpl) tmp;
            if (variable.getType() == null || variable.getType().toString().equals("") || variable.toString().equals("")) continue;
            String simpleName = variable.toString();
            if (simpleName.startsWith("(") && simpleName.endsWith(")"))
            	simpleName = simpleName.substring(1, simpleName.length() - 1);
            variables.add(new Pair<String,String>(variable.getType().toString(), simpleName));
		}
		
		elements = element.filterChildren(new TypeFilter(CtFieldReadImpl.class)).list();
		for (CtElement tmp : elements) {
			CtFieldReadImpl variable = (CtFieldReadImpl) tmp;
            if (variable.getType() == null || variable.getType().toString().equals("") || variable.toString().equals("")) continue;
			String simpleName = variable.toString();
            if (simpleName.startsWith("(") && simpleName.endsWith(")"))
            	simpleName = simpleName.substring(1, simpleName.length() - 1);
			if (simpleName.contains("."))
				simpleName = simpleName.substring(simpleName.lastIndexOf(".") + 1);
			variables.add(new Pair<String,String>(variable.getType().toString(), simpleName));
		}
		
		elements = element.filterChildren(new TypeFilter(CtFieldWriteImpl.class)).list();
		for (CtElement tmp : elements) {
			CtFieldWriteImpl variable = (CtFieldWriteImpl) tmp;
			String simpleName = variable.toString();
            if (variable.getType() == null || variable.getType().toString().equals("") || variable.toString().equals("")) continue;
            if (simpleName.startsWith("(") && simpleName.endsWith(")"))
            	simpleName = simpleName.substring(1, simpleName.length() - 1);
			if (simpleName.contains("."))
				simpleName = simpleName.substring(simpleName.lastIndexOf(".") + 1);
			variables.add(new Pair<String,String>(variable.getType().toString(), simpleName));
		}
		
		return positionVariables.get(sourcePos).containsAll(variables);
	}
	
	public boolean toBeReplaced(CtElement candidate) {
		
		if (isWithinScope(candidate) 
				&& candidate instanceof CtExpression<?> && ((CtExpression<?>) candidate).getType().toString().equals("boolean")) {
			CtElement parent = candidate.getParent();
			while (!(parent instanceof CtStatementImpl)) {
				parent = parent.getParent();
				if (parent == null) break;
			}
			if (parent != null && (parent instanceof CtIfImpl || parent instanceof CtWhileImpl)) {
				
				List<CtElement> enclosingStatements = parent.getElements(new Filter<CtElement>() {
					@Override
					public boolean matches(CtElement arg0) {
						return arg0 instanceof CtStatementImpl;
					}
				});
				
				for (CtElement element : enclosingStatements) {
					if (element.equals(parent)) continue;
					
					for (APIUsage usage : objectUsages) {
						if (element.toString().contains(usage.instance))
							return true;
					}
				}
			}
			
			return false;
		}
		return false;
	}
	
	public boolean toBeSelected(CtElement candidate, SourcePosition position) {
		
		if (isWithinScope(candidate)
				&& candidate instanceof CtExpression<?> && ((CtExpression<?>) candidate).getType().toString().equals("boolean")) {
			// Check whether the variables can be resolved
			if (variableResolved(position.getSourceStart(), (CtExpression<?>) candidate))
				return true;
		}
		return false;
	}
	
	public void process(CtElement element1, CtElement element2) {
		element1.replace(element2);
	}
}
