package bacsc.core


case class BCNumber(digits: String):
  def getHints(guess: BCNumber): (Int, Int) =
    val bulls = (this.digits zip guess.digits).map(_ == _).count(identity)
    val cows = (this.digits intersect guess.digits).length - bulls
    (bulls, cows)

object BCNumber:
  val VALID_DIGITS = "123456789"
  val SIZE = 4

  def apply(digits: String): BCNumber =
    make(digits).left.map(throw _).toOption.get

  def make(digits: String): Either[ValidationException, BCNumber] =
    lazy val invalidDigits = digits.distinct diff BCNumber.VALID_DIGITS
    lazy val repeatedDigits = (digits diff digits.distinct).distinct

    if digits.length != BCNumber.SIZE then
      Left(WrongSize(digits.length))
    else if invalidDigits.nonEmpty then
      Left(InvalidDigits(invalidDigits))
    else if repeatedDigits.nonEmpty then
      Left(RepeatedDigits(repeatedDigits))
    else
      Right(new BCNumber(digits))

  def draw(randomGenerator: util.Random = util.Random): BCNumber =
    apply(randomGenerator.shuffle(BCNumber.VALID_DIGITS).take(BCNumber.SIZE).toString)


  export ValidationException.*
  enum ValidationException(message: String) extends Exception(message):
    case WrongSize(givenSize: Int)
      extends ValidationException(givenSize.toString)
    case InvalidDigits(invalidDigits: String)
      extends ValidationException(invalidDigits.mkString(", "))
    case RepeatedDigits(repeatedDigits: String)
      extends ValidationException(repeatedDigits.mkString(", "))

    def render(): String = this match
      case BCNumber.WrongSize(givenSize) =>
        s"Got $givenSize digits when ${BCNumber.SIZE} is needed!"
      case BCNumber.InvalidDigits(invalidDigits) =>
        "Invalid digits: " + invalidDigits.mkString(", ")
      case BCNumber.RepeatedDigits(repeatedDigits) =>
        "Repeated digits: " + repeatedDigits.mkString(", ")
