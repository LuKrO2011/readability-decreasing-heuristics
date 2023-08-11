/**
 * This package defines the interfaces for the visitors that are used to traverse the Spoon model.
 * <p>
 * One is forced to put the class in this package because the {@link spoon.reflect.visitor.DefaultJavaPrettyPrinter}
 * depends on several classes in this package. An alternative might be to copy the classes in this package to a new
 * package and modify the {@link spoon.reflect.visitor.DefaultJavaPrettyPrinter} to use the new classes.
 */
package spoon.reflect.visitor;
