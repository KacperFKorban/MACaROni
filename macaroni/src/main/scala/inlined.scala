package macaroni.inlined

import scala.quoted.*
import macaroni.quoted.*

inline def normalizedFullName[T]: String = 
  ${ macaroni.quoted.normalizedFullName[T] }

inline def defaultClassArgumentGetters[T]: Map[String, Any] = 
  ${ macaroni.quoted.classDefaultArgumentGetters[T] }

inline def defaultMethodArgumentGetters[T](obj: T, inline methodName: String): Map[String, Any] = 
  ${ macaroni.quoted.methodDefaultArgumentGetters('obj, 'methodName) }
