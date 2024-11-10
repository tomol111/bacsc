package bacsc.core

import org.scalatest.flatspec.AnyFlatSpec


class CommandRepositorySpec extends AnyFlatSpec:

  behavior of "Command repository"

  it should "execute command by it's name" in {
    var commandExecuted = false
    object FakeCommand extends Command("abc"):
      override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] =
        commandExecuted = true
        Right(())

    CommandRepository(GameStub(), List(FakeCommand))
      .executeCommandLine("abc")

    assert(commandExecuted)
  }

  it should "notify if command was absent" in {
    val result = CommandRepository(GameStub(), List())
      .executeCommandLine("abc")

    assert(result == Left(Command.NotFound("abc")))
  }

  it should "execute command by abbreviation" in {
    var commandExecuted = false
    object FakeCommand extends Command("abcde"):
      override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] =
        commandExecuted = true
        Right(())

    CommandRepository(GameStub(), List(FakeCommand))
      .executeCommandLine("abc")

    assert(commandExecuted)
  }

  it should "notify if abbreviation was ambiguous" in {
    class FakeCommand(name: String) extends Command(name):
      override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] =
        Right(())

    val result = CommandRepository(GameStub(), List(FakeCommand("abcd"), FakeCommand("abxy")))
      .executeCommandLine("ab")

    assert(result == Left(Command.AmbiguousAbbreviation("ab", List("abcd", "abxy"))))
  }

  it should "execute command by name even if name is also abbreviation" in {
    var commandExecuted = false
    object FakeTargetCommand extends Command("abc"):
      override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] =
        commandExecuted = true
        Right(())
    object FakeNonTargetCommand extends Command("abcde"):
      override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] =
        Right(())

    CommandRepository(GameStub(), List(FakeTargetCommand, FakeNonTargetCommand))
      .executeCommandLine("abc")

    assert(commandExecuted)
  }

  it should "execute command with arguments" in {
    var commandArguments: List[String] = null
    object FakeCommand extends Command("abc"):
      override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] =
        commandArguments = args
        Right(())

    CommandRepository(GameStub(), List(FakeCommand))
      .executeCommandLine("abc arg1  arg2")

    assert(commandArguments == List("arg1", "arg2"))
  }

  it should "recognize empty command line" in {
    val result = CommandRepository(GameStub(), List())
      .executeCommandLine("")

    assert(result == Left(Command.EmptyCommandLine))
  }

  it should "it should not parse empty tokens when additional white spaces occurs" in {
    var commandArguments: List[String] = null
    object FakeCommand extends Command("abc"):
      override def execute(game: Game.Interface, args: List[String]): Either[Command.Exception, Unit] =
        commandArguments = args
        Right(())

    CommandRepository(GameStub(), List(FakeCommand))
      .executeCommandLine("  abc arg1  arg2  ")

    assert(commandArguments == List("arg1", "arg2"))
  }