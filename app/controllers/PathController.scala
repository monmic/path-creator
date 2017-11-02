package controllers

import javax.inject._

import models.{Map, Room, RoomObject, InputSet, OutputRepr}
import play.api.libs.json.Json
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class PathController @Inject()() extends InjectedController {

  /**
    * Create an Action to render an HTML page with result of Path description.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `POST` request with
    * a path of `/createPathDescr`.
    */
  def createPathDescr() = Action { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        InputSet.jsonFmt.reads(json).fold(
          //errors in INPUT json format
          errors => {
            BadRequest(Json.obj("errors" -> errors.toString()))
          },
          //INPUT json format correct
          data => {
            Ok(printOutputRepr(getOutputRepr(data.map, data.startId, data.objects)))
          }
        )
      case None =>
        BadRequest("No map structure")
    }
  }

  /**
    * Return a list of output representation.
    *
    * @param map input map [[Map]]
    * @param startId start point ID [[Int]]
    * @param objects objects to find [[List[String]]
    * @return list of [[OutputRepr]]
    */
  def getOutputRepr(map: Map, startId: Int, objects: List[String]) : List[OutputRepr] = {
    var output : List[OutputRepr] = List(OutputRepr(-1, "None", "None"))

    if (objects.length == 0) {
      output = List(OutputRepr(-1, "None", "No objects to look for"))
    }
    else if (map.rooms.exists(x => (x.id == startId))) {
      output = executeStep(startId, map, objects, List.empty)
    }

    output
  }

  /**
    * Execute a step of iteration.
    *
    * @param map input map [[Map]]
    * @param passId start point ID [[Int]]
    * @param objectsToFind objects to find [[List[String]]
    * @return list of [[OutputRepr]]
    */
  def executeStep(passId: Int, map: Map, objectsToFind: List[String], output: List[OutputRepr]) : List[OutputRepr] = {
    var objectsToFindFiltered = objectsToFind
    var updatedOutput : List[OutputRepr] = output

    // continue untill all object are found
    if (!objectsToFindFiltered.isEmpty) {
      val room: Room = map.rooms.find(x => (x.id == passId)).get
      val infos = getRoomInfos(room)

      val newRoots = infos._1.filterNot(p => p._2 == -1)
      if (newRoots.length == 0){

      }else {

        val nextPass = newRoots.head
        // check object to find
        val objectsToWrite = infos._2.map(_.name).filter(x => objectsToFindFiltered.contains(x))
        // filter object found from starting list
        objectsToFindFiltered = objectsToFindFiltered.filterNot(u => infos._2.map(_.name).toSet.contains(u))

        val updatedRoom: Room = nextPass._1 match {
          case "N" =>
            room.copy(north = None)
          case "S" =>
            room.copy(south = None)
          case "W" =>
            room.copy(west = None)
          case "E" =>
            room.copy(east = None)
        }

        // update memory structure after doing the i-step
        var newMap = map.copy(rooms = map.rooms.updated(map.rooms.indexOf(room), updatedRoom))
        // add output representation to global list
        updatedOutput = List(OutputRepr(passId, room.name, objsToString(objectsToWrite))) ::: executeStep(nextPass._2, newMap, objectsToFindFiltered, updatedOutput)
      }
    }
    updatedOutput
  }

  /**
    * Return a [[String]] with planned content of objects list.
    *
    * @param objects objects to find [[List[String]]]
    * @return the print [[String]]
    */
  def objsToString(objects: List[String]): String = {
    objects.mkString(", ")
  }

  /**
    * Return a [[Tuple2[List[Tuple2[String,Int]]], List[RoomObject]]] with room reference.
    *
    * @param r room [[Room]]
    * @return the [[Tuple2[List[Tuple2[String,Int]]
    */
  def getRoomInfos(r: Room): Tuple2[List[Tuple2[String,Int]], List[RoomObject]]= {
    (List(("N", r.north.getOrElse(-1)), ("S", r.south.getOrElse(-1)), ("W", r.west.getOrElse(-1)), ("E", r.east.getOrElse(-1))), r.objects)
  }
  /**
    * Return a [[String]] with room reference .
    *
    * @param x list of [[OutputRepr]]
    * @return the [[Tuple2[List[Tuple2[String,Int]]
    */
  def printOutputRepr(x: List[OutputRepr]) : String = {
    var res: String = "----------------------------------\nID  Room          Object collected\n----------------------------------\n"
    x.map { xx =>
      res += s"${xx.id}   ${xx.room}      ${xx.roomObjects}\n"
    }
    res
  }
}
