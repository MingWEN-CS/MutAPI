package ust.hk.entity;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import ust.hk.util.PairNP;

public class MutatedClass {
	
	public static enum MutantType {
		/**
		 * The line of code was deleted from the old revision.
		 */
		OPERATOR, 
		
		/**
		 * The line of code was added in the new revision.
		 */
		EXPRESSION,
		
		DELETE_STRUCTURE,
		SWAP_USAGE,
		DELETE_USAGE,
		MUTATE_CONDITION,
		DELETE_CONTROL,
		DELETE_CHECKER,
		SWAP_PARAMETER,
		ADD_USAGE,
		SWAP_API
	}
	
	public MutatedClass parent;
	public int id;
	public int parentId;
	
	public CtClass<?> mutant;
	
	public int numMutants;
	public List<MutantType> mutantsType;
	public List<PairNP<CtElement, CtElement>> listOfModifications;
	
	public MutatedClass(CtClass<?> mutant) {
		this.mutant = mutant;
		this.parent = null;
		numMutants = 0;
		mutantsType = new ArrayList<MutantType>(); 
		listOfModifications = new ArrayList<PairNP<CtElement, CtElement>>();
	}
	
	public MutatedClass(MutatedClass parent) {
		this.parent = parent;
		this.parentId = parent.id;
		numMutants = parent.numMutants;
		mutantsType = new ArrayList<MutantType>(parent.mutantsType); 
		listOfModifications = new ArrayList<PairNP<CtElement, CtElement>>(parent.listOfModifications);
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setMutants(CtClass<?> mutant, PairNP<CtElement, CtElement> modification, MutantType mutantType) {
		this.mutant = mutant;
		numMutants++;
		listOfModifications.add(modification);
		mutantsType.add(mutantType);
	}
	
	public boolean containsMutant(MutatedClass mutant) {
		if (listOfModifications.size() != mutant.listOfModifications.size()) return false;
		
		for (int i = 0; i < mutant.listOfModifications.size(); i++) {
			
			// Contains
			boolean contains = false;
			for (int j = 0; j < listOfModifications.size(); j++) {
				try {
					if (mutant.mutantsType.get(i).equals(mutantsType.get(j)) && mutant.listOfModifications.get(i).equals(listOfModifications.get(j))) {
						contains = true;
						break;
					}
				} catch (StackOverflowError e) {
					return true;
					// TODO: handle exception
				}
			}
			if (!contains) return false;
		}
		return true;
	}
	
	public boolean containsAllModifications(List<PairNP<CtElement, CtElement>> modifications) {
		if (listOfModifications.size() != modifications.size()) return false;
		for (PairNP<CtElement, CtElement> modification : modifications) {
			for (PairNP<CtElement, CtElement> modification2 : listOfModifications) {
				System.out.println("Error");
				try {
				System.out.println(modification.toString());
				System.out.println(modification2.toString());
				if (!modification.equals(modification2))
					return false;
				} catch (StackOverflowError e) {
					System.out.println("===StackOverflow Caught");
					return true;
				}
			}
//			if (!listOfModifications.contains(modification))
//				return false;
		}
		return true;
	}
	
	public String getModification(int i) {
		String string = listOfModifications.get(i).toString() + "\t" + listOfModifications.get(i).getKey().getPosition().getLine() + ":" + listOfModifications.get(i).getKey().getPosition().getEndLine();
		if (listOfModifications.get(i).getValue() != null) {
			string += ":" + listOfModifications.get(i).getValue().getPosition().getLine() + ":" + listOfModifications.get(i).getValue().getPosition().getEndLine();
		}
		return string;
	}
}
