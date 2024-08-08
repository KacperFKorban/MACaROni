package macaroni.quoted

import scala.quoted.*
import macaroni.reflect.*

def normalizedFullName[T: Type](using Quotes): Expr[String] = {
  import quotes.reflect.*
  val s = TypeRepr.of[T].typeSymbol
  Expr(macaroni.reflect.normalizedFullName(s))
}

private def gettersMapToExpr(using Quotes)(getters: Map[String, quotes.reflect.Term]): Expr[Map[String, Any]] = {
  import quotes.reflect.*
  val gettersListExpr = Expr.ofList(
    getters.map { case (name, term) => Expr(name) -> term.asExpr }.map {
      case (name, term) => '{ $name -> $term }
    }.toList
  )
  '{ $gettersListExpr.toMap }
}

def classDefaultArgumentGetters[T: Type](using Quotes): Expr[Map[String, Any]] = {
  import quotes.reflect.*
  val tpe = TypeRepr.of[T]
  val s = tpe.typeSymbol.primaryConstructor
  val companionRef = Ref(tpe.typeSymbol.companionModule)
  val getters = companionRef.defaultArgumentGettersForMethod(s)(tpe.typeArgs)
  gettersMapToExpr(getters)
}

def methodDefaultArgumentGetters[T: Type](obj: Expr[T], methodName: String)(using Quotes): Expr[Map[String, Any]] = {
  import quotes.reflect.*
  val tpe = TypeRepr.of[T]
  val s = tpe.typeSymbol.methodMemberOrError(methodName)
  val getters = obj.asTerm.defaultArgumentGettersForMethod(s)(tpe.typeArgs)
  gettersMapToExpr(getters)
}

def methodDefaultArgumentGetters[T: Type](obj: Expr[T], methodName: Expr[String])(using Quotes): Expr[Map[String, Any]] = {
  import quotes.reflect.*
  methodDefaultArgumentGetters(obj, methodName.valueOrAbort)
}
