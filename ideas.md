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
