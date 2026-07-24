package com.can.structural.adapter;

/**
 * Target contract understood by {@link RoundHole}.
 *
 * <p>The hole needs a radius, not a particular concrete peg class. Keeping
 * that requirement as a small interface lets adapters use composition
 * without manufacturing unused superclass state.</p>
 *
 * <p>Implementations must return a positive, finite radius. {@link RoundHole}
 * verifies this semantic contract at its public boundary so a faulty adapter
 * cannot turn an invalid measurement into a successful fit.</p>
 */
public interface RoundPegShape {

    double getRadius();
}
