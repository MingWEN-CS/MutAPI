package ust.hk.processor;

import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtFieldAccessImpl;
import spoon.support.reflect.code.CtFieldReadImpl;
import spoon.support.reflect.code.CtFieldWriteImpl;
import spoon.support.reflect.code.CtLiteralImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;
import spoon.support.reflect.code.CtVariableAccessImpl;
import spoon.support.reflect.code.CtVariableReadImpl;
import spoon.support.reflect.code.CtVariableWriteImpl;


public class ProgramMutationProcessor extends CiviProcessor {
	
	@Override
	public void process(CtElement element) {
				
		// Deal with values
		
		if (element instanceof CtLiteralImpl) {
			System.out.println(element.getClass().toString() + "\t" + element.toString());
		}
		
		// Deal with variables
		
		if (element instanceof CtVariableReadImpl || element instanceof CtVariableWriteImpl || element instanceof CtVariableAccessImpl ||
				element instanceof CtFieldAccessImpl || element instanceof CtFieldReadImpl || element instanceof CtFieldWriteImpl) {
			System.out.println(element.getClass().toString() + "\t" + element.toString());
		}
		
		// Deal with operators
		
		if (element instanceof CtBinaryOperatorImpl || element instanceof CtUnaryOperatorImpl) {
			System.out.println(element.getClass().toString() + "\t" + element.toString());
		}
		
	}
	
}
