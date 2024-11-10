package bacsc


@main def main(argv: String*): Unit =
  val ui =
    if argv contains "swing" then
      swingGui.UserInterface()
    else
      cli.UserInterface()

  val rankingRepo =
    if argv contains "memory" then
      bacsc.MemRankingRepo()
    else
      bacsc.FileRankingRepo()

  core.run(ui, rankingRepo)
