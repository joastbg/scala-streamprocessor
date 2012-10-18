package org.joastbg.experiments;
/**
 * StreamProcessor
 * 
 */
import scala.actors.Actor

/**
 * Produces values for the stream
 *
 * @param args variable argument list of StreamActors
 */
class StreamProducer(args: StreamActor*) extends Actor {
  def act() {
    while (true) {
      Thread.sleep((scala.math.random * 1000).toLong)
      for (arg <- args.elements)
        arg ! scala.math.random * 100
    }
  }
}