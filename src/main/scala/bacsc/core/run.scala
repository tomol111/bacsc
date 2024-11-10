package bacsc.core


def run(ui: UserInterface, rankingRepo: RankingRepo): Unit =
  val game = Game(roundFactory(ui.roundOutputPort, rankingRepo), ui.gameOutputPort, rankingRepo)
  ui.init(game)
  game.open()
  while game.isOpen do
    ui.controlGame(game)
  ui.finish()

def roundFactory
    (roundOutputPort: Round.OutputPort, rankingRepo: RankingRepo)
    (notifyRoundEnd: () => Unit)
: Round.Interface =
  val secret = BCNumber.draw()
  println(s"#secret: $secret")  // logging
  Round(secret, roundOutputPort, rankingRepo, notifyRoundEnd)
