package bacsc.swingGui

import scala.swing as sw

import bacsc.core


class MenuBar(game: core.Game.Interface) extends sw.MenuBar:
  contents += new sw.Menu("Game") {
    mnemonic = sw.event.Key.G

    contents += new sw.MenuItem(
      new sw.Action("Restart"):
        mnemonic = sw.event.Key.R.id
        def apply(): Unit =
          game.restartRound()
    )

    contents += sw.Separator()

    contents += new sw.MenuItem(
      new sw.Action("Quit"):
        mnemonic = sw.event.Key.Q.id
        def apply(): Unit =
          game.close()
    )
  }

  contents += new sw.Menu("Tools") {
    mnemonic = sw.event.Key.T

    contents += new sw.MenuItem(
      new sw.Action("Show ranking"):
        mnemonic = sw.event.Key.S.id
        def apply(): Unit =
          game.showRanking()
    )

    contents += new sw.MenuItem(
      new sw.Action("List commands"):
        mnemonic = sw.event.Key.L.id
        def apply(): Unit =
          game.listCommands()
    )
  }

  contents += new sw.Menu("Help") {
    mnemonic = sw.event.Key.H

    contents += new sw.MenuItem(
      new sw.Action("Introduction"):
        mnemonic = sw.event.Key.I.id
        def apply(): Unit =
          game.help(core.Game.HelpSubject.Introduction)
    )

    contents += sw.Separator()

    contents += new sw.MenuItem(
      new sw.Action("Game rules"):
        mnemonic = sw.event.Key.G.id
        def apply(): Unit =
          game.help(core.Game.HelpSubject.Rules)
    )
  }
