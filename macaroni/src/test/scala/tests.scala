package macaroni.tests

class MacroUsageTests extends munit.FunSuite {
  import MacroUsageTests.*

  test("normalizedFullNameUsage") {
    val res = macaroni.inlined.normalizedFullName[TestClass]
    assertEquals(res, "macaroni.tests.MacroUsageTests.TestClass")
  }

  test("addUncheckedToMatch") {
    val res = generateAMatchWithUnchecked
    assertEquals(res(Some(42)), 42)
  }

  test("defaultArgumentGetters for method") {
    val obj = TestClass()
    val res = macaroni.inlined.defaultMethodArgumentGetters(obj, "aMethodWithADefaultArgument")
    assertEquals(res, Map("a" -> 42))
  }

  test("defaultArgumentGetters for class") {
    val res = macaroni.inlined.defaultClassArgumentGetters[TestClass2]
    assertEquals(res, Map("a" -> 42))
  }
}

object MacroUsageTests {
  class TestClass:
    def aMethodWithADefaultArgument(a: Int = 42): Int = a

  case class TestClass2(a: Int = 42)
}
