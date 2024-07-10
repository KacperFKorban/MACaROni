package macaroni.tests

class MacroUsageTests extends munit.FunSuite {
  import MacroUsageTests.*

  test("normalizedFullNameUsage") {
    val res = normalizedFullNameUsage[TestClass]
    assertEquals(res, "macaroni.tests.MacroUsageTests.TestClass")
  }

  test("addUncheckedToMatch") {
    val res = generateAMatchWithUnchecked
    assertEquals(res(Some(42)), 42)
  }
}

object MacroUsageTests {
  class TestClass
}
