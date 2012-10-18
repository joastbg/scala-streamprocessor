package org.joastbg.experiments;
/**
 * Running stream actors and producer
 * 
 */
import scala.actors.Actor
import com.espertech.esper.client.{ EventBean, UpdateListener }
import akka.zeromq.ZeroMQExtension
import org.zeromq.ZMQ

object Setting1 {

  def query = "select avg(price) from OrderEvent.win:time(30 sec)"

  def callback(newEvents: Array[EventBean], oldEvents: Array[EventBean]): Unit = {
    val event = newEvents(0)
    println("avg=" + event.get("avg(price)"))
  }
}

object Setting2 {

  def query = "select avg(price) from OrderEvent.win:time(30 sec) having avg(price) < 50"

  def callback(newEvents: Array[EventBean], oldEvents: Array[EventBean]): Unit = {
    println("Triggered -- Lets go SHORT!")
  }
}

object Runner extends App {

  // Create one stream producer and two stream actors
  val sta1 = new StreamActor(Setting1.query, new A(Setting1.callback))
  val sta2 = new StreamActor(Setting2.query, new A(Setting2.callback))
  var stp1 = new StreamProducer(sta1, sta2)

  sta1.start
  stp1.start
}