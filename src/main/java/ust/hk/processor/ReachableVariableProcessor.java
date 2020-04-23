package ust.hk.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtExpressionImpl;
import spoon.support.reflect.code.CtVariableReadImpl;
import spoon.support.reflect.code.CtVariableWriteImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.support.reflect.declaration.CtParameterImpl;
import spoon.support.reflect.reference.CtVariableReferenceImpl;
import ust.hk.util.Pair;

public class ReachableVariableProcessor extends CiviProcessor {
	
	public List<CtExpressionImpl<?>> targetElements;
	public List<HashSet<Pair<String,String>>> elementsReachableVariables;
	
	public ReachableVariableProcessor() {
		targetElements = new ArrayList<CtExpressionImpl<?>>();
		elementsReachableVariables = new ArrayList<HashSet<Pair<String, String>>>();
	}
	
	
	@Override
	public void process (CtElement element) {
		if (element instanceof CtExpressionImpl<?>) {
			CtExpressionImpl<?> expr = (CtExpressionImpl<?>) element;
			
			int sourceStart = expr.getPosition().getSourceStart();
			
			// Get the enclosing method invocation parent
			CtElement parent = element.getParent();
			while (parent != null && !(parent instanceof CtMethodImpl<?>)) 
				parent = parent.getParent();
			
			if (parent == null) return;
			CtMethodImpl<?> methodImpl = (CtMethodImpl<?>) parent;
			
			List<CtElement> elements  = methodImpl.filterChildren(new TypeFilter(CtParameterImpl.class)).list();
			HashSet<Pair<String, String>> variableNames = new HashSet<Pair<String,String>>();
			for (CtElement tmp : elements) {
				CtParameterImpl<?> parameterImpl = (CtParameterImpl<?>) tmp;
//				System.out.println(parameterImpl.toString());
				if (parameterImpl.getType() == null) continue;			
				variableNames.add(new Pair<String,String>(parameterImpl.getType().toString(), parameterImpl.getSimpleName()));
			}
			
			// Retrieval variables reachable in this location
			elements = methodImpl.filterChildren(new TypeFilter(CtVariableWriteImpl.class)).list();
			for (CtElement tmp : elements) {
				if (tmp.getPosition().getSourceEnd() <= sourceStart) {
					CtVariableWriteImpl<?> write = (CtVariableWriteImpl<?>) tmp;
					if (write.getType() == null) continue;
                    if (!write.getType().toString().equals("") && !write.toString().equals(""))
                        variableNames.add(new Pair<String,String>(write.getType().toString(),write.toString()));
				}
			}
			
			elements = methodImpl.filterChildren(new TypeFilter(CtVariableReadImpl.class)).list();
			for (CtElement tmp : elements) {
				if (tmp.getPosition().getSourceEnd() <= sourceStart) {
					CtVariableReadImpl<?> read = (CtVariableReadImpl<?>) tmp;
					if (read.getType() == null) continue;
                    if (!read.getType().toString().equals("") && !read.toString().equals(""))
                        variableNames.add(new Pair<String,String>(read.getType().toString(),read.toString()));
				}
			}
			
			elements = methodImpl.filterChildren(new TypeFilter(CtVariableReferenceImpl.class)).list();
			for (CtElement tmp : elements) {
				if (tmp.getPosition().getSourceEnd() <= sourceStart) {
					CtVariableReferenceImpl<?> read = (CtVariableReferenceImpl<?>) tmp;
					if (read.getType() == null) continue;
					if (!read.getType().toString().equals("") && !read.toString().equals(""))
					variableNames.add(new Pair<String,String>(read.getType().toString(),read.toString()));
				}
			}
			
			targetElements.add(expr);
			elementsReachableVariables.add(variableNames);
			
		}
	}
}
