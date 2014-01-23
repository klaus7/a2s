package at.ac.meduniwien.mias.adltoschematron.context;

import at.ac.meduniwien.mias.adltoschematron.TreeElement;

/**
 * Interface for finding the correct context.
 * 
 * @author Klaus Pfeiffer
 */
public interface IContextElementStrategy {

	TreeElement getBestMatchingTreeElementStrategy(final ContextElement ce, final TreeElement element);

}
