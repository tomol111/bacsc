package bacsc.core


trait UserInterface:
  def init(game: Game.Interface): Unit 
  def controlGame(game: Game.Interface): Unit = Thread.sleep(200)  // block by default to not make short circuit in core.run()
  def finish(): Unit = ()

  def gameOutputPort: Game.OutputPort
  def roundOutputPort: Round.OutputPort