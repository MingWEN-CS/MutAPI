package ust.hk.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import spoon.Launcher;
import spoon.compiler.ModelBuildingException;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.SpoonClassNotFoundException;
import spoon.support.reflect.code.CtExpressionImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import ust.hk.config.Config;
import ust.hk.entity.APIUsage;
import ust.hk.entity.Method;
import ust.hk.entity.MutatedClass;
import ust.hk.processor.CheckerDeletionProcessor;
import ust.hk.processor.CiviProcessor;
import ust.hk.processor.ConditionReplaceProcessor;
import ust.hk.processor.ControlDeletionProcessor;
import ust.hk.processor.ParameterProcessor;
import ust.hk.processor.ReachableVariableProcessor;
import ust.hk.processor.StructureDeletionProcessor;
import ust.hk.processor.SwapAPIUsagesProcessor;
import ust.hk.processor.UsageAdditionProcessor;
import ust.hk.processor.UsageDeletionProcessor;
import ust.hk.util.DataTransfer;
import ust.hk.util.FileListUnderDirectory;
import ust.hk.util.FileToLines;
import ust.hk.util.GetSourceLocation;
import ust.hk.util.Pair;
import ust.hk.util.PairNP;
import ust.hk.util.WriteLinesToFile;

public class MutateSourceCode {
	
	public String targetClient;
	public String targetAPI;
	public String location;
	public String repoLocation;
	public String dependedLibraryLocation;

	public List<APIUsage> apiInstances;
	public List<Method> instanceMethod;
	public List<List<APIUsage>> objectUsages;
	
	
	public MutateSourceCode(String targetAPI) {
		this.targetClient = Config.targetClient;
		this.targetAPI = targetAPI;
		this.location = Config.location + File.separator + Config.targetClient;
		String libFile = location + File.separator + "libs";
		File file = new File(libFile);
		if (!file.exists())
			file.mkdirs();
		this.dependedLibraryLocation = "LOCATION_TO_STORE_ALL_DEPENDED_LIBRARIES";
		this.repoLocation = "LOCATION_TO_STORE_REPOSITORY";
	}
	
	public void loadAPIInstanceAndObjectUsage() {
		String location = this.location + File.separator + targetAPI;
		String instanceFile = location + File.separator + "instances.txt";
		List<String> lines = FileToLines.fileToLines(instanceFile);
		apiInstances = new ArrayList<APIUsage>();
		objectUsages = new ArrayList<List<APIUsage>>();
		instanceMethod = new ArrayList<Method>();
		for (int i = 0; i < lines.size(); i = i + 2) {
			String usageFile = location + File.separator + "usage_" + i / 2 + ".txt";
			
			if (!new File(usageFile).exists()) continue;
			String line = lines.get(i);
			Method method = Method.toMethod(lines.get(i+1));
			instanceMethod.add(method);
			APIUsage insatnce = APIUsage.readAPIInstance(targetAPI, line);
			
			
			List<String> usageLines = FileToLines.fileToLines(usageFile);
			apiInstances.add(insatnce);
			List<APIUsage> usages = new ArrayList<APIUsage>();
			for (String line2 : usageLines) {
				APIUsage usage = APIUsage.readAPIInstance(targetAPI, line2);
				usages.add(usage);
			}
			objectUsages.add(usages);
 		}
	}
	
	public void spoonOnFile(CiviProcessor processor, String file, String classPath, String output) {
	    try {
            Launcher spoon = new Launcher();
            spoon.addInputResource(file);
            spoon.setSourceOutputDirectory("./spooned" + File.separator + output);
			if (classPath.equals(""))
            	spoon.getEnvironment().setNoClasspath(true);
            else 
                spoon.getEnvironment().setSourceClasspath(classPath.split("::"));
            spoon.getEnvironment().setCommentEnabled(false);
            spoon.addProcessor(processor);
            spoon.run();  
        }   catch(StackOverflowError e){
            System.err.println("ouch!");
        }
	}
	
