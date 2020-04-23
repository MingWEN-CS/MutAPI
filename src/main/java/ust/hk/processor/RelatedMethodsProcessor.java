package ust.hk.processor;

import java.util.ArrayList;
import java.util.List;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtVariableAccessImpl;
import spoon.support.reflect.declaration.CtClassImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import ust.hk.entity.Method;

public class RelatedMethodsProcessor extends AbstractProcessor<CtElement>{
	
	
	public String targetPrefix = "";
	public List<Method> targetMethod = new ArrayList<>();
	
	public void setTargetPrefix(String prefix) {
		targetPrefix = prefix;
	}
	
	public void setFileName(String filename) {
		for (Method method : targetMethod) {
			method.setSourceFile(filename);
		}
	}
	
	@Override
	public void process(CtElement element) {
				
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
            
            if (!methodName.equals("")) {
            	boolean hasUsed = false;
            	
            	List<CtElement> elements = element.filterChildren(new TypeFilter(CtInvocationImpl.class)).list();
            	for (CtElement me : elements) {
            		
            		CtInvocationImpl<?> methodInvocation = (CtInvocationImpl<?>) me;
            		if (methodInvocation == null) continue;
            		
            		String methodInvo = "";
            		

            		
            		List<CtElement> variables = methodInvocation.filterChildren(new TypeFilter(CtVariableAccessImpl.class)).list();
            		
            		if (methodInvocation.toString().contains("getByte"))
            			System.out.println("Invocation:\t" + methodInvocation.toString() + "\t" + variables.toString());
            		String caller = "";
            		for (CtElement ve : variables) {
            			CtVariableAccessImpl<?> variableAccess = (CtVariableAccessImpl<?>) ve;
            			if (methodInvocation.toString().contains("getByte")) {
            				System.out.println(variableAccess.getPosition().getSourceStart() + "\t" + methodInvocation.getPosition().getSourceStart() + "\t" + variableAccess.getReferencedTypes().toString());
            			}
            			if (variableAccess.getPosition().getSourceStart() == methodInvocation.getPosition().getSourceStart()
            					&& variableAccess.getReferencedTypes().size() > 0) {
            				caller = variableAccess.getType().getQualifiedName();
            			}
            		}
            		
            		if (caller.equals("")) {
            			CtElement parent = element.getParent();
                		while (!(parent instanceof CtClassImpl)) {

                			parent = parent.getParent();
                			if (parent == null) break;
                		}
                		
                		if (parent != null) {
                			CtClassImpl<?> ce = (CtClassImpl<?>) parent;
                			methodInvo = ce.getQualifiedName() + "." + methodInvocation.toString();
                		}
            			
            		} else {
            			methodInvo = caller + methodInvocation.toString().substring(methodInvocation.toString().indexOf("."));
            			
            		}
            		
            		if (methodInvo.contains("getByte")) {
            			
            			System.out.println(methodInvo + "\t" + targetPrefix);
            			System.out.println(methodInvo + "\t" + methodInvo.startsWith(targetPrefix));
            		}
            		
            		// The Spoon has a bug, which provides the wrong type of QualifiedName
            		// Instead of providing the accurate 
            		
            		if (methodInvo.contains(targetPrefix)) {
            			hasUsed = true;
            			break;
            		}
            	}
            	if (hasUsed) {
            		
            		String className = "";
            		CtElement parent = element.getParent();
            		while (!(parent instanceof CtClassImpl)) {

            			parent = parent.getParent();
            			if (parent == null) break;
            		}
            		
            		if (parent != null) {
	            		CtClassImpl classImpl = (CtClassImpl) parent;
	        			if (className.equals(""))
	        				className = classImpl.getQualifiedName();
	        			
	            		Method method2 = new Method(methodName, element.getPosition().getSourceStart(), element.getPosition().getSourceEnd(),
	            				element.getPosition().getLine(), element.getPosition().getEndLine(), paraList);
	            		method2.setClassName(className);
	            		targetMethod.add(method2);
            		}
            	}
            }
		}
	}

}
