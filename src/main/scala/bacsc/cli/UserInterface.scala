package bacsc.cli

import bacsc.core


class UserInterface(
    readLine: String => Option[String] = UserInterface.readLine,
) extends core.UserInterface:

  override def init(game: core.Game.Interface): Unit =
    println(
      """BacSc
        |====="""
        .stripMargin
    )

  override def controlGame(game: core.Game.Interface): Unit =
    takeUserInput(game.currentRound.step, game.close)
      .flatMap(filterAndHandleCommand(_, game.commandRepository))
      .flatMap(parseGuess)
      .foreach(game.currentRound.makeGuess)


  override object roundOutputPort extends core.Round.OutputPort:
    override def presentGuessHints(guess: core.BCNumber, bullsCows: (Int, Int)): Unit =
      val (bulls, cows) = bullsCows
      println(s"bulls: $bulls, cows: $cows")

    override def presentSuccessMessage(stepsDone: Int): Unit =
      println(s"\n*** You guessed in $stepsDone steps! ***\n")

    override def promptPlayerNameToSaveScore(): Option[core.Player] =
      readLine(s"Save score as: ") flatMap { input =>
        core.Player.make(input.strip()) match
          case Left(exc) =>
            println(exc.render())
            promptPlayerNameToSaveScore()
          case Right(player) =>
            Some(player)
      }

    override def presentUpdatedRanking(ranking: core.Ranking, position: Int): Unit =
      showRanking(ranking, Some(position))


  override object gameOutputPort extends core.Game.OutputPort:

    override def pager(text: String, title: String = ""): Unit =
      val bordersWidth = 40
      val borderChar = '='
      val topLeftWing = borderChar.toString * ((bordersWidth - title.length) / 2)
      val topBorder = (topLeftWing + title).padTo(bordersWidth, borderChar)
      val bottomBorder = borderChar.toString * bordersWidth
      print(s"$topBorder\n${text.stripLineEnd}\n$bottomBorder\n")

    override def presentRound(round: core.Round.Model): Unit =
      println() // expose round start with empty line

    override def presentRanking(ranking: core.Ranking): Unit =
      showRanking(ranking)

  def takeUserInput(step: Int, endGame: () => Unit): Option[String] =
    readLine(s"[$step] ") orElse { endGame(); None }

  def filterAndHandleCommand(input: String, commandRepository: core.CommandRepository): Option[String] =
    if input.startsWith(core.Command.PREFIX.toString) then
      commandRepository.executeCommandLine(input stripPrefix core.Command.PREFIX.toString)
        .left.foreach(exc => println(exc.render()))
      None
    else
      Some(input)

  def parseGuess(input: String): Option[core.BCNumber] =
    core.BCNumber.make(input).left.map(exc => println(exc.render())).toOption

  def showRanking(ranking: core.Ranking, position: Option[Int] = None): Unit =
    val stringBuilder = StringBuffer()
    val correctPosition = position.map(_ + 1)  // one based counting
    stringBuilder.append(" Pos. Steps Player\n")
    for (idx, record) <- (1 to ranking.length) zip ranking do
      val mark = if correctPosition contains idx then ">" else " "
      stringBuilder.append(f"$mark $idx%2d  ${record.steps}%4d  ${record.player}\n")
    gameOutputPort.pager(stringBuilder.toString, title="Ranking")

object UserInterface:
  def readLine(message: String): Option[String] =
    Option(io.StdIn.readLine(message)) orElse { println(); None }
