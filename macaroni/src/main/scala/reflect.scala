package macaroni.reflect

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
    val uncheckedTypeRef = Symbol.requiredClass("scala.unchecked").typeRef
    Typed(
      t,
      Inferred(
        AnnotatedType(
          t.tpe,
          New(Inferred(uncheckedTypeRef))
        )
      )
    )
  }

extension (using Quotes)(s: quotes.reflect.Symbol)
  def methodMemberOrError(name: String): quotes.reflect.Symbol = {
    import quotes.reflect.*
    s.methodMember(name) match
      case m :: Nil => m
      case Nil => report.errorAndAbort(s"No method named $name found in ${s.fullName}")
      case _ => report.errorAndAbort(s"Multiple methods named $name found in ${s.fullName}")
  }

extension (using Quotes)(obj: quotes.reflect.Term)
  def defaultArgumentGettersForMethod(m: quotes.reflect.Symbol): List[quotes.reflect.TypeRepr] => Map[String, quotes.reflect.Term] = { paramTypes =>
    import quotes.reflect.*
    def defaultGettersForParams(prefix: Term, memberLookupSymbol: Symbol, methodPrefixName: String, params: List[Symbol]): Map[String, Term] = {
      params.zipWithIndex.collect {
        case (p, idx) if p.flags.is(Flags.HasDefault) =>
          val defaultSymbol = memberLookupSymbol.methodMemberOrError(s"${methodPrefixName}$$default$$${idx + 1}")
          val getter = prefix.select(defaultSymbol).appliedToTypes(paramTypes)
          println(s"name: ${p.name}, getter: $getter")
          p.name -> getter
      }.toMap
    }
    // special case for class constructor, since it's default getters are on the companion object
    if m.isClassConstructor then
      val lookupSymbol = m.owner.companionClass
      val params = m.owner.caseFields
      defaultGettersForParams(This(lookupSymbol), lookupSymbol, "$lessinit$greater", params)
    else
      val params = m.paramSymss.collectFirst { case params if params.exists(_.isTerm) => params }.getOrElse(List.empty)
      defaultGettersForParams(obj, obj.tpe.typeSymbol, m.name, params)
  }
