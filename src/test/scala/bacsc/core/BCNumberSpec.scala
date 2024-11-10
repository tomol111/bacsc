package bacsc.core

import org.scalatest.flatspec.AnyFlatSpec


class BCNumberSpec extends AnyFlatSpec :

  behavior of "A BCNumber"

  it should "require proper number of digits" in {
    assert(BCNumber.make("12345") == Left(BCNumber.WrongSize(5)))
    assert(BCNumber.make("123") == Left(BCNumber.WrongSize(3)))
  }

  it should "require unique digits" in {
    assert(BCNumber.make("2245") == Left(BCNumber.RepeatedDigits("2")))
    assert(BCNumber.make("1313") == Left(BCNumber.RepeatedDigits("13")))
  }

  it should "require only valid digits" in {
    assert(BCNumber.make("1a34") == Left(BCNumber.InvalidDigits("a")))
    assert(BCNumber.make("0bb3") == Left(BCNumber.InvalidDigits("0b")))
  }

  it can "be drawn" in {
    assert(BCNumber.draw(util.Random(234)) == BCNumber("4632"))
  }


  "Guess hints getter" should "count matching digits that are in their right positions as bulls and in different positions as cows" in {
    assert(BCNumber("1234").getHints(BCNumber("5678")) == (0, 0))
    assert(BCNumber("1234").getHints(BCNumber("1234")) == (4, 0))
    assert(BCNumber("1234").getHints(BCNumber("1564")) == (2, 0))
    assert(BCNumber("1234").getHints(BCNumber("4312")) == (0, 4))
    assert(BCNumber("1234").getHints(BCNumber("4356")) == (0, 2))
    assert(BCNumber("1234").getHints(BCNumber("1243")) == (2, 2))
  }


  behavior of "Validation exception renderer"

  it should "inform about incorrect guess's length" in {
    val result = BCNumber.WrongSize(6).render()
    assert(result == "Got 6 digits when 4 is needed!")
  }

  it should "inform about invalid digits in guess" in {
    val result = BCNumber.InvalidDigits("a-").render()
    assert(result == "Invalid digits: a, -")
  }

  it should "inform about repeated digits in guess" in {
    val result = BCNumber.RepeatedDigits("26").render()
    assert(result == "Repeated digits: 2, 6")
  }
