package bacsc.swingGui

import org.scalatest.flatspec.AnyFlatSpec

import scala.jdk.CollectionConverters.*
import scala.swing as sw
import bacsc.core


class HintsAreaSpec extends AnyFlatSpec:

  behavior of "Hints area"

  it should "have column headers after preparation" in {
    val hintsTableModel = HintsArea.TableModel()
    val hintsArea = HintsArea(hintsTableModel)

    hintsArea.prepare()

    assert(hintsTableModel.getColumnCount() == 3)
    assert(hintsTableModel.getColumnName(0) == "Guess")
    assert(hintsTableModel.getColumnName(1) == "Bulls")
    assert(hintsTableModel.getColumnName(2) == "Cows")
  }

  it should "display hints in separate rows" in {
    val hintsTableModel = HintsArea.TableModel()
    val hintsArea = HintsArea(hintsTableModel)

    hintsArea.prepare()
    hintsArea.add(core.BCNumber("2341"), (2, 1))
    hintsArea.add(core.BCNumber("5768"), (0, 1))

    assert(
      hintsTableModel.getDataVector() ==
        List(
          List[Any]("2341", 2, 1).asJava,
          List[Any]("5768", 0, 1).asJava,
        ).asJava
    )
  }

  it should "clear table on preparation" in {
    val hintsTableModel = HintsArea.TableModel()
    hintsTableModel.setDataVector(Array(Array[Any]("Old stuff")), Array[Any]("Some column name"))
    val hintsArea = HintsArea(hintsTableModel)
    hintsArea.prepare()

    assert(hintsTableModel.getDataVector().isEmpty)
    assert(hintsTableModel.getColumnCount() == 3)
  }

  it should "not allow to edit cells" in {
    val hintsTableModel = HintsArea.TableModel()
    assert(!hintsTableModel.isCellEditable(0, 0))
  }