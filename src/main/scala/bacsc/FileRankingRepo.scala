package bacsc

import java.time.Instant

import com.github.tototoshi.csv
import com.github.tototoshi.csv.defaultCSVFormat


object FileRankingRepo:

  val FILE_NAME = java.io.File("ranking.csv")

  def ensureFile(): java.io.File =
    FILE_NAME.createNewFile()
    FILE_NAME

class FileRankingRepo(val file: java.io.File = FileRankingRepo.ensureFile()) extends core.RankingRepo:

  override def add(record: core.RankingRecord, limit: Int): Option[(core.Ranking, Int)] =
    val row = List(record.steps, record.player, record.timeStamp)
    csv.CSVWriter.open(file, append=true).writeRow(row)

    val data = readData()
    val position = data.iterator.take(limit) indexOf record
    Option.when(position != -1)((data.view.take(limit).toList, position))

  override def getTop(limit: Int): core.Ranking =
    readData().iterator.take(limit).toList

  def readData(): collection.SortedSet[core.RankingRecord] =
    val records = csv.CSVReader.open(file).iterator map {
      case Seq(steps, player, timeStamp) =>
        core.RankingRecord(steps.toInt, core.Player(player), Instant.parse(timeStamp))
    }
    collection.mutable.TreeSet.from(records)

  override def wouldFitOnTop(steps: Int, limit: Int): Boolean =
    val data = readData()
    data.size < limit || data.iterator.take(limit).takeWhile(_.steps <= steps).length < limit
