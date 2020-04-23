package ust.hk.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtElement;

/** a trivial mutation operator that transforms all binary operators to minus ("-") */
public class BinaryOperatorMutator extends CiviProcessor {
	
	
	public static List<BinaryOperatorKind> relationOperator = new ArrayList<BinaryOperatorKind>();
	public static List<BinaryOperatorKind> logicOperator = new ArrayList<BinaryOperatorKind>();
	public static List<BinaryOperatorKind> mathOperator = new ArrayList<BinaryOperatorKind>();
	public int sourceStart = 0;
	public int sourceEnd = 0;
	
	
	public BinaryOperatorMutator(int sourceStart, int sourceEnd) {
		this.sourceEnd = sourceEnd;
		this.sourceStart = sourceStart;
	}
	
	static {
		relationOperator.add(BinaryOperatorKind.EQ);
		relationOperator.add(BinaryOperatorKind.GE);
		relationOperator.add(BinaryOperatorKind.GT);
		relationOperator.add(BinaryOperatorKind.LE);
		relationOperator.add(BinaryOperatorKind.LT);
		relationOperator.add(BinaryOperatorKind.NE);
		
		logicOperator.add(BinaryOperatorKind.AND);
		logicOperator.add(BinaryOperatorKind.OR);
		
		mathOperator.add(BinaryOperatorKind.DIV);
		mathOperator.add(BinaryOperatorKind.MINUS);
		mathOperator.add(BinaryOperatorKind.MOD);
		mathOperator.add(BinaryOperatorKind.MUL);
		mathOperator.add(BinaryOperatorKind.PLUS);
	}
	
	public boolean isRelational(BinaryOperatorKind kind) {
		return relationOperator.contains(kind);
	}
	
	public boolean isLogical(BinaryOperatorKind kind) {
		return logicOperator.contains(kind);
	}
	
	public boolean isMath(BinaryOperatorKind kind) {
		return mathOperator.contains(kind);
	}
	
	public BinaryOperatorKind getRelationalRandomly() {
		Random random = new Random();
		return relationOperator.get(random.nextInt(relationOperator.size()));
	}
	
	public BinaryOperatorKind getLogicRandomly() {
		Random random = new Random();
		return logicOperator.get(random.nextInt(logicOperator.size()));
	}
	
	public BinaryOperatorKind getMathRandomly() {
		Random random = new Random();
		return mathOperator.get(random.nextInt(mathOperator.size()));
	}
	
	
	@Override
	public boolean isToBeProcessed(CtElement candidate) {
		
		// we only mutate those elements within the method
		return candidate instanceof CtBinaryOperator 
				&& candidate.getPosition().getSourceStart() >= sourceStart 
				&& candidate.getPosition().getSourceEnd() <= sourceEnd;
	}

	@Override
	public void process(CtElement candidate) {
		if (!(candidate instanceof CtBinaryOperator)) {
			return;
		}
		CtBinaryOperator<?> op = (CtBinaryOperator<?>) candidate;
		
		if (isRelational(op.getKind())) {
			
			BinaryOperatorKind bok = getRelationalRandomly();
			while (bok.equals(op.getKind()))
				bok = getRelationalRandomly();
			if (op.getKind().equals(BinaryOperatorKind.EQ))
				bok = BinaryOperatorKind.NE;
			if (op.getKind().equals(BinaryOperatorKind.NE))
				bok = BinaryOperatorKind.EQ;
			op.setKind(bok);
			
		} else if (isLogical(op.getKind())) {
			
			BinaryOperatorKind bok = getLogicRandomly();
			while (bok.equals(op.getKind()))
				bok = getLogicRandomly();
			op.setKind(bok);
			
		} else if (isMath(op.getKind())) {
			
			BinaryOperatorKind bok = getMathRandomly();
			while (bok.equals(op.getKind()))
				bok = getMathRandomly();
			op.setKind(bok);
		}
	}
}
