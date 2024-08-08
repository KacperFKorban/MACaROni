package macaroni.tests

import scala.quoted.*
import macaroni.reflect.*

inline def generateAMatchWithUnchecked: Option[Int] => Int =
  ${ generateAMatchWithUncheckedImpl }

def generateAMatchWithUncheckedImpl(using Quotes): Expr[Option[Int] => Int] = {
  import quotes.reflect.*
  object mapper extends TreeMap {
    override def transformTerm(tree: Term)(owner: Symbol): Term = tree match
      case Match(selector, cases) =>
        Match(selector.withUnchecked, cases.map(transformCaseDef(_)(owner)))
      case _ => super.transformTerm(tree)(owner)
  }
  val aMatch =
    '{ (e: Option[Int]) =>
        e match
          case Some(value) => value
    }.asTerm
  mapper.transformTerm(aMatch)(Symbol.spliceOwner).asExprOf[Option[Int] => Int]
}