	public List<CtExpression<?>> getCtExpressionList(String file) {
		Launcher launcher = new Launcher();
		launcher.addInputResource(file);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();
		

		@SuppressWarnings("unchecked")
		CtClass origClass = (CtClass) launcher.getFactory().Package().getRootPackage()
				.getElements(new TypeFilter(CtClass.class)).get(0);
	
		
		List<CtElement> elementsToBeMutated = origClass.getElements(new Filter<CtElement>() {
			
			@Override
			public boolean matches(CtElement arg0) {
				return arg0 instanceof CtExpression;
			}
		});
		
		List<CtExpression<?>> expressions = new ArrayList<CtExpression<?>>();
		for (CtElement element : elementsToBeMutated) 
			expressions.add((CtExpression<?>) element);
		
		return expressions;
	}
	
	public HashMap<Integer,	HashSet<Pair<String, String>>> getPositionReachableVariable(String file, String saveFile, String classPath) {
		HashMap<Integer, HashSet<Pair<String, String>>> positionReachableVariable = new HashMap<Integer, HashSet<Pair<String, String>>>();
		
		if (!new File(saveFile).exists()) {
			ReachableVariableProcessor rvp = new ReachableVariableProcessor();
			spoonOnFile(rvp, file, classPath, "");
			
			List<CtExpressionImpl<?>> elements = rvp.targetElements;
			List<HashSet<Pair<String, String>>> elementsVariables = rvp.elementsReachableVariables;
			List<String> saveLines = new ArrayList<String>();
			for (int i = 0; i < elements.size(); i++) {
				saveLines.add(elements.get(i).toString().replace("\n", " ").replace("\r", " ") + "\t" + elements.get(i).getPosition().getSourceStart() 
						+ "\t" + elements.get(i).getPosition().getSourceEnd() + "\t" + elementsVariables.get(i).toString());
			}
			
			WriteLinesToFile.writeLinesToFile(saveLines, saveFile);
		} 
		
		List<String> lines = FileToLines.fileToLines(saveFile);
		for (String line : lines) {
			String[] split = line.split("\t");
			if (split[0].equals("")) continue;
			
			int sourceStart = Integer.parseInt(split[1]);
			int sourceEnd = Integer.parseInt(split[2]);
			for (int i = sourceStart; i <= sourceEnd; i++) {
				if (!positionReachableVariable.containsKey(i))
					positionReachableVariable.put(i, new HashSet<Pair<String, String>>());
				positionReachableVariable.get(i).addAll(DataTransfer.turnStringToHashSetPair(split[3]));
			}
		}
		
		return positionReachableVariable;
	}
	
	public boolean hasBeenProcessed(List<MutatedClass> mutants, MutatedClass mutant) {
		for (MutatedClass mc : mutants) {
			if (mc.numMutants == mutant.numMutants) {
				if (mc.containsMutant(mutant))
					return true;
			}
		}
		return false;
	}
	
	private boolean replace(CtElement e, CtElement op) {
		try {
			if (e instanceof CtStatement  && op instanceof CtStatement) {
				((CtStatement)e).replace((CtStatement) op);			
				return true;
			}
			if (e instanceof CtExpression && op instanceof CtExpression) {
				((CtExpression)e).replace((CtExpression) op);
				return true;
			}
		} catch (Exception exc) {
			return false;
		}
		return false;
	}
	
