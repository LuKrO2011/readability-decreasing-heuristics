package de.uni_passau.fim.se2.rdh.refactorings.experimental.imports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.experimental.CtUnresolvedImport;
import spoon.refactoring.CtRefactoring;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtImportImpl;

/**
 * This class implements a refactoring that replaces an import with a star import.
 */
public class CtImportRefactoring implements CtRefactoring {

    /**
     * The target is a method invocation that should be inlined.
     */
    private CtImport target;

    private static final Logger LOG = LoggerFactory.getLogger(CtImportRefactoring.class);

    /**
     * This method performs the refactoring.
     */
    @Override
    public void refactor() {
        replaceWithStarImport();
    }

    /**
     * Replaces the target import with a star import.
     * <p>
     * If the target import is an unresolved import, the unresolved reference is replaced with a star import by
     * string manipulation. If the target import is a resolved import, the reference is resolved and the import is
     * replaced with a star import.
     */
    private void replaceWithStarImport() {

        // Clone the target import
        CtImport newImport = target.clone();

        if (newImport instanceof CtUnresolvedImport unresolvedImport) {

            // Replace the unresolved reference with a star import by string manipulation
            String unresolvedRef = unresolvedImport.getUnresolvedReference();
            String packageReference = unresolvedRef.substring(0, unresolvedRef.lastIndexOf("."));
            packageReference += ".*";
            unresolvedImport.setUnresolvedReference(packageReference);

        } else if (newImport instanceof CtImportImpl importImpl) {

            // Replace the reference with a star import by resolving the reference
            CtReference reference = importImpl.getReference();

            if (reference instanceof CtTypeReference<?> typeReference) {
                CtPackageReference packageRef = typeReference.getPackage();
                newImport = target.getFactory().Type().createImport(packageRef);
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not replace import with star import of type {} and reference of type {}",
                        newImport.getClass().getSimpleName(), reference.getClass().getSimpleName());
                }
            }
        } else {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Could not replace import with star import of type {}",
                    newImport.getClass().getSimpleName());
            }
        }

        // Replace the target import with the new import
        target.replace(newImport);
    }

    /**
     * This method returns the target of the refactoring.
     *
     * @return The target of the refactoring.
     */
    public CtImport getTarget() {
        return target;
    }

    /**
     * This method sets the target of the refactoring.
     *
     * @param target The target of the refactoring.
     */
    public void setTarget(final CtImport target) {
        this.target = target;
    }
}
