package org.joastbg.experiments;
/**
 * Provides classes for the StreamActor functionality
 * 
 */
import scala.actors.Actor
import java.util.HashMap
import scala.collection.JavaConversions._
import com.espertech.esper.client.{ Configuration, EventBean, UpdateListener, EPServiceProviderManager }

/**
 * Generic callback for Esper to take function
 * @param f the function to call on update event
 */
class A(f: (Array[EventBean], Array[EventBean]) => Unit) extends UpdateListener {
  def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]): Unit = {
    val event = newEvents(0)
    f(newEvents, oldEvents)
  }
}

/**
 * StreamActor acts on streams using complex event processing
 * 
 * @param cepQuery the CEP-query for this StreamActor
 * @param listener what to do when event triggers
 */
class StreamActor(val cepQuery: String, listener:UpdateListener) extends Actor {

  val config = new Configuration();
  val eventType = new HashMap[String, AnyRef]()
  eventType.put("inst", classOf[String])
  eventType.put("price", classOf[Double])
  config.addEventType("OrderEvent", eventType)

  val epService = EPServiceProviderManager.getDefaultProvider(config);
  val expression = cepQuery;
  val statement = epService.getEPAdministrator.createEPL(expression);

  statement.addListener(listener)

  def act() {
    loop {
      react {
        case msg =>
          epService.getEPRuntime.sendEvent(Map("inst" -> "msft", "price" -> msg), "OrderEvent")
      }
    }
  }
}