package bacsc.core


import java.time.Instant

import scala.collection.immutable


type Ranking = immutable.Seq[RankingRecord]
object Ranking:
  val LIMIT = 10


case class RankingRecord(steps: Int, player: Player, timeStamp: Instant = Instant.now())
object RankingRecord:
  given Ordering[RankingRecord] = Ordering.by[RankingRecord, (Int, Instant)](
    record => (record.steps, record.timeStamp)
  )

export Players.*
private object Players:

  opaque type Player = String

  extension (player: Player)
    def toString: String = player

  object Player:

    val MIN_LENGTH = 2

    def apply(name: String): Player =
      make(name).left.map(throw _).toOption.get

    def make(name: String): Either[ValidationException, Player] =
      if name.length < MIN_LENGTH then
        Left(TooShortName)
      else if name.map(_.isWhitespace).fold(false)(_ || _) then
        Left(ContainsWhiteSpaces)
      else if name.map(_.isControl).fold(false)(_ || _) then
        Left(ContainsControlCharacters)
      else
        Right(name)

    export ValidationException.*
    enum ValidationException extends Exception:
      case TooShortName
      case ContainsWhiteSpaces
      case ContainsControlCharacters

      def render(): String = this match
        case TooShortName =>
          f"Name must have at least $MIN_LENGTH characters"
        case ContainsWhiteSpaces =>
          "Name contains white spaces"
        case ContainsControlCharacters =>
          "Name contains control characters"


trait RankingRepo:
  def add(record: RankingRecord, limit: Int = Ranking.LIMIT): Option[(Ranking, Int)]
  def getTop(limit: Int = Ranking.LIMIT): Ranking
  def wouldFitOnTop(steps: Int, limit: Int = Ranking.LIMIT): Boolean
