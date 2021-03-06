package ca.uwaterloo.ide.conversion

import java.util

import ca.uwaterloo.ide.problem.solver.{IdeSolver, PropagatorI}
import com.ibm.wala.dataflow.IFDS.{PathEdge, TabulationProblem, TabulationResult}
import com.ibm.wala.util.intset.{IntSet, MutableSparseIntSet}

import scala.collection.JavaConverters._

trait IdeResultToIfdsResult extends IdeFromIfdsBuilder with IdeSolver with PropagatorI {

  def ideResultToIfdsResult: TabulationResult[Node, Procedure, F] =
    new TabulationResult[Node, Procedure, F] {

      override def getResult(node: Node): IntSet = {
        val result = MutableSparseIntSet.makeEmpty
        for {
          (XNode(`node`, f), l) <- solvedResult
          if l != Top
        } {
          result.add(f)
        }
        result
      }

      // todo
      override def getSummaryTargets(n1: Node, d1: Int, n2: Node): IntSet = {
        val result = MutableSparseIntSet.makeEmpty
        ???
      }

      override def getSeeds: util.Collection[PathEdge[Node]] = walaIfdsProblem.initialSeeds()

      override def getSupergraphNodesReached: util.Collection[Node] =
        (for {
          (XNode(n, f), l) <- solvedResult
//          if f != Λ // todo apparently in WALA's IFDS implementation there's no 0 fact
          if l != Top
        } yield n).toSeq.asJava

      override def getProblem: TabulationProblem[Node, Procedure, F] = walaIfdsProblem
    }
}
