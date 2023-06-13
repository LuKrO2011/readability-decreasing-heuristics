package de.uni_passau.fim.se2.rdm;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.refactoring.CtRenameLocalVariableRefactoring;
import spoon.refactoring.RefactoringException;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.gui.SpoonModelTree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class provides steps for renaming a variable in a Java program.
 */
class VariableRenamer {
    String IN_DIR = "input";
    String OUT_DIR = "output";

    // Main API - main place to go into spoon
    SpoonAPI spoonUniverse;

    VariableRenamer readClasses() {
        // Command line launcher.  There are more for launching via maven, gradle...
        spoonUniverse = new Launcher();

        // Call it many times if needed - on directories and files
        spoonUniverse.addInputResource(IN_DIR);

        // Build and wait - but generally fast enough.
        spoonUniverse.buildModel();
        return this;
    }

    VariableRenamer display() {
        // Get a graphical overview, constructing is enough
        SpoonModelTree tree = new SpoonModelTree(spoonUniverse.getFactory());

        return this;
    }

    VariableRenamer renameVariable() {
        CtRenameLocalVariableRefactoring refactoring = new CtRenameLocalVariableRefactoring();

        // Get local variable named i using spoon from spoonUniverse
        CtLocalVariable<Integer> i = spoonUniverse.getModel().getRootPackage()
                .getElements(new TypeFilter<CtLocalVariable<Integer>>(CtLocalVariable.class) {
                    @Override
                    public boolean matches(CtLocalVariable<Integer> element) {
                        return super.matches(element) &&
                                element.getSimpleName().equals("processId");
                    }
                }).iterator().next();

        // Rename i to j
        refactoring.setTarget(i);
        refactoring.setNewName("UPDATED_VARIABLE_NAME");
        try {
            refactoring.refactor();
        } catch (RefactoringException e) {
            e.printStackTrace();
        }

        return this;
    }

    VariableRenamer writeTransformedClasses() {

        // And now just write the transformed classes
        Environment env = spoonUniverse.getEnvironment();

        // replace FQN with imports and short names
        env.setAutoImports(true);

        // include comments - source is for human consumption
        env.setCommentEnabled(false);

        // where to write
        spoonUniverse.setSourceOutputDirectory(OUT_DIR);

        // default printing, use a different printer for even fancier formatting
        spoonUniverse.prettyprint();

        return this;
    }

    VariableRenamer renameMethod() {
        Collection<CtType<?>> allTypes = spoonUniverse.getModel().getAllTypes();
        Set<CtMethod<?>> allMethods = new HashSet<>();

        // Get all method declarations from allTypes
        for (CtType<?> type : allTypes) {
            allMethods.addAll(type.getMethods());
        }

        CtMethod<?> firstMethod = allMethods.iterator().next();

        // Set the name of the first method to "foo"
        firstMethod.setSimpleName("UPDATED_METHOD_NAME");

        return this;
    }

}
