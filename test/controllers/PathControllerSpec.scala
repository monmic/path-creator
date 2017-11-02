package controllers

import java.io.File

import akka.util.Timeout
import org.junit.runner.RunWith
import play.api.test.FakeRequest
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.contentAsBytes
import play.api.{ApplicationLoader, Environment, Mode}
import play.api.test.WithApplication

import scala.concurrent.duration._
import scala.reflect.ClassTag
import models.{Map, OutputRepr}

object TestHelper {
  def app = (new GuiceApplicationBuilder).in(new Environment(new File("."), ApplicationLoader.getClass.getClassLoader, Mode.Test)).build()

  def inject[T: ClassTag]: T = app.injector.instanceOf[T]
}

@RunWith(classOf[JUnitRunner])
class PathControllerSpec extends Specification {

  "creation path - POST request" should {

    "getObjsToString" in new WithApplication(TestHelper.app) {
      val controller = TestHelper.inject[controllers.PathController]
      val objects: List[String] = List("Knife","Potted Plant")
      controller.objsToString(objects) must be equalTo "Knife, Potted Plant"
    }

    "executeStep 1" in new WithApplication(TestHelper.app) {
      val controller = TestHelper.inject[controllers.PathController]
      val passId = 2
      val map = Map.jsonFmt.reads(Json.parse("""{
                    "rooms": [
                      { "id": 1, "name": "Hallway", "north": 2, "objects": [] },
                      { "id": 2, "name": "Dining Room", "south": 1, "west": 3, "east": 4,"objects": [] },
                      { "id": 3, "name": "Kitchen","east":2, "objects": [ { "name": "Knife" } ]},
                      { "id": 4, "name": "Sun Room","west":2, "objects": [ { "name": "Potted Plant" } ]}
                    ]
                  }
        """.stripMargin)).get
      val objects = List("Knife", "Potted Plant", "Pillow")
      controller.executeStep(passId, map, objects, List.empty)
    }

    "getOutputRepr 1" in new WithApplication(TestHelper.app) {
      val controller = TestHelper.inject[controllers.PathController]
      val passId = 2
      val map = Map.jsonFmt.reads(Json.parse("""{
                    "rooms": [
                      { "id": 1, "name": "Hallway", "north": 2, "objects": [] },
                      { "id": 2, "name": "Dining Room", "south": 1, "west": 3, "east": 4,"objects": [] },
                      { "id": 3, "name": "Kitchen","east":2, "objects": [ { "name": "Knife" } ]},
                      { "id": 4, "name": "Sun Room","west":2, "objects": [ { "name": "Potted Plant" } ]}
                    ]
                  }
        """.stripMargin)).get
      val objects = List.empty
      controller.getOutputRepr(map, passId, objects) must be equalTo(List(OutputRepr(-1, "None", "No objects to look for")))
    }

    "getOutputRepr 2" in new WithApplication(TestHelper.app) {
      val controller = TestHelper.inject[controllers.PathController]
      val passId = 5
      val map = Map.jsonFmt.reads(Json.parse("""{
                    "rooms": [
                      { "id": 1, "name": "Hallway", "north": 2, "objects": [] },
                      { "id": 2, "name": "Dining Room", "south": 1, "west": 3, "east": 4,"objects": [] },
                      { "id": 3, "name": "Kitchen","east":2, "objects": [ { "name": "Knife" } ]},
                      { "id": 4, "name": "Sun Room","west":2, "objects": [ { "name": "Potted Plant" } ]}
                    ]
                  }
        """.stripMargin)).get
      val objects = List("Knife")
      controller.getOutputRepr(map, passId, objects) must be equalTo(List(OutputRepr(-1, "None", "None")))
    }

    "get the path description with Example 1" in new WithApplication(TestHelper.app) {

      val timeout = Timeout(1 day)
      val controller = TestHelper.inject[controllers.PathController]

      val jsonStr = """
      {
        "map": {
          "rooms": [
            { "id": 1, "name": "Hallway", "north": 2, "objects": [] },
            { "id": 2, "name": "Dining Room", "south": 1, "west": 3, "east": 4,"objects": [] },
            { "id": 3, "name": "Kitchen","east":2, "objects": [ { "name": "Knife" } ]},
            { "id": 4, "name": "Sun Room","west":2, "objects": [ { "name": "Potted Plant" } ] }
          ]
        },
        "startId": 2,
        "objects": [
          "Knife",
          "Potted Plant"
        ]
      }
      """
      val json = Json.parse(jsonStr)


      val req2 = FakeRequest().withJsonBody(json)
      val res2 = contentAsBytes(controller.createPathDescr()(req2))(timeout)

      println(res2.utf8String)

    }

    "get the path description with Example 2" in new WithApplication(TestHelper.app) {

      val timeout = Timeout(1 day)
      val controller = TestHelper.inject[controllers.PathController]

      val jsonStr = """
                      {
                        "map":{
                          "rooms": [
                            { "id": 1, "name": "Hallway", "north": 2, "east":7, "objects": [] },
                            { "id": 2, "name": "Dining Room", "north": 5, "south": 1, "west": 3, "east": 4, "objects": [] },
                            { "id": 3, "name": "Kitchen","east":2, "objects": [ { "name": "Knife"} ] },
                            { "id": 4, "name": "Sun Room","west":2, "north":6, "south":7, "objects": [] },
                            { "id": 5, "name": "Bedroom","south":2, "east":6, "objects": [{"name": "Pillow" }] },
                            { "id": 6, "name": "Bathroom","west":5, "south":4, "objects": [] },
                            { "id": 7, "name": "Living room","west":1, "north":4, "objects": [{"name": "Potted Plant" }] }
                          ]
                        },
                        "startId": 4,
                        "objects": [
                          "Knife",
                          "Potted Plant",
                          "Pillow"
                        ]
                      }
      """
      val json = Json.parse(jsonStr)


      val req2 = FakeRequest().withJsonBody(json)
      val res2 = contentAsBytes(controller.createPathDescr()(req2))(timeout)
      println(res2.utf8String)

    }
  }
}
