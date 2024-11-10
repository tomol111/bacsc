package bacsc.core

import org.scalatest.flatspec.AnyFlatSpec


class PlayerSpec extends AnyFlatSpec:

  behavior of "Player name"

  it should "have minimum number of characters" in {
    assert(Player.make("ab") == Right(Player("ab")))
    assert(Player.make("a") == Left(Player.TooShortName))
  }

  it should "not contain white spaces" in {
    assert(Player.make("a b") == Left(Player.ContainsWhiteSpaces))
    assert(Player.make("a\tb") == Left(Player.ContainsWhiteSpaces))
  }

  it should "not contain control characters" in {
    assert(Player.make("a\u0000b") == Left(Player.ContainsControlCharacters))
  }

  it should "render validation exception" in {
    assert(Player.TooShortName.render() == "Name must have at least 2 characters")
    assert(Player.ContainsWhiteSpaces.render() == "Name contains white spaces")
    assert(Player.ContainsControlCharacters.render() == "Name contains control characters")
  }
