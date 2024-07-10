package macaroni.tests

import scala.quoted.*
import macaroni.*

inline def normalizedFullNameUsage[T]: String = 
  ${ normalizedFullNameImpl[T] }

inline def generateAMatchWithUnchecked: Option[Int] => Int =
  ${ generateAMatchWithUncheckedImpl }

def normalizedFullNameImpl[T: Type](using Quotes): Expr[String] = {
  import quotes.reflect.*
  val s = TypeRepr.of[T].typeSymbol
  Expr(s.normalizedFullName)
}

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
