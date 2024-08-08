# Ideas

- annotated helper, to easier fo e.g.
  ```scala
  (x: @unchecked) match ...
  ```
- TypeRepr#asTypeTree
  ```scala
  extension (tpe: TypeRepr)(using Quotes)
    def asTypeTree: TypeTree = Inferred(tpe) // unless it's an antipattern?
  ```
- default params for case fields (ideally it should be usable with mirrors)
- default params of methods
- methodMemberOrError (possibly when ambiguous methods found too)
- warn/info in as inline method