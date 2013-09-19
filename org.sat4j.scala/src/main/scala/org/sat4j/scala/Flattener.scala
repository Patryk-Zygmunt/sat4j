package org.sat4j.scala
import scala.collection._
import org.sat4j.scala.Logic._

/** Abstract base class representing boolean literals in a FlatClause or cardinality constraint. */
abstract class Literal(val id: Int) {
  def unary_!():Literal
}

/** Represents positive literals (without negation). */
case class PositiveLiteral(override val id: Int) extends Literal(id) {
  def unary_!() = NegativeLiteral(this.id)
  override def toString = "x" + id.toString
}

/** Represents a negative literal (with a logical negation). */
case class NegativeLiteral(override val id: Int) extends Literal(id){
  def unary_!() = PositiveLiteral(this.id)
  override def toString = "~x" + id.toString
}

/** Base class for constraints making up a flat constraint system. */
abstract class FlatConstraint

/** A clause.*/
class FlatClause(literals: Literal*) extends FlatConstraint {
  override def toString = literals.mkString("(", " | ", ")")
}

/** An 'at most' cardinality constraint. */
class FlatAtMost(literals: List[Literal], k:Int) extends FlatConstraint {
  override def toString = "(" + literals.mkString(" + ") + " <= " + k.toString + ")"
}

/** An 'at least' cardinality constraint. */
class FlatAtLeast(literals: List[Literal], k:Int) extends FlatConstraint {
  override def toString = "(" + literals.mkString(" + ") + " >= " + k.toString + ")"
}

/** An 'exact' cardinality constraint. */
class FlatCardExact(literals: List[Literal], k:Int) extends FlatConstraint {
  override def toString = "(" + literals.mkString(" + ") + " = " + k.toString + ")"
}

/** A system in conjunctive normal form, also stores the mapping from identifiers to literals (immutable attributes). */
case class CNFSystem(constraints: List[FlatConstraint], identifierMap: immutable.HashMap[Identifier, Literal]) {
  override def toString = {
    val s1 = (identifierMap map { x => x.toString }).mkString("\n")
    val s2 = (constraints map { x => x.toString }).mkString("\n")
    "IdentifierMap:\n" + s1 + "\nConstraints:\n" + s2
  }
}

/** Allows to flatten a BoolExp to a list of clauses and cardinality constraints.
 * 
 * */
class Flattener {
  
	/** A cache which stores the translation of expressions. */
	val boolExpCache = new mutable.OpenHashMap[BoolExp, Literal]()
	
	/** Map storing the literals associated with BoolExp identifiers */
	val identifierMap = new mutable.OpenHashMap[Identifier, Literal]()

	/** All constraints generated by the flattening. */
	val constraints = new mutable.ArrayStack[FlatConstraint]()
	
	/** PositiveLiteral represents True. */
	constraints += new FlatClause(PositiveLiteral(0))

	/** Returns an immutable constraint system generated from the current contents of the flattener. */
	def getCNFSystem = new CNFSystem(constraints.toList, new immutable.HashMap[Identifier, Literal]() ++ identifierMap.iterator)
	
	/** Global Literal id counter. */
	var gid = 0
	
	/** Returns a new literal. */
	private[this] def newLiteral = {
	  gid += 1
	  PositiveLiteral(gid)
	} 

	/** Returns a literal encoding the given expression.
	 *  Constraints are created by side effect and stored in the flattener.
	 * */
	def apply(e: BoolExp):Literal = boolExpCache.getOrElseUpdate(e, _translate(e))

