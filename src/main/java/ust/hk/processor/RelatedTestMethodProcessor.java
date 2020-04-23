package ust.hk.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtConstructorCallImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.declaration.CtClassImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import ust.hk.entity.Method;

public class RelatedTestMethodProcessor extends AbstractProcessor<CtElement> {
	
	public HashSet<Method> targetMethods;
	public List<Method> targetTestMethods;
	
	public RelatedTestMethodProcessor(HashSet<Method> targetMethods) {
		// TODO Auto-generated constructor stub
		this.targetMethods = new HashSet<Method>(targetMethods);
		targetTestMethods = new ArrayList<Method>();
		
	}
	
	public List<Method> isTargetedMethod(String method) {
		List<Method> testingMethods = new ArrayList<Method>();
		for (Method target : targetMethods) {
			if (method.startsWith(target.className + "." + target.methodName))
				testingMethods.add(target);
		}
		return testingMethods;
	}
	
	// If it calls the target class, we also collect such test
	public List<Method> isTargetedClass(String method) {
		List<Method> testingMethods = new ArrayList<Method>();
		for (Method target : targetMethods) {
			if (method.startsWith(target.className))
				testingMethods.add(target);
		}
		return testingMethods;
	}
	
	public void setFileName(String filename) {
		for (Method method : targetTestMethods) {
			method.setSourceFile(filename);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void process(CtElement element) {
		// TODO Auto-generated method stub
		if (element instanceof CtMethodImpl) {
			CtMethodImpl<?> method = (CtMethodImpl<?>) element;
			String signature  = method.getSignature();
			signature = signature.substring(signature.indexOf(" ") + 1);
            String methodName = "";
            List<String> paraList = new ArrayList<String>();
            
            if (signature.contains("(")) {
                methodName = signature.substring(0, signature.indexOf("("));
                String paras = signature.substring(signature.indexOf("(") + 1, signature.lastIndexOf(")"));
    		    String[] split = paras.split(",");
    		    
    		    for (String tmp : split) {
    			    if (tmp.trim().equals("")) continue;
    			    paraList.add(tmp.trim());
    		    }
            }
            System.out.println(methodName + "\t" + element.getPosition().getSourceStart() + "\t" + element.getPosition().getSourceEnd());
            String className = "";
    		CtElement parent = element.getParent();
    		while (parent != null && !(parent instanceof CtClassImpl)) {
    			parent = parent.getParent();
    		}
    		if (parent == null) {
    			System.out.println(element.toString());
    			return ;
    		}
    		CtClassImpl<?> classImpl = (CtClassImpl<?>) parent;
			if (className.equals(""))
				className = classImpl.getQualifiedName();
			
            if (!methodName.equals("")) {
				List<CtElement> elements = element.filterChildren(new TypeFilter(CtInvocationImpl.class)).list();
            	for (CtElement methodInvocation : elements) {
            		CtInvocationImpl<?> invocation = (CtInvocationImpl<?>) methodInvocation;		
            		String reference = invocation.getExecutable().getSignature();
            		if (!reference.contains("#")) continue;
            		String[] split = reference.split("#");
            		String caller = split[0];
            		String callee = split[1];
            		if (callee.contains("("))
            			callee = callee.substring(0, callee.indexOf("("));
            		if (!caller.equals("")) {
		        		List<Method> testingMethods = isTargetedClass(caller + "." + callee);
		        	
            			if (testingMethods.size() > 0) {
            				Method method2 = new Method(methodName, element.getPosition().getSourceStart(), element.getPosition().getSourceEnd(),
                    				element.getPosition().getLine(), element.getPosition().getEndLine(), paraList);
            				method2.setRelatedMethods(testingMethods);;
            				method2.setClassName(className);
            				targetTestMethods.add(method2);
            			}
		        	}
		        }
            	
            	elements = element.filterChildren(new TypeFilter(CtConstructorCallImpl.class)).list();
            	for (CtElement constructor : elements) {
            		
            		CtConstructorCallImpl<?> invocation = (CtConstructorCallImpl<?>) constructor;
            		String reference = invocation.getExecutable().getSignature();
            		if (!reference.contains("#")) continue;
            		String[] split = reference.split("#");
            		String caller = split[0];
            		String callee = split[1];
            		if (callee.contains("("))
            			callee = callee.substring(0, callee.indexOf("("));
	        		if (!caller.equals("")) {
		        		List<Method> testingMethods = isTargetedClass(caller + "." + callee);
		        		if (testingMethods.size() > 0) {
	        				Method method2 = new Method(methodName, element.getPosition().getSourceStart(), element.getPosition().getSourceEnd(),
	                				element.getPosition().getLine(), element.getPosition().getEndLine(), paraList);
	        				method2.setRelatedMethods(testingMethods);;
	        				method2.setClassName(className);
	        				targetTestMethods.add(method2);
	        			}
	        		}
            	}
            }
		}
	}

}
