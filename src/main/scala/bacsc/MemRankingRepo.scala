package bacsc


class MemRankingRepo(records: collection.IterableOnce[core.RankingRecord] = Nil) extends core.RankingRepo:

  val data = collection.mutable.TreeSet.from(records)

  def getTop(limit: Int): core.Ranking =
    data.view.take(limit).toList

  def add(record: core.RankingRecord, limit: Int): Option[(core.Ranking, Int)] =
    data += record
    val position = data.iterator.take(limit) indexOf record

    Option.when(position != -1)((getTop(limit), position))

  def wouldFitOnTop(steps: Int, limit: Int): Boolean =
    data.size < limit || data.iterator.take(limit).takeWhile(_.steps <= steps).length < limit
