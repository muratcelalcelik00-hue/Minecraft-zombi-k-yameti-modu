# ut_a: attribute and daylight subject, under open sky.
execute store success score #sa ut run summon minecraft:zombie 0 -59 0 {Tags:["ut_a"],PersistenceRequired:1b}
# ut_b: sense subject, 30 blocks short of where the TNT goes off.
execute store success score #sb ut run summon minecraft:zombie 100 -59 0 {Tags:["ut_b"],PersistenceRequired:1b}
# ut_v: infection subject, 60 blocks from ut_a — outside the zombie's 47 block
# follow range, inside the spawn chunks and inside the forceloaded band.
execute store success score #sv ut run summon minecraft:villager -60 -59 0 {Tags:["ut_v"],PersistenceRequired:1b}
# ut_c: control. If a cow survives where a villager does not, the problem is the
# villager; if neither is there, it is the spot or the summon itself.
execute store success score #sc ut run summon minecraft:cow -60 -59 3 {Tags:["ut_c"],PersistenceRequired:1b}
say UNDYING-TEST spawned
