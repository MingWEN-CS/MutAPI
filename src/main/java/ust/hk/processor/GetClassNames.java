package ust.hk.civi.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import spoon.Launcher;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtClassImpl;
import ust.hk.civi.util.Pair;

public class GetClassNames extends CiviProcessor {
	
	
	public static HashSet<String> getClassNames(String sourceFile, String output) {
		 Launcher spoon = new Launcher();
         spoon.addInputResource(sourceFile);
         spoon.setSourceOutputDirectory("./spooned" + File.separator + output);
         spoon.getEnvironment().setNoClasspath(true);
//       spoon.getEnvironment().setCommentEnabled(true);
         ClassDeclrationProcessor processor = new ClassDeclrationProcessor();
         spoon.addProcessor(processor);
	     spoon.run();
	     
	     return processor.classNames;
	}
	
	public static List<Pair<String, String>> getClassSuperClasses(List<String> sourceFiles, String classPath, String output) {
		Launcher spoon = new Launcher();
		for (String file : sourceFiles) {
			spoon.addInputResource(file);
		}
		spoon.setSourceOutputDirectory("./spooned" + File.separator + output);
		spoon.getEnvironment().setNoClasspath(true);
	//	spoon.getEnvironment().setSourceClasspath(classPath.split(" "));
		ClassDeclrationProcessor processor = new ClassDeclrationProcessor();
		spoon.addProcessor(processor);
		spoon.run();
		return processor.classInheritance;
		
	}
}

class ClassDeclrationProcessor extends CiviProcessor {
	
	public HashSet<String> classNames = new HashSet<String>();
	public List<Pair<String,String>> classInheritance = new ArrayList<Pair<String, String>>();
	
	@Override
	public void process(CtElement element) {
		// TODO Auto-generated method stub
		if (element instanceof CtClassImpl) {
			CtClassImpl<?> classImpl = (CtClassImpl<?>) element;
			String simpleName = classImpl.getQualifiedName();
			classNames.add(classImpl.getSimpleName());
			
			if (classImpl.getSuperclass() != null) 
				classInheritance.add(new Pair<String, String>(simpleName, classImpl.getSuperclass().getQualifiedName()));
			if (classImpl.getSuperInterfaces() != null) {
				Set<CtTypeReference<?>> inheritants = classImpl.getSuperInterfaces();
				for (CtTypeReference<?> inheritant : inheritants)
					classInheritance.add(new Pair<String, String>(simpleName, inheritant.getQualifiedName()));
			}
		}
	}
}
