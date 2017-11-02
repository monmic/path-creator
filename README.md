# path-creator

Create a valid route one could follow to collect all specified items within a map:

{
"rooms": [
       { "id": 1, "name": "Hallway", "north": 2, "objects": [] },
       { "id": 2, "name": "Dining Room", "south": 1, "west": 3, "east": 4, "objects": [] },
       { "id": 3, "name": "Kitchen","east":2, "objects": [ { "name": "Knife" } ] },
       { "id": 4, "name": "Sun Room","west":2, "objects": [ { "name": "Potted Plant" } ] }
     ]
}

Other input are the start ID linked to a room and the list of items to collect.

Example of input set:

{
  "map": {
    "rooms": [
    { "id": 1, "name": "Hallway", "north": 2, "objects": [] },
           { "id": 2, "name": "Dining Room", "south": 1, "west": 3, "east": 4, "objects": [] },
           { "id": 3, "name": "Kitchen","east":2, "objects": [ { "name": "Knife" } ] },
           { "id": 4, "name": "Sun Room","west":2, "objects": [ { "name": "Potted Plant" } ] }
    ]
  },
  "startId": 2,
  "objects": ["Knife", "Potted Plant"]
}