	private[this] def _translate(e: BoolExp):Literal = e match {
	  case True => {
		  PositiveLiteral(0)
	  }
	  case False => {
		  NegativeLiteral(0)
	  }
//	  case ident@Ident(s) => {
//		  val x = newLiteral
//		  identifierMap += (ident -> x)
//		  x
//	  }
//	  case ident@IndexedIdent(s, l) => {
//		  val x = newLiteral
//		  identifierMap += (ident -> x)
//		  x
//	  }
//	  case Not(Not(l)) => {
//		  apply(l)
//	  }
//	  case Not(l) => {
//		  !apply(l)
//	  }
//	  
//	  case And(False, _) => apply(False)
//	  case And(_, False) => apply(False)
//	  case And(True, r) => apply(r)
//	  case And(l, True) => apply(l)
//	  
//	  case And(l, r) => {
//		  val x = newLiteral
//		  val tl = apply(l)
//		  val tr = apply(r)
//		  constraints += new FlatClause(x, !tl, !tr)
//		  constraints += new FlatClause(!x, tl)
//		  constraints += new FlatClause(!x, tr)
//		  x
//	  }
//
//	  case Or(False, r) => apply(r)
//	  case Or(l, False) => apply(l)
//	  case Or(True, _) => apply(True)
//	  case Or(_, True) => apply(True)
//	  
//	  case Or(l, r) => {
//		  val x = newLiteral
//		  val tl = apply(l)
//		  val tr = apply(r)
//		  constraints += new FlatClause(!x, tl, tr)
//		  constraints += new FlatClause(x, !tl)
//		  constraints += new FlatClause(x, !tr)
//		  x
//	  }
//
//	  case Implies(False, _) => apply(True)
//	  case Implies(True, r) => apply(r)
//	  case Implies(l, False) => apply(Not(l))
//	  
//	  case Implies(l, r) => {
//		  val x = newLiteral
//		  val tl = apply(l)
//		  val tr = apply(r)
//		  constraints += new FlatClause(!x, !tl, tr)
//		  constraints += new FlatClause(x, tl)
//		  constraints += new FlatClause(x, !tr)
//		  x
//	  }
//	  
//	  case Iff(True, r) => apply(r)
//	  case Iff(False, r) => apply(Not(r))
//	  case Iff(l, True) => apply(l)
//	  case Iff(l, False) => apply(Not(l))
//	  case Iff(l, r) => {
//		  val x = newLiteral
//		  val tl = apply(l)
//		  val tr = apply(r)
//		  constraints += new FlatClause(!x, !tl, tr)
//		  constraints += new FlatClause(!x, tl, !tr)
//		  constraints += new FlatClause(x, tl, tr)
//		  constraints += new FlatClause(x, !tl, !tr)
//		  x
//	  }
//	  
//	  case CardEQ(es, k) => apply(And(CardLE(es,k), CardGE(es,k)))
//
//	  /*
//	   *  x1 + ... + xn <= k equivalent to literal x with
//	   * (x + ...(k+1 times)... + x)  + x1 + ... + xn >= k+1
//	   * (x + ...(n times)... + x)  + x1 + ... + xn <= n + k
//	   */
//	  case CardLE(es, k) => {
//		  val x = newLiteral
//		  val n = es.size
//		  val kp1x = (1 to k+1).toList map { _ => x }
//		  val nx = (1 to n).toList map { _ => x }
//		  val tes = es map { apply }
//		  constraints += new FlatAtLeast(tes.reverse_:::(kp1x), k + 1)
//		  constraints += new FlatAtMost(tes.reverse_:::(nx), n + k )
//		  x
//	  }
//
//	  case CardLT(es, k) => apply(CardLE(es, k-1))
//	  
//	  /*
//	   *  x1 + ... + xn >= k equivalent to literal x with
//	   * (!x + ...(k times)... + !x)  + x1 + ... + xn >= k
//	   * (!x + ...(n times)... + !x)  + x1 + ... + xn <= n + k - 1
//	   */
//	  case CardGE(es, k) => {
//		  val x = newLiteral
//		  val n = es.size
//		  val kx = (1 to k).toList map { _ => !x }
//		  val nx = (1 to n).toList map { _ => !x }
//		  val tes = es map { apply }
//		  constraints += new FlatAtLeast(tes.reverse_:::(kx), k)
//		  constraints += new FlatAtMost(tes.reverse_:::(nx), n + k - 1)
//		  x
//	  }
//	  
//	  case CardGT(es, k) => apply(CardGE(es, k+1))
	} 
}