	public void mutateSourceCode(boolean generateMutants) {
		String mutantsLoc = location + File.separator + targetAPI + File.separator + "mutants";
		String projectLoc = repoLocation + File.separator + targetClient;
		String srcLoc = GetSourceLocation.getPath(projectLoc);
		File file = new File(mutantsLoc);
		if (!file.exists())
			file.mkdirs();
		
		String ingredientsLoc = location + File.separator + targetAPI + File.separator + "ingredients";
		file = new File(ingredientsLoc);
		if (!file.exists())
			file.mkdirs();
		
		String[] tmp = Config.classPath.split(":");
		
		String dependencies = "";
		for (String tmp2 : tmp) {
			dependencies += new File(repoLocation + File.separator + targetClient + File.separator + tmp2).getAbsolutePath() + ":";
		}
		dependencies = dependencies + new File(location + File.separator + "libs/*").getAbsolutePath();

		HashSet<Method> methodProcessed = new HashSet<Method>();
		// Generate Mutants
		if (generateMutants) {
			
			for (int i = 0; i < instanceMethod.size(); i++) {
				
				if (methodProcessed.contains(instanceMethod.get(i))) continue;
				methodProcessed.add(instanceMethod.get(i));
				
				try {
					String saveLoc = mutantsLoc + File.separator + "usage_" + i;
					
					// 
					file = new File(saveLoc + File.separator + "no_test");
					if (file.exists()) {
                        System.out.println("No Tests");
                        continue;
                    }
					File mutantFile = new File(saveLoc + File.separator + "mutantsSummary.txt");
					Method targetMethod = instanceMethod.get(i);
					String targetClassLocation = targetMethod.sourceFile;
					String className = targetClassLocation.replaceAll("/", ".");				
					String prefix = srcLoc.replaceAll("/", ".").replaceAll(File.separator, ".");
					System.out.println("Instance:\t" + i + "\t" + targetClassLocation);
					className = className.substring(className.lastIndexOf(prefix) + prefix.length() + 1, className.length() - 5);
					String packageName = className.substring(0, className.lastIndexOf("."));
					String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
									
					file = new File(saveLoc);
					if (!file.exists())
						file.mkdirs();
					
					file = new File(targetClassLocation);
					
					String classPath = dependencies.replace(":", "::").replace("*", "") + "::" + new File(projectLoc + File.separator + GetSourceLocation.getPath(projectLoc)).getAbsolutePath();
					
					List<String> files = FileListUnderDirectory.getFileListUnder(location + File.separator + "libs", ".jar");
					for (String cp : files) {
						classPath += "::" + cp;
					}
					
					files = FileListUnderDirectory.getFileListUnder(this.dependedLibraryLocation, ".jar");
					for (String cp : files) {
						classPath += "::" + cp;
					}
					System.out.println(classPath);
					HashMap<Integer, HashSet<Pair<String, String>>> positionVariables = getPositionReachableVariable(targetClassLocation, ingredientsLoc + File.separator + className + ".variable", classPath);
					
					Launcher launcher = new Launcher();
					launcher.addInputResource(targetClassLocation);
					System.out.println(location + File.separator + "spoon");
					launcher.setSourceOutputDirectory(location + File.separator + "spoon");
					launcher.getEnvironment().setSourceClasspath(classPath.split("::"));
					launcher.buildModel();
					
					List<CtElement> originalClasses = launcher.getFactory().Package().getRootPackage()
							.getElements(new TypeFilter(CtClass.class));

					CtClass<?> origClass = null;
					System.out.println("analyzedClasses:\t" + originalClasses.size());
					for (CtElement element : originalClasses) {
						CtClass<?> tmpClass = (CtClass<?>) element; 
						//System.out.println("qualified Name\t" + tmpClass.getQualifiedName());
						//System.out.println(tmpClass.toString());
						if (tmpClass.getQualifiedName().equals(className)) 
							origClass = tmpClass;
					}
					
					if (origClass == null) continue;
					
	//				System.out.println(origClass.toString());
					List<CtElement> ingredientsTmp = origClass.getElements(new Filter<CtElement>() {
						@Override
						public boolean matches(CtElement arg0) {
							return arg0 instanceof CtExpression<?>;
						}
					});
					
					List<CtExpression<?>> expressions = new ArrayList<CtExpression<?>>();
					List<CtStatement> statements = new ArrayList<CtStatement>();
					
					for (CtElement element : ingredientsTmp) {
						if (element instanceof CtExpression<?>)
							expressions.add((CtExpression<?>) element);
						if (element instanceof CtStatement)
							statements.add((CtStatement) element);
					}
					
					List<MutatedClass> mutants = new ArrayList<MutatedClass>();
					MutatedClass originalMutant = new MutatedClass(launcher.getFactory().Core().clone(origClass));

					mutants.add(originalMutant);
					
					
					StructureDeletionProcessor deleteStructure = new StructureDeletionProcessor(targetMethod, objectUsages.get(i));
					SwapAPIUsagesProcessor swapUsage = new SwapAPIUsagesProcessor(targetMethod, objectUsages.get(i));
					UsageDeletionProcessor deleteUsage = new UsageDeletionProcessor(targetMethod, objectUsages.get(i));
					ConditionReplaceProcessor replaceCondition = new ConditionReplaceProcessor(targetMethod, objectUsages.get(i), positionVariables);
					ControlDeletionProcessor deleteControl = new ControlDeletionProcessor(targetMethod, objectUsages.get(i));
					CheckerDeletionProcessor deleteChecker = new CheckerDeletionProcessor(targetMethod, objectUsages.get(i));
					UsageAdditionProcessor addUsage = new UsageAdditionProcessor(targetMethod, objectUsages.get(i));
					ParameterProcessor swapParameter = new ParameterProcessor(targetMethod, objectUsages.get(i), positionVariables);
					
										
					// Start Evolution
					System.out.println(targetMethod);
					Random random = new Random(731);
					
					int iteration = 0;
					int continuesCount = 0;
					while (true) {
	                    if (mutants.size() > Config.mutantSize) break;
	                    if (iteration++ > Config.randomSize) break;
	                    int parantIndex = random.nextInt(mutants.size());
						int seed = random.nextInt(100);
						
						if (seed % 3 == 0) {
							// We favor those with a single mutation
							parantIndex = 0;
						}
						
						MutatedClass parent = mutants.get(parantIndex);
						MutatedClass children = null;
						if (parent.numMutants >= Config.mutantBound) continue;
						CtClass<?> parentClass = parent.mutant;
						
						// Randomly select a mutation operator
						int operatorIndex = random.nextInt(100);
						//operatorIndex = 1;
						
						try {
							if (operatorIndex % 8 == 0) {
								// Delete Structure and Its Contents
								
								CtClass<?> clonedClass = launcher.getFactory().Core().clone(parentClass);
								List<CtElement> elementsToBeMutated = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return deleteStructure.isToBeProcessed(arg0);
									}
								});
								//System.out.println("Deleting Structure:\t" + elementsToBeMutated.size());
								if (elementsToBeMutated.size() == 0) continue;
								CtElement toBeDeleted = elementsToBeMutated.get(random.nextInt(elementsToBeMutated.size()));
								// System.out.println(toBeDeleted.toString());
								deleteStructure.process(toBeDeleted);
								if (parentClass == null || parentClass.getParent() == null) continue;
								clonedClass.setParent(parentClass.getParent());
								children = new MutatedClass(parent);
								children.setMutants(clonedClass, new PairNP<CtElement, CtElement>(toBeDeleted, null), MutatedClass.MutantType.DELETE_STRUCTURE);
							
							} else if (operatorIndex % 8 == 1) {
								// Replace Condition of Structures
								
								CtClass<?> clonedClass = launcher.getFactory().Core().clone(parentClass);
								List<CtElement> elementsToBeMutated = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return replaceCondition.toBeReplaced(arg0);
									}
								});

								List<CtElement> isTargetAssignments = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return deleteChecker.isTargetAssignments(arg0);
									}
								});
								
								List<CtElement> conditionsToBeReplaced = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return deleteChecker.isConditionToBeProcessed(arg0, isTargetAssignments);
									}
								});
								
																
								elementsToBeMutated.addAll(conditionsToBeReplaced);
								
								if (elementsToBeMutated.size() <= 0) continue;
								CtElement element1 = elementsToBeMutated.get(random.nextInt(elementsToBeMutated.size()));

								List<CtElement> elementsToBeSelected = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return replaceCondition.toBeSelected(arg0, element1.getPosition());
									}
								});
								

								if (elementsToBeSelected.size() == 0) continue;
								
								CtElement element2 = elementsToBeSelected.get(random.nextInt(elementsToBeSelected.size()));
								if (elementsToBeSelected.size() == 1 && element1.toString().equals(element2.toString())) continue;								
								// if the selected element is the same as the element to be mutated, keep randomly selecting another element
								int iter = 0;
		                        while (element1.toString().equals(element2.toString())) {
								
		                            if (iter++ > 10) break;
		                            element2 = elementsToBeSelected.get(random.nextInt(elementsToBeSelected.size()));
								}
								
								if (element1.toString().equals(element2.toString())) continue;
								replaceCondition.process(element1, element2);
								
								if (parentClass == null || parentClass.getParent() == null) continue;
								clonedClass.setParent(parentClass.getParent());
								children = new MutatedClass(parent);
								children.setMutants(clonedClass, new PairNP<CtElement, CtElement>(element1, element2), MutatedClass.MutantType.MUTATE_CONDITION);
								
							} else if (operatorIndex % 8 == 2) {
								// Swap the order of the two usages
								CtClass<?> clonedClass = launcher.getFactory().Core().clone(parentClass);
								List<CtElement> elementsToBeMutated = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return swapUsage.isToBeProcessed(arg0);
									}
								});

								if (elementsToBeMutated.size() <= 1) continue;
								CtElement element1 = elementsToBeMutated.get(random.nextInt(elementsToBeMutated.size()));
								CtElement element2 = elementsToBeMutated.get(random.nextInt(elementsToBeMutated.size()));
								int iter = 0;
		                        while (element1.equals(element2)) { 
									
		                            if (iter++ > 10) break;
		                            element2 = elementsToBeMutated.get(random.nextInt(elementsToBeMutated.size()));
								}
		                        if (element1.equals(element2)) continue;
								CtElement swap1 = launcher.getFactory().Core().clone(element1);
								CtElement swap2 = launcher.getFactory().Core().clone(element2);
								
								boolean flag = swapUsage.process(element1, element2, swap1, swap2);
								if (!flag) continue;
								if (parentClass == null || parentClass.getParent() == null) continue;
								clonedClass.setParent(parentClass.getParent());
								children = new MutatedClass(parent);
								children.setMutants(clonedClass, new PairNP<CtElement, CtElement>(element1, element2), MutatedClass.MutantType.SWAP_USAGE);
								
							} else if (operatorIndex % 8 == 3) {
								// Delete API Usage
								CtClass<?> clonedClass = launcher.getFactory().Core().clone(parentClass);
								List<CtElement> elementsToBeMutated = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return deleteUsage.isToBeProcessed(arg0);
									}
								});

								if (elementsToBeMutated.size() == 0) continue;
								CtElement toBeDeleted = elementsToBeMutated.get(random.nextInt(elementsToBeMutated.size()));
								//System.out.println(toBeDeleted.toString());
								deleteUsage.process(toBeDeleted);
								if (parentClass == null || parentClass.getParent() == null) continue;
								clonedClass.setParent(parentClass.getParent());
								children = new MutatedClass(parent);
								children.setMutants(clonedClass, new PairNP<CtElement, CtElement>(toBeDeleted, null), MutatedClass.MutantType.DELETE_USAGE);
							} else if (operatorIndex % 8 == 4) {
								// Delete Control Statement
								CtClass<?> clonedClass = launcher.getFactory().Core().clone(parentClass);
								List<CtElement> elementsToBeMutated = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return deleteControl.isToBeProcessed(arg0);
									}
								});
								//System.out.println("Deleting Control\t" + elementsToBeMutated.size());
								if (elementsToBeMutated.size() == 0) continue;
								CtElement toBeDeleted = elementsToBeMutated.get(random.nextInt(elementsToBeMutated.size()));
								deleteControl.process(toBeDeleted);
								if (parentClass == null || parentClass.getParent() == null) continue;
								clonedClass.setParent(parentClass.getParent());
								children = new MutatedClass(parent);
								children.setMutants(clonedClass, new PairNP<CtElement, CtElement>(toBeDeleted, toBeDeleted), MutatedClass.MutantType.DELETE_CONTROL);
							} else if (operatorIndex % 8 == 5) {
								// delete the checker of receivers or parameters
								CtClass<?> clonedClass = launcher.getFactory().Core().clone(parentClass);
								List<CtElement> isTargetAssignments = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return deleteChecker.isTargetAssignments(arg0);
									}
								});
								if (isTargetAssignments.size() == 0) continue;
								
																
								List<CtElement> elementsToBeMutated = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return deleteChecker.isToBeProcessed(arg0, isTargetAssignments);
									}
								});
								if (elementsToBeMutated.size() == 0) continue;
								CtElement toBeDeleted = elementsToBeMutated.get(random.nextInt(elementsToBeMutated.size()));
								deleteChecker.process(toBeDeleted);
								if (parentClass == null || parentClass.getParent() == null) continue;
								clonedClass.setParent(parentClass.getParent());
								children = new MutatedClass(parent);
								children.setMutants(clonedClass, new PairNP<CtElement, CtElement>(toBeDeleted, toBeDeleted), MutatedClass.MutantType.DELETE_CHECKER);
							} else if (operatorIndex % 8 == 6) {
								// add usage
								CtClass<?> clonedClass = launcher.getFactory().Core().clone(parentClass);
								List<CtElement> elementsToBeMutated = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return addUsage.isToBeProcessed(arg0);
									}
								});
								if (elementsToBeMutated.size() <= 1) continue;
								CtElement element1 = elementsToBeMutated.get(random.nextInt(elementsToBeMutated.size()));
								CtElement element2 = elementsToBeMutated.get(random.nextInt(elementsToBeMutated.size()));
								CtElement swap2 = launcher.getFactory().Core().clone(element2);

								boolean flag = addUsage.process(element1, swap2);
								if (!flag) continue;
								if (parentClass == null || parentClass.getParent() == null) continue;
								clonedClass.setParent(parentClass.getParent());
								children = new MutatedClass(parent);
								children.setMutants(clonedClass, new PairNP<CtElement, CtElement>(element1, element2), MutatedClass.MutantType.ADD_USAGE);
							} else {
                                // replace variables of API call
                            	CtClass<?> clonedClass = launcher.getFactory().Core().clone(parentClass);
								List<CtElement> elementsToBeMutated = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return swapParameter.isToBeProcessed(arg0);
									}
								});
								if (elementsToBeMutated.size() <= 0) continue;
								
								CtElement element = elementsToBeMutated.get(random.nextInt(elementsToBeMutated.size()));
								CtInvocationImpl<?> invocationImpl = (CtInvocationImpl<?>) element;
								List<CtExpression<?>> arguments = invocationImpl.getArguments();
								if (arguments.size() <= 0) continue;
								CtExpression<?> selectedArgument = arguments.get(random.nextInt(arguments.size()));
								
								List<CtElement> elementsToBeSelected = clonedClass.getElements(new Filter<CtElement>() {
									@Override
									public boolean matches(CtElement arg0) {
										return swapParameter.toBeSelected(arg0, selectedArgument.getPosition(), selectedArgument.getType().toString());
									}
								});
								
								if (elementsToBeSelected.size() <= 0) continue;
								CtExpression<?> toBeReplaced = (CtExpression<?>) elementsToBeSelected.get(random.nextInt(elementsToBeSelected.size()));
								
								int iter = 0;
		                        while (selectedArgument.equals(toBeReplaced)) {
									
		                            if (iter++ > 10) break;
		                            toBeReplaced = (CtExpression<?>) elementsToBeSelected.get(random.nextInt(elementsToBeSelected.size()));
								}

								swapParameter.process(selectedArgument, toBeReplaced);
								
								if (parentClass == null || parentClass.getParent() == null) continue;
								clonedClass.setParent(parentClass.getParent());
								children = new MutatedClass(parent);
								children.setMutants(clonedClass, new PairNP<CtElement, CtElement>(selectedArgument, toBeReplaced), MutatedClass.MutantType.SWAP_PARAMETER);
                            }
						}
						catch (StackOverflowError e) {
							e.printStackTrace();
							System.out.println("StackOverflowError detected");
							continue;
						}
						catch (SpoonClassNotFoundException e) {
							System.out.println("RERUN-REQUIRED:\tClassNotFoundException");
							//e.printStackTrace();
							continue;
						} 
						catch (NullPointerException e) {
							System.out.println("Null Pointer Exception Caught");
							continue;
						}
						if (hasBeenProcessed(mutants, children)) {
                        	continuesCount++;
                        	if (continuesCount > 20) break;
                        	continue;
                        }
						continuesCount = 0;
						children.setId(mutants.size());
						mutants.add(children);
					}
					
					System.out.println("number of mutants created:\t" + mutants.size());
					String usageMutantsLoc = saveLoc + File.separator + "mutants";
					file = new File(usageMutantsLoc);
					if (!file.exists())
						file.mkdirs();
					
					List<String> mutantsInformation = new ArrayList<String>();
					
					for (int j = 0; j < mutants.size(); j++) {
						try {
							String mutantLoc = usageMutantsLoc + File.separator + j;
							file = new File(mutantLoc);
							if (!file.exists())
								file.mkdirs();
							System.out.println(j + "\t" + mutants.get(j).parentId + "\t" + mutants.get(j).numMutants + "\t" + mutants.get(j).listOfModifications.size());
							mutantsInformation.add(j + "\t" + mutants.get(j).parentId + "\t" + mutants.get(j).numMutants + "\t" + mutants.get(j).listOfModifications.size());
							for (int k = 0; k < mutants.get(j).listOfModifications.size(); k++) {
								System.out.println("===\t" + k + "\t" + mutants.get(j).mutantsType.get(k) + "\t" + mutants.get(j).listOfModifications.get(k).toString());
								mutantsInformation.add("===\t" + k + "\t" + mutants.get(j).mutantsType.get(k) + "\t" + mutants.get(j).getModification(k));
							}
							CtClass mutantClass = mutants.get(j).mutant;
							String mutantSourceLoc = mutantLoc + File.separator + simpleClassName + ".java";
						
							WriteLinesToFile.writeToFiles("package " + packageName + ";\n" + mutantClass.toString(), mutantSourceLoc);
					    } catch (Exception e) {
							e.printStackTrace();
							System.out.println("Writing mutants:\t" + i + " failed");
						} catch (StackOverflowError e) {
							continue;
						}
					}
					WriteLinesToFile.writeLinesToFile(mutantsInformation, saveLoc + File.separator + "mutantsSummary.txt");
				}
				catch (StackOverflowError e) {
					System.out.println("StackOverflow Detected");
					e.printStackTrace();
				}
				catch (NullPointerException e) {
					System.out.println("Null Pointer Exception Caught");
					continue;
					// TODO: handle exception
				}
				catch (ModelBuildingException e) {
					
					System.out.println("Model Building Exception Caught");
					e.printStackTrace();
					continue;
				}
			}
		}
	}
	
	public static void main(String[] args) {
	//	Config.readConfig(args);
		MutateSourceCode msCode = new MutateSourceCode(Config.targetAPI);
		msCode.loadAPIInstanceAndObjectUsage();
		msCode.mutateSourceCode(true);
	}
}
