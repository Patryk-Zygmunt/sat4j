/*******************************************************************************
 * SAT4J: a SATisfiability library for Java Copyright (C) 2004-2008 Daniel Le Berre
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU Lesser General Public License Version 2.1 or later (the
 * "LGPL"), in which case the provisions of the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL, and not to allow others to use your version of
 * this file under the terms of the EPL, indicate your decision by deleting
 * the provisions above and replace them with the notice and other provisions
 * required by the LGPL. If you do not delete the provisions above, a recipient
 * may use your version of this file under the terms of the EPL or the LGPL.
 * 
 * Based on the pseudo boolean algorithms described in:
 * A fast pseudo-Boolean constraint solver Chai, D.; Kuehlmann, A.
 * Computer-Aided Design of Integrated Circuits and Systems, IEEE Transactions on
 * Volume 24, Issue 3, March 2005 Page(s): 305 - 317
 * 
 * and 
 * Heidi E. Dixon, 2004. Automating Pseudo-Boolean Inference within a DPLL 
 * Framework. Ph.D. Dissertation, University of Oregon.
 *******************************************************************************/
package org.sat4j.pb.constraints.pb;

import java.math.BigInteger;

public class ConflictMapSwitchToClause extends ConflictMap {

	public static int UPPERBOUND;

	public ConflictMapSwitchToClause(PBConstr cpb, int level) {
		super(cpb, level);
	}

	public static IConflict createConflict(PBConstr cpb, int level) {
		return new ConflictMapSwitchToClause(cpb, level);
	}

	/**
	 * reduces the constraint defined by wpb until the result of the cutting
	 * plane is a conflict. this reduction returns either a clause if .
	 * 
	 * @param litImplied
	 * @param ind
	 * @param reducedCoefs
	 * @param wpb
	 * @return BigInteger.ONE
	 */
	@Override
	protected BigInteger reduceUntilConflict(int litImplied, int ind,
			BigInteger[] reducedCoefs, WatchPb wpb) {
		BigInteger degreeCons = super.reduceUntilConflict(litImplied, ind,
				reducedCoefs, wpb);
		// updating of the degree of the conflict
		int i = 0;
		for (; i < reducedCoefs.length
				&& reducedCoefs[i].equals(BigInteger.ZERO) && i != ind; i++) {
		}
		if (i < reducedCoefs.length) {
			BigInteger bigCoef = reducedCoefs[i].multiply(coefMultCons);
			if (weightedLits.containsKey(wpb.get(i)))
				bigCoef.add(weightedLits.get(wpb.get(i)).multiply(coefMult));
			if (bigCoef.toString().length() > UPPERBOUND) {
				// if we deal with really big integers
				// reducing the constraint to a clause
				numberOfReductions++;
				hasBeenReduced = true;
				degreeCons = reduceToClause(ind, wpb, reducedCoefs);
				coefMultCons = weightedLits.get(litImplied ^ 1);
				coefMult = BigInteger.ONE;
			}
		}
		return degreeCons;
	}

	private BigInteger reduceToClause(int ind, WatchPb wpb,
			BigInteger[] reducedCoefs) {
		for (int i = 0; i < reducedCoefs.length; i++)
			if (i == ind || wpb.getVocabulary().isFalsified(wpb.get(i)))
				reducedCoefs[i] = BigInteger.ONE;
			else
				reducedCoefs[i] = BigInteger.ZERO;
		return BigInteger.ONE;
	}

}