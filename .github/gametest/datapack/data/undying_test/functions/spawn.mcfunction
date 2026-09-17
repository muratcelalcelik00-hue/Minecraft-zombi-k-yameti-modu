# ut_a: attribute and daylight subject, under open sky.
summon minecraft:zombie 0 -59 0 {Tags:["ut_a"],PersistenceRequired:1b}
# ut_b: sense subject, 30 blocks short of where the TNT goes off.
summon minecraft:zombie 100 -59 0 {Tags:["ut_b"],PersistenceRequired:1b}
# ut_v: infection subject, 60 blocks from ut_a — outside the zombie's 47 block
# follow range, inside the spawn chunks and inside the forceloaded band.
summon minecraft:villager -60 -59 0 {Tags:["ut_v"],PersistenceRequired:1b}
say UNDYING-TEST spawned
