package models

import play.api.libs.json.{Format, Json}

/**
  * roomObject model
  */
case class RoomObject (name: String)
object RoomObject {
  val jsonFmt: Format[RoomObject] = Json.format[RoomObject]
}

/**
  * Room model
  */
case class Room (id: Int,
                 name: String,
                 north: Option[Int]=None, //referring to a connected room
                 south: Option[Int]=None, //referring to a connected room
                 west: Option[Int]=None,  //referring to a connected room
                 east: Option[Int]=None,  //referring to a connected room
                 objects: List[RoomObject])
object Room {
  implicit val RoomObjectFormat = Json.format[RoomObject]
  val jsonFmt: Format[Room] = Json.format[Room]
}

/**
  * Map model
  */
case class Map (rooms: List[Room])
object Map {
  implicit val RoomObjectFormat = Json.format[RoomObject]
  implicit val RoomFormat = Json.format[Room]
  val jsonFmt: Format[Map] = Json.format[Map]
}

/**
  * InputSet model
  */
case class InputSet (map: Map,
                     startId: Int,
                     objects: List[String])

object InputSet {
  implicit val RoomObjectFormat = Json.format[RoomObject]
  implicit val RoomFormat = Json.format[Room]
  implicit val mapFormat = Json.format[Map]
  val jsonFmt: Format[InputSet] = Json.format[InputSet]
}

/**
  * OutputRepr model
  */
case class OutputRepr (id: Int,
                       room: String,
                       roomObjects: String){
  def toJson = Json.obj("id" -> id, "Room" -> room, "roomObjects" -> roomObjects)
}