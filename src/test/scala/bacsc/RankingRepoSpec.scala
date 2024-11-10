package bacsc

import java.time.Instant

import org.scalatest.flatspec.AnyFlatSpec


trait RankingRepoSpec extends AnyFlatSpec:

  def newRepository(): core.RankingRepo

  behavior of "Ranking repository"

  it should "be empty by default" in {
    assert(newRepository().getTop().isEmpty)
  }

  it should "hold new records" in {
    val repo = newRepository()
    val record = core.RankingRecord(3, core.Player("Tom"))
    repo.add(record)
    println(f"${repo.getTop()}")
    println(f"${record}")
    assert(repo.getTop() sameElements List(record))
  }

  it should "sort records" in {
    val repo = newRepository()
    val record1 = core.RankingRecord(3, core.Player("Tom"))
    val record2 = core.RankingRecord(4, core.Player("Tom"))
    val record3 = core.RankingRecord(3, core.Player("Tom"))
    val sortedRecords = List(record1, record3, record2)
    repo.add(record1)
    repo.add(record2)
    repo.add(record3)
    assert(repo.getTop() sameElements sortedRecords)
  }

  it should "truncate top to it's max size" in {
    val repo = newRepository()
    val record1 = core.RankingRecord(3, core.Player("Tom"))
    val record2 = core.RankingRecord(4, core.Player("Tom"))
    val record3 = core.RankingRecord(3, core.Player("Tom"))
    val record4 = core.RankingRecord(2, core.Player("Tom"))
    val sortedRecords = List(record4, record1, record3)
    repo.add(record1)
    repo.add(record2)
    repo.add(record3)
    repo.add(record4)
    assert(repo.getTop(3) sameElements sortedRecords)
  }

  it should "return updated ranking and new record position" in {
    val repo = newRepository()
    val record1 = core.RankingRecord(3, core.Player("Tom"))
    val record2 = core.RankingRecord(4, core.Player("Tom"))
    val record3 = core.RankingRecord(2, core.Player("Tom"))
    val newRecord = core.RankingRecord(3, core.Player("Tom"))
    repo.add(record1)
    repo.add(record2)
    repo.add(record3)

    val (newRanking, position) = repo.add(newRecord, limit=3).get

    assert(newRanking sameElements List(record3, record1, newRecord))
    assert(position == 2)
  }

  it should "not return updated ranking and position if ranking is not in top" in {
    val repo = newRepository()
    val record1 = core.RankingRecord(3, core.Player("Tom"))
    val record2 = core.RankingRecord(4, core.Player("Tom"))
    val record3 = core.RankingRecord(3, core.Player("Tom"))
    val newRecord = core.RankingRecord(4, core.Player("Tom"))
    repo.add(record1)
    repo.add(record2)
    repo.add(record3)

    assert(repo.add(newRecord, limit=3) == None)
  }

  it should "check if new record would fit on top" in {
    val repo = newRepository()
    val record1 = core.RankingRecord(3, core.Player("Tom"))
    val record2 = core.RankingRecord(4, core.Player("Tom"))
    val record3 = core.RankingRecord(3, core.Player("Tom"))
    val sortedRecords = List(record1, record3, record2)
    repo.add(record1)
    repo.add(record2)
    repo.add(record3)
    assert(!repo.wouldFitOnTop(4, limit = 3))
    assert(repo.wouldFitOnTop(3, limit = 3))
  }
