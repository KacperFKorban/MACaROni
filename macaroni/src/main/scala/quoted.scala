package macaroni

import scala.quoted.*

extension (using Quotes)(s: quotes.reflect.Symbol)
  private def normalizedName: String = {
    import quotes.reflect.*
    val withoutObjectSuffix = if s.flags.is(Flags.Module) then s.name.stripSuffix("$") else s.name
    val constructorNormalizedName = if s.isClassConstructor then "this" else withoutObjectSuffix
    constructorNormalizedName
  }

  private def ownerNameChain: List[String] = {
    import quotes.reflect.*
    if s.isNoSymbol then List.empty
    else if s == defn.EmptyPackageClass then List.empty
    else if s == defn.RootPackage then List.empty
    else if s == defn.RootClass then List.empty
    else s.owner.ownerNameChain :+ s.normalizedName
  }

  def normalizedFullName: String =
    s.ownerNameChain.mkString(".")

extension (using Quotes)(t: quotes.reflect.TypeRepr)
  def toTypeTree: quotes.reflect.TypeTree = {
    import quotes.reflect.*
    Inferred(t)
  }

extension (using Quotes)(t: quotes.reflect.Term)
  def withUnchecked: quotes.reflect.Term = {
    import quotes.reflect.*
    Typed(t, Inferred(AnnotatedType(t.tpe, New(Inferred(Symbol.requiredClass("scala.unchecked").typeRef))))) 
  }
